package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.changeset;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractDatabaseHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsMisc;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.ComponentScan.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@DataMongoTest
@ComponentScans( value = {
    @ComponentScan(CONFIG_MONGO),
    @ComponentScan(REPOSITORY),
    @ComponentScan(UTILS)
})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChangeSetRepoTest extends AbstractDatabaseHandler {

    @SpyBean
    private MongoTemplate mongo;
    @Autowired
    private IChangeSetRepo<SchemaETY> repository;
    @Autowired
    private IDocumentRepo documents;
    private Date lastUpdate;

    @BeforeAll
    public void setup() throws IOException, OperationException {
        this.setupTestRepository();
        this.lastUpdate = new Date();
    }

    @Test
    void getInsertions() throws OperationException {
        // Retrieve documents with the current timestamp
        List<SchemaETY> insertions = repository.getInsertions(new Date());
        // Expect nothing
        assertTrue(insertions.isEmpty());
    }

    @Test
    void getInsertionsWithTimestamp() throws OperationException {
        // Retrieve documents with yesterday date
        List<SchemaETY> insertions = repository.getInsertions(UtilsMisc.getYesterday());
        // Expect full match
        assertEquals(SCHEMA_INTO_DB, insertions.size());
    }

    @Test
    void getInsertionsExceptions() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).find(any(), eq(SchemaETY.class));
        // Expect error
        assertThrows(OperationException.class, () -> repository.getInsertions(new Date()));
    }

    @Test
    void getModifications() throws OperationException {
        // Retrieve documents with the current timestamp
        List<SchemaETY> modifications = repository.getModifications(new Date());
        // Expect nothing
        assertTrue(modifications.isEmpty());
    }

    @Test
    void getModificationsWithTimestamp() throws OperationException, DataIntegrityException {
        // Documents to modify
        List<String> filenames = new ArrayList<>(getEntitiesToUseAsReplacement().keySet());
        // Retrieve old documents
        Map<String, SchemaETY> oldFiles = documents.isDocumentInserted(
            SCHEMA_TEST_EXTS_B,
            filenames
        );
        // Modify data
        oldFiles.forEach((name, entity) -> {
            // Assign their ids to the new ones
            getEntitiesToUseAsReplacement().get(name).setId(entity.getId());
            // Create new timestamp
            getEntitiesToUseAsReplacement().get(name).setLastUpdateDate(
                Date.from(Instant.now().plus(5, ChronoUnit.MINUTES))
            );
        });
        // Replace test entities content
        documents.updateDocsByExtensionId(
            new ArrayList<>(getEntitiesToUseAsReplacement().values())
        );
        // Retrieve documents with last update
        List<SchemaETY> modifications = repository.getModifications(lastUpdate);
        // Expect size match
        assertEquals(getEntitiesToUseAsReplacement().values().size(), modifications.size());
    }

    @Test
    void getModificationsExceptions() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).find(any(), eq(SchemaETY.class));
        // Expect error
        assertThrows(OperationException.class, () -> repository.getModifications(new Date()));
    }

    @Test
    void getDeletions() throws OperationException {
        // Retrieve documents with current timestamp
        List<SchemaETY> deletions = repository.getDeletions(new Date());
        // Expect nothing
        assertTrue(deletions.isEmpty());
    }

    @Test
    void getDeletionsWithTimestamp() throws OperationException, DataIntegrityException {
        // Delete one collection
        documents.deleteDocsByExtensionId(SCHEMA_TEST_EXTS_C);
        // Retrieve documents with lastUpdate
        List<SchemaETY> deletions = repository.getDeletions(lastUpdate);
        // Expect full match
        assertEquals(SCHEMA_TEST_SIZE, deletions.size());
    }

    @Test
    void getDeletionsExceptions() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).find(any(), eq(SchemaETY.class));
        // Expect error
        assertThrows(OperationException.class, () -> repository.getDeletions(new Date()));
    }

    @Test
    void getEveryActiveDocument() throws OperationException {
        // Retrieve documents with lastUpdate
        List<SchemaETY> documents = repository.getEveryActiveDocument();
        // We could calculate it, but I am leaving it on the empty check
        // due to getDeletionsTest could run before or after it
        // Expect not blank
        assertFalse(documents.isEmpty());
    }

    @Test
    void getEveryActiveDocumentExceptions() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).find(any(), eq(SchemaETY.class));
        // Expect error
        assertThrows(OperationException.class, () -> repository.getEveryActiveDocument());
    }

    @AfterAll
    public void teardown() {
        this.clearTestRepository();
    }
}
