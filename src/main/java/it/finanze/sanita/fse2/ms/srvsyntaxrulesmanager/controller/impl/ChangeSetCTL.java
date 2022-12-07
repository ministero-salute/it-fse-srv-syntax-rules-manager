/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.IChangeSetCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IChangeSetSRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class ChangeSetCTL extends AbstractCTL implements IChangeSetCTL {

    @Autowired
    private IChangeSetSRV service;

    /**
     * @param lastUpdate The last update date
     * @return The changeset for the current time according to the last update
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public ChangeSetResDTO changeSet(@Nullable Date lastUpdate) throws OperationException {
        // Retrieve changes
        List<ChangeSetDTO> insertions = service.getInsertions(lastUpdate);
        List<ChangeSetDTO> deletions = service.getDeletions(lastUpdate);
        long collectionSize = service.getCollectionSize();
        long totalNumberOfElements = insertions.size() + deletions.size();
        // Retrieve log trace
        LogTraceInfoDTO trace = getLogTraceInfo();
        // Build response
        ChangeSetResDTO response = new ChangeSetResDTO();
        response.setTraceID(trace.getTraceID());
        response.setSpanID(trace.getSpanID());
        response.setLastUpdate(lastUpdate);
        response.setTimestamp(new Date());
        response.setInsertions(insertions);
        response.setDeletions(deletions);
        response.setTotalNumberOfElements(totalNumberOfElements);
        response.setCollectionSize(collectionSize);
        return response;
    }
}
