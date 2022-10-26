package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractEntityHandler.SCHEMA_MOD_SAMPLE_FILES;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractEntityHandler.SCHEMA_SAMPLE_FILES;
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
        req.part(new MockPart(API_PARAM_ROOT, root.getBytes()));
        req.part(new MockPart(API_PATH_EXTS_VAR, extension.getBytes()));
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

    public static MockHttpServletRequestBuilder patchDocsByExtensionIdReq(String extension, MockMultipartFile[] files) {
        // Create request
        MockMultipartHttpServletRequestBuilder req = multipart(API_DOCUMENT_MAPPER);
        req.part(new MockPart(API_PATH_EXTS_VAR, extension.getBytes()));
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
            new MockPart(API_PARAM_ROOT, root.getBytes()),
            new MockPart(API_PATH_EXTS_VAR, extension.getBytes())
        );
        return req;
    }

    public static String createBlankString() {
        return "  ";
    }

    public static MockMultipartFile createInvalidFakeMultipart(String filename, boolean validFile) {
        return new MockMultipartFile(
            "files",
            filename,
            APPLICATION_XML_VALUE,
            validFile ? "<?xml xs:schema".getBytes(StandardCharsets.UTF_8) : new byte[0]
        );
    }

    public static MockMultipartFile[] createPartialSchemaFromResource(String field) throws IOException {
        return retrieveSchemaFromResource(field, SCHEMA_MOD_SAMPLE_FILES).toArray(new MockMultipartFile[0]);
    }

    public static MockMultipartFile[] createSchemaFromResource(String field, boolean isValid) throws IOException {
        // Get as array
        ArrayDeque<MockMultipartFile> mocks = new ArrayDeque<>(
            retrieveSchemaFromResource(field, SCHEMA_SAMPLE_FILES)
        );
        // Just remove last file, it won't be validated anymore
        if(!isValid) mocks.removeLast();
        return mocks.toArray(new MockMultipartFile[0]);
    }

    private static Queue<MockMultipartFile> retrieveSchemaFromResource(String field,Path p) throws IOException {
        // Working var
        Queue<MockMultipartFile> mocks = new ArrayDeque<>();
        // List of all files inside the sample modified directory
        try (Stream<Path> files = Files.list(p)) {
            // Convert to list
            List<Path> samples = files.collect(Collectors.toList());
            // Add to each map and convert
            for (Path path : samples) {
                mocks.add(
                    new MockMultipartFile(
                        field,
                        path.getFileName().toString(),
                        APPLICATION_XML_VALUE,
                        Files.newInputStream(path.toFile().toPath())
                    )
                );
            }
        }
        return mocks;
    }


    public static MockHttpServletRequestBuilder findActiveDocsReq() {
        return get(API_DOCUMENT_MAPPER)
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
