package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.impl.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation.Fixtures.*;

@Tag("ISOLATION")
@org.springframework.test.context.ActiveProfiles("ISOLATION")
@SpringJUnitConfig(IsolationContext.class)
class RepositoryIsolationTest {
    @Autowired MongoTemplate mongo;
    @Autowired DocumentRepo repo;
    @Autowired ChangeSetRepo changes;
    @BeforeEach void resetBoundary() { reset(mongo); }

    @Test void queriesEnforceExtensionDeletionAndDateBoundaries() throws Exception {
        when(mongo.find(any(Query.class), eq(SchemaETY.class))).thenReturn(Collections.emptyList());
        repo.findDocsByExtensionId("ext", false);
        repo.findDocsByExtensionId("ext", true);
        repo.findExistingDocumentsByExtensionAndFilenames("ext", Arrays.asList("a.xsd", "b.xsd"));
        changes.getInsertions(new Date(123));
        changes.getDeletions(new Date(123));
        changes.getEveryActiveDocument();
        org.mockito.ArgumentCaptor<Query> q = org.mockito.ArgumentCaptor.forClass(Query.class);
        verify(mongo, times(6)).find(q.capture(), eq(SchemaETY.class));
        List<Query> queries = q.getAllValues();
        assertThat(queries.get(0).getQueryObject()).containsEntry("type_id_extension", "ext").containsEntry("deleted", false);
        assertThat(queries.get(1).getQueryObject()).doesNotContainKey("deleted");
        assertThat(queries.get(2).getQueryObject().toJson()).contains("$in", "a.xsd", "b.xsd");
        assertThat(queries.get(3).getQueryObject().toJson()).contains("$gt", "$ne");
        assertThat(queries.get(4).getQueryObject().toJson()).contains("$gt", "$lte", "true");
        assertThat(queries.get(5).getQueryObject().toJson()).contains("$ne");
    }

    @Test void allMongoReadAndInsertFailuresBecomeCheckedOperationErrors() {
        MongoException failure = new MongoException("offline");
        when(mongo.find(any(Query.class), eq(SchemaETY.class))).thenThrow(failure);
        when(mongo.exists(any(Query.class), eq(SchemaETY.class))).thenThrow(failure);
        when(mongo.count(any(Query.class), eq(SchemaETY.class))).thenThrow(failure);
        when(mongo.insertAll(anyCollection())).thenThrow(failure);
        when(mongo.aggregate(any(TypedAggregation.class), eq(ExtensionETY.class))).thenThrow(failure);
        org.assertj.core.api.ThrowableAssert.ThrowingCallable[] operations = {
            () -> repo.findDocsByExtensionId("ext", false), () -> repo.isExtensionInserted("ext"),
            () -> repo.getInsertedDocumentsByExtension("ext"), () -> repo.insertDocsByExtensionId(Collections.emptyList()),
            () -> repo.findExistingDocumentsByExtensionAndFilenames("ext", Collections.emptyList()),
            () -> repo.groupByExtension(false), () -> changes.getInsertions(new Date()),
            () -> changes.getDeletions(new Date()), () -> changes.getEveryActiveDocument(), () -> changes.getActiveDocumentCount()
        };
        for (org.assertj.core.api.ThrowableAssert.ThrowingCallable operation : operations)
            assertThatThrownBy(operation).isInstanceOf(OperationException.class).hasCause(failure);
    }

    @Test void logicalDeletionChecksBothMatchedAndModifiedCounts() throws Exception {
        when(mongo.find(any(Query.class), eq(SchemaETY.class))).thenReturn(Collections.singletonList(entity()));
        for (long[] counts : new long[][]{{0, 0}, {1, 0}}) {
            when(mongo.updateMulti(any(Query.class), any(Update.class), eq(SchemaETY.class)))
                .thenReturn(UpdateResult.acknowledged(counts[0], counts[1], null));
            assertThatThrownBy(() -> repo.deleteDocsByExtensionId("ext")).isInstanceOf(DataIntegrityException.class);
            assertThatThrownBy(() -> repo.deleteDocsByExtensionIdAndFilenames("ext", Collections.singletonList("root.xsd"))).isInstanceOf(DataIntegrityException.class);
        }
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(SchemaETY.class))).thenThrow(new MongoException("offline"));
        assertThatThrownBy(() -> repo.deleteDocsByExtensionId("ext")).isInstanceOf(OperationException.class);
        assertThatThrownBy(() -> repo.deleteDocsByExtensionIdAndFilenames("ext", Collections.singletonList("root.xsd"))).isInstanceOf(OperationException.class);
    }

    @Test void bulkReplacementMarksOldVersionDeletedAndInsertsNewVersion() throws Exception {
        BulkOperations bulk = mock(BulkOperations.class);
        when(mongo.bulkOps(BulkOperations.BulkMode.UNORDERED, SchemaETY.class)).thenReturn(bulk);
        BulkWriteResult result = mock(BulkWriteResult.class);
        when(bulk.execute()).thenReturn(result);
        SchemaETY old = entity(), newest = entity();
        Map<SchemaETY, SchemaETY> map = Collections.singletonMap(old, newest);
        when(result.getMatchedCount()).thenReturn(1);
        when(result.getModifiedCount()).thenReturn(1);
        assertThat(repo.updateDocsByExtensionId(map)).containsExactly(newest);
        org.mockito.ArgumentCaptor<Update> update = org.mockito.ArgumentCaptor.forClass(Update.class);
        verify(bulk).updateOne(any(Query.class), update.capture());
        assertThat(update.getValue().getUpdateObject().toJson()).contains("deleted", "true", "last_update");
        verify(bulk).insert(newest);
        when(result.getMatchedCount()).thenReturn(0);
        assertThatThrownBy(() -> repo.updateDocsByExtensionId(map)).isInstanceOf(DataIntegrityException.class);
        when(result.getMatchedCount()).thenReturn(1);
        when(result.getModifiedCount()).thenReturn(0);
        assertThatThrownBy(() -> repo.updateDocsByExtensionId(map)).isInstanceOf(DataIntegrityException.class);
        when(bulk.execute()).thenThrow(new MongoException("offline"));
        assertThatThrownBy(() -> repo.updateDocsByExtensionId(map)).isInstanceOf(OperationException.class);
    }
}
