package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.documents;

import brave.Tracer;
import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IDocumentSRV;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@WebMvcTest(IDocumentSRV.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentSRVTest extends AbstractEntityHandler {

    @MockBean
    private Tracer tracer;
    @MockBean
    private IDocumentRepo repository;
    @Autowired
    private IDocumentSRV service;

    @BeforeAll
    public void setup() throws IOException {
        this.setupTestEntities();
    }

    @Test
    void findDocWithValidId() throws OperationException {
        // Providing mock knowledge
        when(repository.findDocById(anyString()))
            .thenReturn(getEntitiesToUpload().get(0));
        // Verify exception is not thrown
        assertDoesNotThrow(() -> {
            service.findDocById(FAKE_VALID_DTO_ID);
        });
    }

    @Test
    void findDocWithInvalidId() {
        // Verify exception is thrown
        assertThrows(ObjectIdNotValidException.class, () -> {
            service.findDocById(FAKE_INVALID_DTO_ID);
        });
    }

    @Test
    void findDocWithNotFoundDocument() throws OperationException {
        // Providing mock knowledge
        when(repository.findDocById(anyString())).thenReturn(null);
        // Verify exception is thrown
        assertThrows(DocumentNotFoundException.class, () -> {
            service.findDocById(FAKE_VALID_DTO_ID);
        });
    }

    @Test
    void findDocsWithInvalidExtension() throws OperationException {
        // Providing mock knowledge
        when(repository.findDocsByExtensionId(anyString()))
            .thenReturn(new ArrayList<>());
        // Verify exception is thrown
        assertThrows(ExtensionNotFoundException.class, () -> {
            service.findDocsByExtensionId(SCHEMA_TEST_FAKE_EXTS);
        });
    }

    @Test
    void findDocsWithValidExtension() throws OperationException {
        // Providing mock knowledge
        when(repository.findDocsByExtensionId(SCHEMA_TEST_EXTS_A))
            .thenReturn(new ArrayList<>(getReadOnlyEntities().values()));
        // Verify exception is not thrown
        assertDoesNotThrow(() -> {
            service.findDocsByExtensionId(SCHEMA_TEST_EXTS_A);
        });
    }

    @Test
    void insertDocsWithValidExtension() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_D)).thenReturn(false);
        when(repository.insertDocsByExtensionId(anyList())).thenReturn(getEntitiesToUpload());
        // Verify exception is not thrown
        assertDoesNotThrow(() -> {
            service.insertDocsByExtensionId(
                SCHEMA_TEST_ROOT_B,
                SCHEMA_TEST_EXTS_D,
                new MockMultipartFile[]{
                    new MockMultipartFile(
                        SCHEMA_TEST_ROOT_B,
                        SCHEMA_TEST_ROOT_B,
                        "application/xsd",
                        new byte[0]
                    )
                }
            );
        });
    }

    @Test
    void insertDocsWithRootNotValid() {
        assertThrows(RootNotValidException.class, () -> {
            service.insertDocsByExtensionId(
                SCHEMA_TEST_ROOT_B,
                SCHEMA_TEST_EXTS_D,
                new MockMultipartFile[]{
                    new MockMultipartFile(
                        "invalid_root.xsd",
                        "invalid_root.xsd",
                        "application/xsd",
                        new byte[0]
                    )
                }
            );
        });
    }

    @Test
    void insertDocsWithOperationError() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_D)).thenReturn(false);
        when(repository.insertDocsByExtensionId(anyList())).thenThrow(
            new OperationException(
                "Something exploded",
                new MongoException("Everything blew up")
            )
        );
        // Verify exception is not thrown
        assertThrows(OperationException.class, () -> {
            service.insertDocsByExtensionId(
                SCHEMA_TEST_ROOT_B,
                SCHEMA_TEST_EXTS_D,
                new MockMultipartFile[]{
                    new MockMultipartFile(
                        SCHEMA_TEST_ROOT_B,
                        SCHEMA_TEST_ROOT_B,
                        "application/xsd",
                        new byte[0]
                    )
                }
            );
        });
    }

    @Test
    void insertDocsWithInvalidData() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_D)).thenReturn(false);
        when(repository.insertDocsByExtensionId(anyList())).thenReturn(getEntitiesToUpload());
        try(MockedStatic<SchemaETY> mock = mockStatic(SchemaETY.class)) {
            // Providing mock knowledge
            mock.when(() -> SchemaETY.fromMultipart(
                any(),
                anyString(),
                anyBoolean()
            )).thenThrow(
                new DataProcessingException("Unable to convert raw data", new IOException())
            );
           // Verify exception is thrown
           assertThrows(DataProcessingException.class, () -> {
               service.insertDocsByExtensionId(
                   SCHEMA_TEST_ROOT_B,
                   SCHEMA_TEST_EXTS_D,
                   new MockMultipartFile[]{
                       new MockMultipartFile(
                           SCHEMA_TEST_ROOT_B,
                           SCHEMA_TEST_ROOT_B,
                           "application/xsd",
                           new byte[0]
                       )
                   }
               );
            });
        }
    }

    @Test
    void updateWithValidExtension() throws OperationException, DataIntegrityException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_B)).thenReturn(true);
        when(repository.isDocumentInserted(anyString(), any())).thenReturn(getEntitiesToUseAsReplacement());
        when(repository.updateDocsByExtensionId(anyList())).thenReturn(any());
        // Verify exception is thrown
        assertDoesNotThrow(() -> {
            service.updateDocsByExtensionId(
                SCHEMA_TEST_EXTS_B,
                new MockMultipartFile[]{
                    new MockMultipartFile(
                        "datatypes.xsd",
                        "datatypes.xsd",
                        MediaType.APPLICATION_XML.toString(),
                        new byte[0]
                    ),
                    new MockMultipartFile(
                        "datatypes-base.xsd",
                        "datatypes-base.xsd",
                        MediaType.APPLICATION_XML.toString(),
                        new byte[0]
                    ),
                }
            );
        });
    }

    @Test
    void updateWithInvalidExtension() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_C)).thenReturn(false);
        // Verify exception is thrown
        assertThrows(ExtensionNotFoundException.class, () -> {
            service.updateDocsByExtensionId(
                SCHEMA_TEST_EXTS_C,
                new MockMultipartFile[]{
                    new MockMultipartFile(SCHEMA_TEST_ROOT_B, new byte[0])
                }
            );
        });
    }

    @Test
    void updateWithInvalidFile() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_B)).thenReturn(true);
        when(repository.isDocumentInserted(anyString(), any())).thenReturn(new HashMap<>());
        // Verify exception is thrown
        assertThrows(DocumentNotFoundException.class, () -> {
            service.updateDocsByExtensionId(
                SCHEMA_TEST_EXTS_B,
                new MockMultipartFile[]{
                    new MockMultipartFile(SCHEMA_TEST_ROOT_B, new byte[0])
                }
            );
        });
    }

    @Test
    void updateWithInvalidDataFile() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_B)).thenReturn(true);
        when(repository.isDocumentInserted(anyString(), any())).thenReturn(new HashMap<>());
        try(MockedStatic<SchemaETY> mock = mockStatic(SchemaETY.class)) {
            // Providing mock knowledge
            mock.when(() -> SchemaETY.fromMultipart(
                any(),
                anyString(),
                anyBoolean()
            )).thenThrow(
                new DataProcessingException("Unable to convert raw data", new IOException())
            );
            // Verify exception is thrown
            assertThrows(DocumentNotFoundException.class, () -> {
                service.updateDocsByExtensionId(
                    SCHEMA_TEST_EXTS_B,
                    new MockMultipartFile[]{
                        new MockMultipartFile(SCHEMA_TEST_ROOT_B, new byte[0])
                    }
                );
            });
        }
    }

    @Test
    void deleteDocsWithValidExtension() throws OperationException, DataIntegrityException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_C)).thenReturn(true);
        when(repository.deleteDocsByExtensionId(SCHEMA_TEST_EXTS_C)).thenReturn(getEntitiesToUpload());
        // Verify exception is not thrown
        assertDoesNotThrow(() -> {
            service.deleteDocsByExtensionId(SCHEMA_TEST_EXTS_C);
        });
    }

    @Test
    void deleteDocsWithInvalidExtension() throws OperationException {
        // Providing mock knowledge
        when(repository.isExtensionInserted(SCHEMA_TEST_EXTS_D)).thenReturn(false);
        // Verify exception is not thrown
        assertThrows(ExtensionNotFoundException.class, () -> {
            service.deleteDocsByExtensionId(SCHEMA_TEST_EXTS_D);
        });
    }

    @AfterAll
    public void teardown() {
        this.clearTestEntities();
    }
}
