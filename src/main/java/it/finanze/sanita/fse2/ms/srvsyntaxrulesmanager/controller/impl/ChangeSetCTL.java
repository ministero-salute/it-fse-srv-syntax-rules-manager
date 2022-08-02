package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.IChangeSetCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DateNotValidException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IChangeSetSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
public class ChangeSetCTL extends AbstractCTL implements IChangeSetCTL {

    @Autowired
    private IChangeSetSRV service;

    /**
     * @param lastUpdate The last update date
     * @return The changeset for the current time according to the last update
     * @throws OperationException If a data-layer error occurs
     * @throws DateNotValidException If the last update is in the future
     */
    @Override
    public ChangeSetResDTO changeSet(@Nullable Date lastUpdate) throws OperationException, DateNotValidException {
        // Verify not in the future
        if(lastUpdate != null && lastUpdate.after(new Date())) {
            throw new DateNotValidException("The last update date cannot be in the future");
        }
        // Retrieve changes
        List<ChangeSetDTO> insertions = service.getInsertions(lastUpdate);
        List<ChangeSetDTO> deletions = service.getDeletions(lastUpdate);
        List<ChangeSetDTO> modifications = service.getModifications(lastUpdate);
        int totalNumberOfElements = insertions.size() + deletions.size() + modifications.size();
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
        response.setModifications(modifications);
        response.setTotalNumberOfElements(totalNumberOfElements);
        // Have a nice day
        return response;
    }
}
