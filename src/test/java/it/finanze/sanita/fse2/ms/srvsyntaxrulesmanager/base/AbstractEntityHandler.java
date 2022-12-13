/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 *
 */package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractEntityHandler {

    /**
     * An invalid objectID for test purpose
     */
    public final String FAKE_INVALID_DTO_ID = "||----test";
    /**
     * Valid objectID for test purpose
     */
    public final String FAKE_VALID_DTO_ID = "62cd4f7f5c7e221a80e7effa";

    /**
     * This collection is going to be retrieved
     */
    public static final String SCHEMA_TEST_EXTS_A = "POCD_MT000040UV02";
    /**
     * This collection is going to be partially replaced
     * with entities contained by {@link #replacement}
     */
    public static final String SCHEMA_TEST_EXTS_B = "POCD_MT000040UV03";
    /**
     * This collection is going to be deleted
     */
    public static final String SCHEMA_TEST_EXTS_C = "POCD_MT000040UV04";
    /**
     * This collection is going to be uploaded
     */
    public static final String SCHEMA_TEST_EXTS_D = "7POCD_MT000040UV05";
    /**
     * This collection does not exist
     */
    public static final String SCHEMA_TEST_FAKE_EXTS = "POCD_MT000040UV06";
    /**
     * Each collection is made by X files, where X is defined by this constant<br>
     * Note: It does not apply for {@code getEntitiesToUseAsReplacement()}
     */
    public static final int SCHEMA_TEST_SIZE = 10;
    /**
     * Sample parameter for multiple tests
     */
    public static final String SCHEMA_TEST_ROOT = "CDA.xsd";
    /**
     * Sample parameter for multiple tests
     */
    public static final String SCHEMA_TEST_ROOT_B = "file.xsd";
    /**
     * Directory containing sample files to upload as test
     */
    public static final Path SCHEMA_SAMPLE_FILES = Paths.get(
        "src",
        "test",
        "resources",
        "schema",
        "files",
        "standard");
    /**
     * Directory containing modified files used to replace sample ones
     */
    public static final Path SCHEMA_MOD_SAMPLE_FILES = Paths.get(
        "src",
        "test",
        "resources",
        "schema",
        "files",
        "modified");

    private final Map<String, Map<String, SchemaETY>> entities;

    private final Map<String, SchemaETY> replacement;

    protected AbstractEntityHandler() {
        this.replacement = new HashMap<>();
        this.entities = new HashMap<>();
        this.entities.put(SCHEMA_TEST_EXTS_A, new HashMap<>());
        this.entities.put(SCHEMA_TEST_EXTS_D, new HashMap<>());
        this.entities.put(SCHEMA_TEST_EXTS_C, new HashMap<>());
        this.entities.put(SCHEMA_TEST_EXTS_B, new HashMap<>());
    }

    protected void setupTestEntities() throws IOException {
        // Add entities to map instance
        addTestEntityToMap(SCHEMA_TEST_EXTS_A);
        addTestEntityToMap(SCHEMA_TEST_EXTS_B);
        addTestEntityToMap(SCHEMA_TEST_EXTS_C);
        addTestEntityToMap(SCHEMA_TEST_EXTS_D);
        // Add entities to be replaced by tests
        setTestEntityToReplace();
    }

    protected void clearTestEntities() {
        this.entities.forEach(
            (s, map) -> map.clear()
        );
        this.entities.clear();
        this.replacement.clear();
    }

    protected Map<String, Map<String, SchemaETY>> getEntities() {
        return new HashMap<>(entities);
    }

    protected Map<String, SchemaETY> getReadOnlyEntities() {
        return new HashMap<>(entities.get(SCHEMA_TEST_EXTS_A));
    }

    protected List<SchemaETY> getEntitiesToUpload() {
        return new ArrayList<>(entities.get(SCHEMA_TEST_EXTS_D).values());
    }

    protected Map<String, SchemaETY> getEntitiesToUseAsReplacement() {
        return new HashMap<>(replacement);
    }

    protected List<SchemaETY> getEntitiesToUseAsReplacementList() {
        return new ArrayList<>(getEntitiesToUseAsReplacement().values());
    }

    private void setTestEntityToReplace() throws IOException {
        // List of all files inside the sample modified directory
        try (Stream<Path> files = Files.list(SCHEMA_MOD_SAMPLE_FILES)) {
            // Convert to list
            List<Path> samples = files.collect(Collectors.toList());
            // Add to each map and convert
            for (Path path : samples) {
                this.replacement.put(
                    path.getFileName().toString(),
                    SchemaETY.fromPath(
                        path,
                        SCHEMA_TEST_EXTS_B,
                        SCHEMA_TEST_ROOT.equals(path.getFileName().toString())
                    )
                );
            }
        }
    }
    private void addTestEntityToMap(String extension) throws IOException {
        // List of all files inside the sample modified directory
        try (Stream<Path> files = Files.list(SCHEMA_SAMPLE_FILES)) {
            // Convert to list
            List<Path> samples = files.collect(Collectors.toList());
            // Add to each map and convert
            for (Path path : samples) {
                this.entities
                    .get(extension)
                    .put(
                        path.getFileName().toString(),
                        SchemaETY.fromPath(
                            path,
                            extension,
                            SCHEMA_TEST_ROOT.equals(path.getFileName().toString())
                        )
                    );
            }
        }
    }
}
