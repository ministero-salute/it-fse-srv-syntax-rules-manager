/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.runner;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@Profile(Constants.Profile.RUNNER)
public class TestFileLoader implements CommandLineRunner {

    /**
     * This collection is going to be retrieved
     */
    public static final String SCHEMA_TEST_ID = "POCD_MT000040UV02";
    /**
     * Sample parameter for multiple tests
     */
    public static final String SCHEMA_TEST_ROOT = "CDA.xsd";
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

    @Autowired
    private IDocumentRepo repository;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        log.info("RUNNER testFileLoader()");
        try {
            // Check if test files exists
            if(!repository.isExtensionInserted(SCHEMA_TEST_ID)) {
                log.info("[testFileLoader()] Test files not found");
                // Start insertions
                insertEntities();
            }else {
                log.info("[testFileLoader()] Test files already inserted, quitting...");
            }
        } catch (OperationException e) {
            log.error("[testFileLoader()] Unable to verify if test files are inserted", e);
        }
    }

    private void insertEntities() {
        try {
            log.info("[testFileLoader()] Retrieving from F/S");
            // Retrieve files from I/O
            insertEntitiesIntoDB(getTestEntities());
        } catch (IOException e) {
            log.error("[testFileLoader()] Unable to retrieve test files to upload", e);
        }
    }

    private void insertEntitiesIntoDB(List<SchemaETY> entities) {
        try {
            log.info("[testFileLoader()] Uploading files...");
            repository.insertDocsByExtensionId(entities);
            log.info("[testFileLoader()] Files uploaded");
        }catch (OperationException e) {
            log.error("[testFileLoader()] Unable to insert test files into the database", e);
        }
    }

    private List<SchemaETY> getTestEntities() throws IOException {
        // Create collection
        List<SchemaETY> entities = new ArrayList<>();
        // List of all files inside the sample modified directory
        try (Stream<Path> files = Files.list(SCHEMA_SAMPLE_FILES)) {
            // Convert to list
            List<Path> samples = files.collect(Collectors.toList());
            // Add each one to list
            for (Path path : samples) {
                entities.add(
                    SchemaETY.fromPath(
                        path,
                        TestFileLoader.SCHEMA_TEST_ID,
                        SCHEMA_TEST_ROOT.equals(path.getFileName().toString())
                    )
                );
            }
        }
        // Return list
        return entities;
    }
}
