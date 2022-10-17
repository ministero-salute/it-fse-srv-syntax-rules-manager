package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityRoutes.*;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public final class MockRequests {

    /**
     * Private constructor to disallow to access from other classes
     */
    private MockRequests() {}

    public static MockHttpServletRequestBuilder getStatusReq(Date lastUpdate) {
        // Default GET without parameter
        MockHttpServletRequestBuilder req = get(API_CHANGESET_STATUS);
        // Add last update
        if(lastUpdate != null) {
            // Set timezone
            // Truncate to millis otherwise SpringBoot DateTimeFormat start crying
            OffsetDateTime update = lastUpdate
                .toInstant()
                .atOffset(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS);
            // Format
            req.queryParam(API_QP_LAST_UPDATE, ISO_DATE_TIME.format(update));
        }
        return req;
    }

    public static MockHttpServletRequestBuilder getDocByIdReq(String id) {
        return get(API_GET_ONE_BY_ID_FULL, id).contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public static MockHttpServletRequestBuilder findDocsByExtensionIdReq(String extension) {
        return get(API_GET_BY_EXTS_FULL, extension)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public static MockHttpServletRequestBuilder deleteDocsByExtensionIdReq(String extension) {
        return delete(API_DELETE_BY_EXTS_FULL, extension)
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    }

    public static MockHttpServletRequestBuilder putDocsByExtensionIdReq(String root, String extension, MockMultipartFile[] files) {
        // Create request
        MockMultipartHttpServletRequestBuilder req = multipart(API_DOCUMENT_MAPPER);
        req.part(new MockPart("root", root.getBytes()));
        req.part(new MockPart("extension", extension.getBytes()));
        // Iterate file
        for (MockMultipartFile f : files) {
            req.file(f);
        }
        // Modify output method
        req.with(request -> {
            request.setMethod(HttpMethod.PUT.name());
            return request;
        });
        return req;
    }

    public static MockHttpServletRequestBuilder patchDocsByExtensionIdReq(String extension, List<MockMultipartFile> files) {
        // Create request
        MockMultipartHttpServletRequestBuilder req = multipart(API_DOCUMENT_MAPPER);
        req.part(new MockPart("extension", extension.getBytes()));
        // Iterate file
        if (files != null) {
            for (MockMultipartFile f : files) {
                req.file(f);
            }
        }

        // Modify output method
        req.with(request -> {
            request.setMethod(HttpMethod.PATCH.name());
            return request;
        });
        return req;
    }

    public static MockHttpServletRequestBuilder postDocsByExtensionIdReq(String root, String extension, MockMultipartFile[] files) {
        // Create request
        MockMultipartHttpServletRequestBuilder req = multipart(API_DOCUMENT_MAPPER);
        // Iterate file
        for (MockMultipartFile f : files) {
            req.file(f);
        }
        // Add parts
        req.part(
            new MockPart("root", root.getBytes()),
            new MockPart("extension", extension.getBytes())
        );
        return req;
    }

    public static String createBlankString() {
        return "  ";
    }

    public static MockMultipartFile createFakeMultipart(String filename, boolean validFile) {
        return new MockMultipartFile(
            "files",
            filename,
            APPLICATION_XML_VALUE,
            validFile ? "<?xml xs:schema".getBytes(StandardCharsets.UTF_8) : new byte[0]
        );
    }

    public static MockHttpServletRequestBuilder findActiveDocsReq() {
        return get(API_DOCUMENT_MAPPER)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
