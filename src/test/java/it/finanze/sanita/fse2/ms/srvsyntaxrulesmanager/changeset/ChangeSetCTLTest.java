/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.changeset;


import brave.Tracer;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.IChangeSetCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IChangeSetSRV;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base.MockRequests.getStatusReq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IChangeSetCTL.class)
class ChangeSetCTLTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private Tracer tracer;
    @MockBean
    private IChangeSetSRV service;
    @Test
    void getStatus() throws Exception {
        // Providing mock knowledge
        when(service.getInsertions(any(Date.class))).thenReturn(new ArrayList<>());
        when(service.getDeletions(any(Date.class))).thenReturn(new ArrayList<>());
        // Execute request
        mvc.perform(
            getStatusReq(new Date())
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE),
            jsonPath("$.totalNumberOfElements").value(0)
        );
    }

    @Test
    void getStatusAsNull() throws Exception {
        // Providing mock knowledge
        when(service.getInsertions(any())).thenReturn(new ArrayList<>());
        when(service.getDeletions(any())).thenReturn(new ArrayList<>());
        // Execute request
        mvc.perform(
            getStatusReq(null)
        ).andExpectAll(
            status().is2xxSuccessful(),
            content().contentType(APPLICATION_JSON_VALUE),
            jsonPath("$.totalNumberOfElements").value(0)
        );
    }

    @Test
    void getFutureStatus() throws Exception {
        // Execute request
        mvc.perform(
            getStatusReq(Date.from(LocalDateTime.now().plusWeeks(1).toInstant(ZoneOffset.UTC)))
        ).andExpectAll(
            status().is4xxClientError(),
            content().contentType(APPLICATION_PROBLEM_JSON)
        );
    }

}
