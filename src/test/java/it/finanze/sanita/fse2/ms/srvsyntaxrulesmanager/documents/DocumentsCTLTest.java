package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.documents;

import brave.Tracer;
import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.IEdsDocumentsCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IDocumentSRV;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.MockRequests.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorType.*;
import static org.apache.http.HttpStatus.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IEdsDocumentsCTL.class)
class DocumentsCTLTest extends AbstractEntityHandler{

    @Autowired
    private MockMvc mvc;
    @MockBean
    private Tracer tracer;
    @MockBean
    private IDocumentSRV service;

    @Test
    void getDocumentById() throws Exception {
        // Providing mock knowledge
        when(
            service.findDocById(anyString())
        ).thenReturn(FAKE_DTO);
        // Execute request
        mvc.perform(
            getDocByIdReq(FAKE_VALID_DTO_ID)
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void getDocumentByIdWithInvalidId() throws Exception {
        // Execute request
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
        // Providing mock knowledge
        when(
            service.findDocById(anyString())
        ).thenThrow(new DocumentNotFoundException("ID is valid but no file exists"));
        // Execute request
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
        // Providing mock knowledge
        when(
            service.findDocById(anyString())
        ).thenThrow(new OperationException("An error occurred", new MongoException("")));
        // Execute request
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
        // Providing mock knowledge
        when(
            service.findDocsByExtensionId(anyString())
        ).thenReturn(new ArrayList<>());
        // Execute request
        mvc.perform(
            findDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void getDocumentsByInvalidExtension() throws Exception {
        // Providing mock knowledge
        when(
            service.findDocsByExtensionId(anyString())
        ).thenThrow(
            new ExtensionNotFoundException("Extension does not exists")
        );
        // Execute request
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
        // Providing mock knowledge
        when(
            service.findDocsByExtensionId(anyString())
        ).thenThrow(
            new OperationException("I/O Error", new MongoException("Everything blew up"))
        );
        // Execute request
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
        // Providing mock knowledge
        when(
            service.findDocsByExtensionId(anyString())
        ).thenThrow(new RuntimeException());
        // Execute request
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
        // Providing mock knowledge
        when(
            service.updateDocsByExtensionId(anyString(), any())
        ).thenReturn(new ArrayList<>());
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd"),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_OK),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void updateDocumentsWithInvalidOperation() throws Exception {
        // Providing mock knowledge
        when(
            service.updateDocsByExtensionId(anyString(), any())
        ).thenThrow(
            new OperationException("I/O Error", new MongoException("Everything blew up"))
        );
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd"),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }

    @Test
    void updateDocumentsWithInvalidExtension() throws Exception {
        // Providing mock knowledge
        when(
            service.updateDocsByExtensionId(anyString(), any())
        ).thenThrow(
            new ExtensionNotFoundException("Extension does not exists")
        );
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd"),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_NOT_FOUND),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void updateDocumentsWithOneOrMoreInvalidFiles() throws Exception {
        // Providing mock knowledge
        when(
            service.updateDocsByExtensionId(anyString(), any())
        ).thenThrow(
            new DocumentNotFoundException("test0.xsd does not exists")
        );
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd"),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_NOT_FOUND),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void updateDocumentsWithMalformedData() throws Exception {
        // Providing mock knowledge
        when(
            service.updateDocsByExtensionId(anyString(), any())
        ).thenThrow(
            new DataProcessingException(
                "Puff preety",
                new IOException("Puff puff preety")
            )
        );
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd"),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_UNPROCESSABLE_ENTITY),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(IO.getTitle())
        );
    }

    @Test
    void updateDocumentsWithMissingFiles() throws Exception {
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
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
        // Execute request
        mvc.perform(
            putDocsByExtensionIdReq(
                createBlankString(),
                new MockMultipartFile[]{
                    createFakeMultipart("test0.xsd"),
                    createFakeMultipart("test1.xsd")
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
        // Providing mock knowledge
        when(
            service.insertDocsByExtensionId(anyString(),anyString(), any())
        ).thenReturn(new ArrayList<>());
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_OK),
            content().contentType(APPLICATION_JSON)
        );
    }

    @Test
    void uploadDocumentsWithDuplicatedFiles() throws Exception {
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_BAD_REQUEST),
            content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    void uploadDocumentsWithOperationError() throws Exception {
        // Providing mock knowledge
        when(
            service.insertDocsByExtensionId(anyString(),anyString(),any())
        ).thenThrow(
            new DataProcessingException(
                "Puff preety",
                new IOException("Puff puff preety")
            )
        );
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_UNPROCESSABLE_ENTITY),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(IO.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithConflictExtension() throws Exception {
        // Providing mock knowledge
        when(
            service.insertDocsByExtensionId(anyString(),anyString(), any())
        ).thenThrow(
            new ExtensionAlreadyExistsException("Extension exists")
        );
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_CONFLICT),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(RESOURCE.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithProcessingError() throws Exception {
        // Providing mock knowledge
        when(
            service.insertDocsByExtensionId(anyString(),anyString(), any())
        ).thenThrow(
            new DataProcessingException("KO", new IOException())
        );
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
                }
            )
        ).andExpectAll(
            status().is(SC_UNPROCESSABLE_ENTITY),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(IO.getTitle())
        );
    }

    @Test
    void uploadDocumentsWithMissingRoot() throws Exception {
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                createBlankString(),
                SCHEMA_TEST_EXTS_A,
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
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
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                createBlankString(),
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
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
        // Execute request
        mvc.perform(
            postDocsByExtensionIdReq(
                SCHEMA_TEST_ROOT,
                createBlankString(),
                new MockMultipartFile[]{
                    createFakeMultipart(SCHEMA_TEST_ROOT),
                    createFakeMultipart("test1.xsd")
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
        // Execute request
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
        // Providing mock knowledge
        when(
            service.deleteDocsByExtensionId(anyString())
        ).thenReturn(new ArrayList<>());
        // Execute request
        mvc.perform(
            deleteDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE)
        );
    }

    @Test
    void deleteDocumentsByInvalidExtension() throws Exception {
        // Providing mock knowledge
        when(
            service.deleteDocsByExtensionId(anyString())
        ).thenThrow(
            new ExtensionNotFoundException("Extension does not exists")
        );
        // Execute request
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
        // Providing mock knowledge
        when(
            service.deleteDocsByExtensionId(anyString())
        ).thenThrow(
            new OperationException("I/O Error", new MongoException("Everything blew up"))
        );
        // Execute request
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
        // Providing mock knowledge
        when(
            service.deleteDocsByExtensionId(anyString())
        ).thenThrow(new RuntimeException());
        // Execute request
        mvc.perform(
            deleteDocsByExtensionIdReq(SCHEMA_TEST_EXTS_A)
        ).andExpectAll(
            status().is(SC_INTERNAL_SERVER_ERROR),
            content().contentType(APPLICATION_PROBLEM_JSON),
            jsonPath("$.title").value(SERVER.getTitle())
        );
    }
}
