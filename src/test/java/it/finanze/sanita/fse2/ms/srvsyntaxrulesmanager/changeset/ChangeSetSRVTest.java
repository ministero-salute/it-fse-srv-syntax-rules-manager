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
