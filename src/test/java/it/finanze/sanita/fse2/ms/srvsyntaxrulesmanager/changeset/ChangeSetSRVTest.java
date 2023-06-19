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

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.AbstractEntityHandler;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IChangeSetSRV;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@WebMvcTest(IChangeSetSRV.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChangeSetSRVTest extends AbstractEntityHandler {

    @MockBean
    private Tracer tracer;
    @MockBean
    private IChangeSetRepo<SchemaETY> repository;
    @Autowired
    private IChangeSetSRV service;

    @BeforeAll
    public void setup() throws IOException {
        this.setupTestEntities();
    }

    @Test
    void getInsertions() throws OperationException {
        // Providing mock knowledge
        when(repository.getInsertions(any(Date.class))).thenReturn(getEntitiesToUpload());
        // Assertions
        assertDoesNotThrow(() -> {
            assertEquals(getEntitiesToUpload().size(), service.getInsertions(new Date()).size());
        });
        // Providing mock knowledge
        when(repository.getEveryActiveDocument()).thenReturn(getEntitiesToUpload());
        // Assertions
        assertDoesNotThrow(() -> {
            assertEquals(getEntitiesToUpload().size(), service.getInsertions(null).size());
        });
    }

    @Test
    void getDeletions() throws OperationException {
        // Providing mock knowledge
        when(repository.getDeletions(any(Date.class))).thenReturn(getEntitiesToUpload());
        // Assertions
        assertDoesNotThrow(() -> {
            assertEquals(getEntitiesToUpload().size(), service.getDeletions(new Date()).size());
        });
        // Providing mock knowledge
        when(repository.getDeletions(any())).thenReturn(getEntitiesToUpload());
        // Assertions
        assertDoesNotThrow(() -> {
            assertTrue(service.getDeletions(null).isEmpty());
        });
    }

    @AfterAll
    public void teardown() {
        this.clearTestEntities();
    }
}
