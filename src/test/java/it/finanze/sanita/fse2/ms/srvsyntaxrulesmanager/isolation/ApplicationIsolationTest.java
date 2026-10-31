package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation;

import brave.Tracer;
import brave.Span;
import com.mongodb.MongoException;
import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.impl.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.impl.DocumentSRV;
import org.bson.Document;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.isolation.Fixtures.*;

@Tag("ISOLATION")
@org.springframework.test.context.ActiveProfiles("ISOLATION")
@SpringJUnitConfig(IsolationContext.class)
class ApplicationIsolationTest {
    @Autowired MongoTemplate mongo;
    @Autowired EdsDocumentsCTL documents;
    @Autowired ChangeSetCTL changes;
    @Autowired DocumentSRV service;
    @Autowired Tracer tracer;
    SchemaETY entity;

    @BeforeEach void setup() throws Exception {
        reset(mongo);
        entity = entity();
        when(mongo.find(any(Query.class), eq(SchemaETY.class))).thenReturn(Collections.singletonList(entity));
        when(mongo.findOne(any(Query.class), eq(SchemaETY.class))).thenReturn(entity);
        when(mongo.insertAll(anyCollection())).thenAnswer(i -> i.getArgument(0));
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(SchemaETY.class)))
            .thenReturn(UpdateResult.acknowledged(1, 1L, null));
    }

    @Test void fullCrudFlowPreservesContentAndCounts() throws Exception {
        assertThat(documents.uploadDocuments("root", "ext", files()).getInsertedItems()).isEqualTo(1);
        when(mongo.exists(any(Query.class), eq(SchemaETY.class))).thenReturn(true);
        assertThat(documents.updateDocuments("root.xsd", "ext", files(), null).getUpdatedItems()).isEqualTo(1);
        assertThat(documents.patchDocuments("ext", files(), null).getUpdatedItems()).isEqualTo(1);
        assertThat(documents.getDocumentById(ID).getDocument().getContentSchema())
            .isEqualTo(Base64.getEncoder().encodeToString(XSD.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        assertThat(documents.getDocumentsByExtension("ext", false, false).getItems().get(0).getContent().get(0).getContentSchema()).isNull();
        assertThat(documents.getDocumentsByExtension("ext", true, true).getItems().get(0).getNameRoot()).isEqualTo("root.xsd");
        assertThat(documents.deleteDocuments("ext").getDeletedItems()).isEqualTo(1);
        verify(mongo, times(3)).insertAll(anyCollection());
    }

    @Test void groupedDocumentsAndChangesetUseRealMappings() throws Exception {
        ExtensionETY ext = new ExtensionETY(); ext.setId("ext"); ext.setItems(Collections.singletonList(entity));
        when(mongo.aggregate(any(TypedAggregation.class), eq(ExtensionETY.class)))
            .thenReturn(new AggregationResults<>(Collections.singletonList(ext), new Document()));
        assertThat(documents.getAllDocuments(true, false).getNumberOfItems()).isEqualTo(1);
        assertThat(documents.getAllDocuments(false, true).getItems().get(0).getContent().get(0).getContentSchema()).isNull();
        when(mongo.count(any(Query.class), eq(SchemaETY.class))).thenReturn(7L);
        assertThat(changes.changeSet(null).getDeletions()).isEmpty();
        assertThat(changes.changeSet(null).getInsertions().get(0).getDescription().getFilename()).isEqualTo("root.xsd");
        assertThat(changes.changeSet(new Date(1)).getTotalNumberOfElements()).isEqualTo(2);
        assertThat(changes.changeSet(new Date(1)).getCollectionSize()).isEqualTo(7);
    }

    @Test void traceContextIsPropagated() throws Exception {
        Span span = tracer.nextSpan().start();
        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            assertThat(documents.getDocumentById(ID).getTraceID()).isEqualTo(span.context().traceIdString());
            assertThat(changes.changeSet(null).getSpanID()).isEqualTo(span.context().spanIdString());
        } finally { span.finish(); }
        assertThat(new LivenessCheckCTL().health().getStatus().getCode()).isEqualTo("UP");
    }

    @Test void invalidControllerInputsDoNotReachDatabase() {
        for (MultipartFile[] invalid : Arrays.asList(null, new MultipartFile[0], new MultipartFile[]{null},
            new MultipartFile[]{file("bad.txt", XSD)}, new MultipartFile[]{file("empty.xsd", "")})) {
            assertThatThrownBy(() -> documents.uploadDocuments("root.xsd", "ext", invalid)).isInstanceOf(InvalidContentException.class);
            assertThatThrownBy(() -> documents.updateDocuments("root.xsd", "ext", invalid, null)).isInstanceOf(InvalidContentException.class);
            assertThatThrownBy(() -> documents.patchDocuments("ext", invalid, null)).isInstanceOf(InvalidContentException.class);
        }
        assertThatThrownBy(() -> documents.uploadDocuments("root.txt", "ext", files())).isInstanceOf(RootNotValidException.class);
        verifyNoInteractions(mongo);
    }

    @Test void serviceRejectsMissingRootsAndExtensionsAndDocuments() throws Exception {
        assertThatThrownBy(() -> service.insertDocsByExtensionId("missing", "ext", files())).isInstanceOf(RootNotValidException.class);
        assertThatThrownBy(() -> service.updateDocsByExtensionId("missing", "ext", files())).isInstanceOf(RootNotValidException.class);
        assertThatThrownBy(() -> service.updateDocsByExtensionId("root.xsd", "ext", files())).isInstanceOf(ExtensionNotFoundException.class);
        assertThatThrownBy(() -> service.patchDocsByExtensionId("ext", files())).isInstanceOf(ExtensionNotFoundException.class);
        assertThatThrownBy(() -> service.deleteDocsByExtensionId("ext")).isInstanceOf(ExtensionNotFoundException.class);
        when(mongo.exists(any(Query.class), eq(SchemaETY.class))).thenReturn(true);
        assertThatThrownBy(() -> service.insertDocsByExtensionId("root.xsd", "ext", files())).isInstanceOf(ExtensionAlreadyExistsException.class);
        entity.setRootSchema(false);
        assertThatThrownBy(() -> service.patchDocsByExtensionId("ext", files())).isInstanceOf(DocumentNotFoundException.class);
        when(mongo.findOne(any(Query.class), eq(SchemaETY.class))).thenReturn(null);
        when(mongo.find(any(Query.class), eq(SchemaETY.class))).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> service.findDocById(ID)).isInstanceOf(DocumentNotFoundException.class);
        assertThatThrownBy(() -> service.findDocsByExtensionId("ext", false)).isInstanceOf(ExtensionNotFoundException.class);
    }

    @Test void patchCanAddAFileAndRetainsRootFlag() throws Exception {
        when(mongo.exists(any(Query.class), eq(SchemaETY.class))).thenReturn(true);
        when(mongo.find(any(Query.class), eq(SchemaETY.class))).thenReturn(Collections.singletonList(entity), Collections.emptyList());
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(SchemaETY.class))).thenReturn(UpdateResult.acknowledged(0, 0L, null));
        assertThat(service.patchDocsByExtensionId("ext", new MultipartFile[]{file("extra.xsd", XSD)})).isEqualTo(1);
        org.mockito.ArgumentCaptor<Collection> inserted = org.mockito.ArgumentCaptor.forClass(Collection.class);
        verify(mongo).insertAll(inserted.capture());
        SchemaETY added = (SchemaETY) inserted.getValue().iterator().next();
        assertThat(added.getRootSchema()).isFalse();
        assertThat(added.getNameSchema()).isEqualTo("extra.xsd");
    }

    @Test void malformedSchemasAndDatabaseFailuresPropagate() throws Exception {
        assertThatThrownBy(() -> documents.uploadDocuments("root.xsd", "ext", new MultipartFile[]{file("root.xsd", "bad")})).isInstanceOf(SchemaValidatorException.class);
        when(mongo.findOne(any(Query.class), eq(SchemaETY.class))).thenThrow(new MongoException("offline"));
        assertThatThrownBy(() -> documents.getDocumentById(ID)).isInstanceOf(OperationException.class).hasCauseInstanceOf(MongoException.class);
    }
}
