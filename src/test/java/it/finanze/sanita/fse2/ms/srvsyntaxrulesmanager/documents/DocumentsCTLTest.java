package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.documents;

import brave.Tracer;
import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataProcessingException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.MockRequests.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorType.*;
import static org.apache.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(Constants.Profile.TEST)
@AutoConfigureMockMvc
class DocumentsCTLTest extends AbstractEntityHandler {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Tracer tracer;

    @SpyBean
    private MongoTemplate mongoTemplate;

    @SpyBean
    private IDocumentRepo documentRepo;

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection(SchemaETY.class);
    }

    @Test
    void getDocumentById() throws Exception {
        SchemaETY schemaETY = SchemaETY.fromMultipart(createFakeMultipart("test1.xsd", true), SCHEMA_TEST_EXTS_A, true);
        schemaETY.setId(FAKE_VALID_DTO_ID);
        mongoTemplate.insert(schemaETY);
        mvc.perform(getDocByIdReq(FAKE_VALID_DTO_ID)).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void getDocumentByIdWithInvalidId() throws Exception {
        mvc.perform(
            getDocByIdReq(FAKE_INVALID_DTO_ID)
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }

    @Test
    void getDocumentByIdWithNotFound() throws Exception {
        mvc.perform(
            getDocByIdReq(FAKE_VALID_DTO_ID)
        ).andExpectAll(
            status().is(SC_NOT_FOUND),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void getDocumentByIdInvalidOperation() throws Exception {
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).findOne(any(), eq(SchemaETY.class));
        mvc.perform(
            getDocByIdReq(FAKE_VALID_DTO_ID)
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }

    @Test
    void getDocumentsByValidExtension() throws Exception {
        this.uploadDocumentsWithValidData();
        mvc.perform(
            findDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void getDocumentsByInvalidExtension() throws Exception {
        mvc.perform(
            findDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_NOT_FOUND),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void getDocumentsByInvalidOperation() throws Exception {
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).find(any(), eq(SchemaETY.class));
        mvc.perform(
            findDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }

    @Test
    void getDocumentsByUnknownError() throws Exception {
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).find(any(), eq(SchemaETY.class));
        mvc.perform(
            findDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }

    @Test
    void updateDocumentsWithValidData() throws Exception {
        this.uploadDocumentsWithValidData();
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                    SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_OK),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void updateDocumentsWithInvalidRoot() throws Exception {
        this.uploadDocumentsWithValidData();
        mvc.perform(
                putDocsByExtensionIdReq(
                        SCHEMA_TEST_ROOT,
                        SCHEMA_TEST_EXTS_A,
                        new MockMultipartFile[]{
                                createFakeMultipart("test0.xsd", true),
                                createFakeMultipart("test1.xsd", true)
                        }
                )
        ).andExpectAll(
                status().is(SC_BAD_REQUEST),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void updateDocumentsWithInvalidOperation() throws Exception {
        this.uploadDocumentsWithValidData();
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).insertAll(any());
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                    SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void updateDocumentsWithInvalidExtension() throws Exception {
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                    SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_NOT_FOUND),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void updateDocumentsWithOneOrMoreEmptyFiles() throws Exception {
        mvc.perform(
                putDocsByExtensionIdReq(
                        SCHEMA_TEST_ROOT,
                        SCHEMA_TEST_EXTS_A,
                        new MockMultipartFile[]{
                                createFakeMultipart("test0.xsd", false),
                                createFakeMultipart("test1.xsd", true)
                        }
                )
        ).andExpectAll(
                status().is(SC_BAD_REQUEST),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void updateDocumentsWithMissingFiles() throws Exception {
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                    SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{}
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }

    @Test
    void updateDocumentsWithMissingExtension() throws Exception {
        mvc.perform(
            putDocsByExtensionIdReq(
                createBlankString(),
                createBlankString(),
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd", true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }


    @Test
    void uploadDocumentsWithValidData() throws Exception {
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                }
            )
        ).andExpectAll(
            status().is(201),
            content().contentType(APPLICATION_JSON)
        );
    }

    @Test
    void uploadDocumentsWithDuplicatedFiles() throws Exception {
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void uploadDocumentsWithOperationError() throws Exception {
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).insertAll(any());
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void uploadDocumentsWithConflictExtension() throws Exception {
        this.uploadDocumentsWithValidData();
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_CONFLICT),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithMissingRoot() throws Exception {
        mvc.perform(
            postDocsByExtensionIdReq(
                createBlankString(),
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithMissingExtension() throws Exception {
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                createBlankString(),
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithMissingType() throws Exception {
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                createBlankString(),
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT, true),
                    createFakeMultipart("test1.xsd", true)
                }
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithMissingFiles() throws Exception {
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{}
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(VALIDATION.getTitle())
        );
    }

    @Test
    void deleteDocumentsByValidExtension() throws Exception {
        this.uploadDocumentsWithValidData();
        mvc.perform(
            deleteDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void deleteDocumentsByInvalidExtension() throws Exception {
        mvc.perform(
            deleteDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_NOT_FOUND),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void deleteDocumentsByInvalidOperation() throws Exception {
        this.uploadDocumentsWithValidData();
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).updateMulti(any(), any(), eq(SchemaETY.class));
        mvc.perform(
            deleteDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }

    @Test
    void deleteDocumentsByUnknownError() throws Exception {
        this.uploadDocumentsWithValidData();
        Mockito.doThrow(new MongoException("Invalid exception")).when(mongoTemplate).updateMulti(any(), any(), eq(SchemaETY.class));
        mvc.perform(
            deleteDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }

    @Test
    void patchDocumentsTest() throws Exception {
        this.uploadDocumentsWithValidData();
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(createFakeMultipart("CDA.xsd", true));
        files.add(createFakeMultipart("test1.xsd", true));

        mvc.perform(patchDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A, files)).andExpectAll(
                status().is(SC_OK),
                content().contentType(APPLICATION_JSON_VALUE)
        );

        List<SchemaETY> onDB = mongoTemplate.find(Query.query(Criteria.where("type_id_extension").is(SCHEMA_TEST_EXTS_A)), SchemaETY.class);
        // old CDA.xsd deleted, new CDA.xsd patched, new test1.xsd appended
        assertEquals(3, onDB.size());
    }

    @Test
    void patchDocumentsTestWithInvalidData() throws Exception {
        this.uploadDocumentsWithValidData();
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(createFakeMultipart("CDA.xsd", true));
        files.add(createFakeMultipart("test1.xsd", false));

        mvc.perform(patchDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A, files)).andExpectAll(
                status().is(SC_BAD_REQUEST),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );

        List<SchemaETY> onDB = mongoTemplate.find(Query.query(Criteria.where("type_id_extension").is(SCHEMA_TEST_EXTS_A)), SchemaETY.class);
        assertEquals(1, onDB.size());
    }

    @Test
    void patchDocumentsTestWithNullFiles() throws Exception {
        this.uploadDocumentsWithValidData();

        mvc.perform(patchDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A, null)).andExpectAll(
                status().is(SC_BAD_REQUEST),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );

        List<SchemaETY> onDB = mongoTemplate.find(Query.query(Criteria.where("type_id_extension").is(SCHEMA_TEST_EXTS_A)), SchemaETY.class);
        assertEquals(1, onDB.size());
    }

    @Test
    void patchDocumentsTestWithExtensionNotFound() throws Exception {
        List<MockMultipartFile> files = new ArrayList<>();
        files.add(createFakeMultipart("CDA.xsd", true));
        files.add(createFakeMultipart("test1.xsd", true));
        mvc.perform(patchDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A, files)).andExpectAll(
                status().is(SC_NOT_FOUND),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );

        List<SchemaETY> onDB = mongoTemplate.find(Query.query(Criteria.where("type_id_extension").is(SCHEMA_TEST_EXTS_A)), SchemaETY.class);
        assertEquals(0, onDB.size());
    }

    @Test
    void patchDocumentsTestWithMongoFailure() throws Exception {
        this.uploadDocumentsWithValidData();

        Mockito.doThrow(new MongoException("Mongo failure")).when(mongoTemplate).insertAll(any());

        List<MockMultipartFile> files = new ArrayList<>();
        files.add(createFakeMultipart("CDA.xsd", true));
        files.add(createFakeMultipart("test1.xsd", true));
        mvc.perform(patchDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A, files)).andExpectAll(
                status().is(SC_INTERNAL_SERVER_ERROR),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void patchDocumentsTestWithDataIntegrityException() throws Exception {
        this.uploadDocumentsWithValidData();

        Mockito.doThrow(new DataIntegrityException("Integrity failed")).when(documentRepo).deleteDocsByExtensionIdAndFilenames(anyString(), any());

        List<MockMultipartFile> files = new ArrayList<>();
        files.add(createFakeMultipart("CDA.xsd", true));
        files.add(createFakeMultipart("test1.xsd", true));
        mvc.perform(patchDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A, files)).andExpectAll(
                status().is(SC_INTERNAL_SERVER_ERROR),
                content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }
}
