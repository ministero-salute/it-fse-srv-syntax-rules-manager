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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.changeset;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractDatabaseHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.MiscUtility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.ComponentScan.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    void getInsertionsWithTimestamp() throws OperationException {
        // Retrieve documents with yesterday date
        List<SchemaETY> insertions = repository.getInsertions(MiscUtility.getYesterday());
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
    @Disabled("to modify")
    void getModificationsWithTimestamp() throws OperationException, DataIntegrityException {
        // Working vars
        Map<String, SchemaETY> newest = getEntitiesToUseAsReplacement();
        Map<SchemaETY, SchemaETY> entities = new HashMap<>();
        // Documents to modify
        List<String> filenames = new ArrayList<>(getEntitiesToUseAsReplacement().keySet());
        // Retrieve old documents
        List<SchemaETY> current = documents.getInsertedDocumentsByExtension(
            SCHEMA_TEST_EXTS_B
        );
        // Modify data
        current.forEach((entity) -> {
            // Create new date
            Date date = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));
            // Create new timestamp
            // (we need it because the updated files and the newest one already match)
            newest.get(entity.getNameSchema()).setInsertionDate(date);
            newest.get(entity.getNameSchema()).setLastUpdateDate(date);
            // Add it
            entities.put(entity, newest.get(entity.getNameSchema()));
        });
        // Replace test entities content
        documents.updateDocsByExtensionId(entities);
        // Retrieve documents with last update
        List<SchemaETY> insertions = repository.getInsertions(lastUpdate);
        List<SchemaETY> deletions = repository.getDeletions(lastUpdate);
        // Expect size match
        assertEquals(getEntitiesToUseAsReplacement().values().size(), insertions.size());
        assertEquals(getEntitiesToUseAsReplacement().values().size(), deletions.size());
    }

    @Test
    void getDeletions() throws OperationException {
        // Retrieve documents with current timestamp
        List<SchemaETY> deletions = repository.getDeletions(new Date());
        // Expect nothing
        assertTrue(deletions.isEmpty());
    }

    @Test
    @Disabled("to modify")
    void getDeletionsWithTimestamp() throws OperationException, DataIntegrityException {
        // Delete one collection
        documents.deleteDocsByExtensionId(SCHEMA_TEST_EXTS_C);
        // Retrieve documents with lastUpdate
        List<SchemaETY> deletions = repository.getDeletions(lastUpdate);
        // Expect full match
        // We need to sum up the files we modified during this test run
        assertEquals(SCHEMA_TEST_SIZE + getEntitiesToUseAsReplacement().size(), deletions.size());
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
