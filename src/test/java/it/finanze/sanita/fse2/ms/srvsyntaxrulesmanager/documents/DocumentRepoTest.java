package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.documents;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractDatabaseHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.ComponentScan.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED;

@DataMongoTest
@ComponentScans( value = {
    @ComponentScan(CONFIG_MONGO),
    @ComponentScan(REPOSITORY),
    @ComponentScan(UTILS)
})
@ActiveProfiles(Constants.Profile.TEST)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentRepoTest extends AbstractDatabaseHandler {

    @SpyBean
    private MongoTemplate mongo;
    @Autowired
    private IDocumentRepo repository;

    @BeforeAll
    public void setup() throws IOException, OperationException {
        this.setupTestRepository();
    }

    @Test
    void findDocById() throws OperationException {
        // Retrieve some entities
        List<SchemaETY> entities = repository.findDocsByExtensionId(SCHEMA_TEST_EXTS_A);
        // Pick one randomly
        SchemaETY toGet = entities.get(0);
        // Test
        assertDoesNotThrow(() -> {
            // Retrieve
            SchemaETY entity = repository.findDocById(toGet.getId());
            // Assert equality
            assertEquals(toGet, entity);
        });
    }

    @Test
    void findDocByIdException() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).findOne(any(), eq(SchemaETY.class));
        // Verify exception
        assertThrows(OperationException.class, () -> repository.findDocById(FAKE_VALID_DTO_ID));
    }

    @Test
    void isExtensionInserted() throws OperationException {
        // This extension is already available by default
        assertTrue(repository.isExtensionInserted(SCHEMA_TEST_EXTS_A));
        // This extension is completely made up
        assertFalse(repository.isExtensionInserted(SCHEMA_TEST_FAKE_EXTS));
    }

    @Test
    void isExtensionInsertedException() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).exists(any(), eq(SchemaETY.class));
        // Verify exception
        assertThrows(OperationException.class, () -> repository.isExtensionInserted(SCHEMA_TEST_EXTS_A));
    }

    @Test
    void findDocsWithValidExtension() throws OperationException {
        // Retrieve entities
        List<SchemaETY> entities = repository.findDocsByExtensionId(SCHEMA_TEST_EXTS_A);
        // Assert size
        assertEquals(
            getReadOnlyEntities().size(), entities.size()
        );
        // Assert equality
        for (SchemaETY entity : entities) {
            assertEquals(
                getReadOnlyEntities().get(entity.getNameSchema()), entity
            );
        }
    }

    @Test
    void findDocsWithValidExtensionException() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).find(any(), eq(SchemaETY.class));
        // Verify exception
        assertThrows(OperationException.class, () -> repository.findDocsByExtensionId(SCHEMA_TEST_EXTS_A));
    }

    @Test
    void findDocsWithInvalidExtension() throws OperationException {
        // Retrieve entities
        List<SchemaETY> entities = repository.findDocsByExtensionId(SCHEMA_TEST_FAKE_EXTS);
        // Assert size
        assertTrue(entities.isEmpty());
    }

    @Test
    void isDocumentInsertedWithTestEntities() throws OperationException {
        // Filenames holder
        List<String> toFind = new ArrayList<>(getReadOnlyEntities().keySet()).subList(0, 3);
        // Retrieve entities
        Map<String, SchemaETY> entities0 = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_A, toFind
        );
        // Assert existence
        for (String filename : toFind) {
            assertTrue(entities0.containsKey(filename));
        }
    }

    @Test
    void isDocumentInsertedWithTestEntitiesException() {
        // Filenames holder
        List<String> toFind = new ArrayList<>(getReadOnlyEntities().keySet()).subList(0, 3);
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).find(any(), eq(SchemaETY.class));
        // Verify exception
        assertThrows(OperationException.class, () -> repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_A, toFind
        ));
    }

    @Test
    void isDocumentInsertedWithFakeEntities() throws OperationException {
        // Fake filename holder
        List<String> toFindFake = new ArrayList<>();
        // Populate (none exists)
        toFindFake.add("ks.xsd");
        toFindFake.add("gb.xsd");
        // Retrieve entities
        Map<String, SchemaETY> entities = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_A, toFindFake
        );
        // Assert size
        assertTrue(entities.isEmpty());
        // Populate again
        toFindFake.add(SCHEMA_TEST_ROOT);
        // Retrieve entities
        entities = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_A, toFindFake
        );
        // Assert existence
        for (String filename : toFindFake) {
            if(filename.equals(SCHEMA_TEST_ROOT)) {
                assertTrue(entities.containsKey(filename));
            }else{
                assertFalse(entities.containsKey(filename));
            }
        }
    }

    @Test
    void isDocumentInsertedWithMixedEntities() throws OperationException {
        // Fake filename holder
        List<String> toFindMixed = new ArrayList<>();
        // Populate (none exists)
        toFindMixed.add("ks.xsd");
        toFindMixed.add("gb.xsd");
        toFindMixed.add(SCHEMA_TEST_ROOT);
        // Retrieve entities
        Map<String, SchemaETY> entities = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_A, toFindMixed
        );
        // Assert size
        assertEquals(1, entities.size());
        // Assert existence
        for (String filename : toFindMixed) {
            if(filename.equals(SCHEMA_TEST_ROOT)) {
                assertTrue(entities.containsKey(filename));
            }else{
                assertFalse(entities.containsKey(filename));
            }
        }
    }

    @Test
    void insertDocsByExtensionId() throws OperationException {
        // Insert test entities B
        List<SchemaETY> entities = repository.insertDocsByExtensionId(
            getEntitiesToUpload()
        );
        // Convert to map
        Map<String, SchemaETY> inserted = entities.stream().collect(
            Collectors.toMap(SchemaETY::getNameSchema, entity -> entity)
        );
        // Assert size
        assertEquals(entities.size(), inserted.size());
        // Assertions
        for (SchemaETY entity : entities) {
            // Get filename
            String filename = entity.getNameSchema();
            // Verify file exists
            assertTrue(inserted.containsKey(filename));
            // Verify equality
            assertSame(inserted.get(filename), entity);
        }
    }

    @Test
    void insertDocsByExtensionIdExceptions() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).insertAll(anyList());
        // Verify exception
        assertThrows(OperationException.class, () -> repository.insertDocsByExtensionId(
            getEntitiesToUpload()
        ));
    }

    @Test
    void updateDocsByExtensionId() throws OperationException, DataIntegrityException {
        // Working vars
        Map<String, SchemaETY> newest = getEntitiesToUseAsReplacement();
        Map<SchemaETY, SchemaETY> entities = new HashMap<>();
        // Documents to modify
        List<String> filenames = new ArrayList<>(getEntitiesToUseAsReplacement().keySet());
        Map<String, SchemaETY> current = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_B,
            filenames
        );
        // Modify data
        current.forEach((name, entity) -> {
            entities.put(entity, newest.get(name));
        });
        // Replace test entities content
        repository.updateDocsByExtensionId(entities);
        // Retrieve new documents
        Map<String, SchemaETY> newFiles = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_B,
            filenames
        );
        // Assert size
        assertEquals(newFiles.size(), current.size());
        // Assertions
        for (String filename : filenames) {
            // Get documents instance (before and after)
            SchemaETY newFile = newFiles.get(filename);
            SchemaETY oldFile = current.get(filename);
            // Verify file exists
            assertNotNull(newFile);
            assertNotNull(oldFile);
            // Verify they are not equals
            assertNotSame(current.get(filename), newFiles.get(filename));
            // Verify content is not the same
            assertNotEquals(oldFile.getContentSchema(), newFile.getContentSchema());
            // Verify timestamp is not the same
            assertNotEquals(oldFile.getLastUpdateDate(), newFile.getLastUpdateDate());
        }
    }

    @Test
    void updateDocsByExtensionIdExceptions() throws OperationException {
        // Working vars
        Map<String, SchemaETY> newest = getEntitiesToUseAsReplacement();
        Map<SchemaETY, SchemaETY> entities = new HashMap<>();
        BulkOperations ops = Mockito.spy(BulkOperations.class);
        // Documents to modify
        List<String> filenames = new ArrayList<>(getEntitiesToUseAsReplacement().keySet());
        // Retrieve old documents
        Map<String, SchemaETY> current = repository.isDocumentInserted(
            SCHEMA_TEST_EXTS_B,
            filenames
        );
        // Modify data
        current.forEach((name, entity) -> {
            entities.put(entity, newest.get(name));
        });
        // Provide knowledge
        doReturn(ops).when(mongo).bulkOps(UNORDERED, SchemaETY.class);
        doThrow(new MongoException("test")).when(ops).execute();
        // Verify exception
        assertThrows(OperationException.class, () -> repository.updateDocsByExtensionId(
            entities
        ));
    }

    @Test
    void deleteDocsWithValidExtension() throws OperationException, DataIntegrityException {
        // Remove collection entities
        List<SchemaETY> entities = repository.deleteDocsByExtensionId(SCHEMA_TEST_EXTS_C);
        // Convert to map
        Map<String, SchemaETY> removed = entities.stream().collect(
            Collectors.toMap(SchemaETY::getNameSchema, entity -> entity)
        );
        // Assert size
        assertEquals(entities.size(), removed.size());
        // Assertions
        for (SchemaETY entity : entities) {
            // Get filename
            String filename = entity.getNameSchema();
            // Verify file exists
            assertTrue(removed.containsKey(filename));
            // Verify equality
            assertSame(removed.get(filename), entity);
        }
    }

    @Test
    void deleteDocsWithValidExtensionException() {
        // Provide knowledge
        doThrow(new MongoException("Test")).when(mongo).updateMulti(any(),any(), eq(SchemaETY.class));
        // Verify exception
        assertThrows(OperationException.class, () -> repository.deleteDocsByExtensionId(
            SCHEMA_TEST_FAKE_EXTS
        ));
    }

    @Test
    void deleteDocsWithInvalidExtension() throws OperationException, DataIntegrityException {
        // Remove collection entities
        List<SchemaETY> entities = repository.deleteDocsByExtensionId(SCHEMA_TEST_FAKE_EXTS);
        // Assert size
        assertTrue(entities.isEmpty());
    }

    @AfterAll
    public void teardown() {
        this.clearTestRepository();
    }
}
