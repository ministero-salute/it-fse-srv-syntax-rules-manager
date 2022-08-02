package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IChangeSetSRV;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsMisc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChangeSetSRV implements IChangeSetSRV {

    @Autowired
    private IChangeSetRepo<SchemaETY> repository;

    /**
     * Retrieves the latest insertions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing insertions
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<ChangeSetDTO> getInsertions(Date lastUpdate) throws OperationException {
        // Retrieve insertions
        List<SchemaETY> insertions;
        // Verify no null value has been provided
        if(lastUpdate != null) {
            insertions = repository.getInsertions(lastUpdate);
        }else{
            insertions = repository.getEveryActiveDocument();
        }
        // Iterate and populate
        return insertions.stream().map(UtilsMisc::toChangeset).collect(Collectors.toList());
    }

    /**
     * Retrieves the latest modifications according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing modifications
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<ChangeSetDTO> getModifications(Date lastUpdate) throws OperationException {
        // Create empty container
        List<ChangeSetDTO> changes = new ArrayList<>();
        // Verify no null value has been provided
        if(lastUpdate != null) {
            // Retrieve modifications
            List<SchemaETY> modifications = repository.getModifications(lastUpdate);
            // Iterate and populate
            changes = modifications.stream().map(UtilsMisc::toChangeset).collect(Collectors.toList());
        }
        return changes;
    }

    /**
     * Retrieves the latest deletions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing deletions
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<ChangeSetDTO> getDeletions(Date lastUpdate) throws OperationException {
        // Create empty container
        List<ChangeSetDTO> changes = new ArrayList<>();
        // Verify no null value has been provided
        if(lastUpdate != null) {
            // Retrieve deletions
            List<SchemaETY> deletions = repository.getDeletions(lastUpdate);
            // Iterate and populate
            changes = deletions.stream().map(UtilsMisc::toChangeset).collect(Collectors.toList());
        }
        return changes;
    }
}
