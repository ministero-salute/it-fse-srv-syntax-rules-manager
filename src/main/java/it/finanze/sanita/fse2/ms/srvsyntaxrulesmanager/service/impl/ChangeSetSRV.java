/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IChangeSetSRV;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityMisc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ChangeSetDTO> getInsertions(@Nullable Date lastUpdate) throws OperationException {
        // Retrieve insertions
        List<SchemaETY> insertions;
        // Verify no null value has been provided
        if(lastUpdate != null) {
            insertions = repository.getInsertions(lastUpdate);
        } else {
            insertions = repository.getEveryActiveDocument();
        }
        // Iterate and populate
        return insertions.stream().map(UtilityMisc::toChangeset).collect(Collectors.toList());
    }

    /**
     * Retrieves the latest deletions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing deletions
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<ChangeSetDTO> getDeletions(@Nullable Date lastUpdate) throws OperationException {
        // Create empty container
        List<ChangeSetDTO> changes = new ArrayList<>();
        // Verify no null value has been provided
        if(lastUpdate != null) {
            // Retrieve deletions
            List<SchemaETY> deletions = repository.getDeletions(lastUpdate);
            // Iterate and populate
            changes = deletions.stream().map(UtilityMisc::toChangeset).collect(Collectors.toList());
        }
        return changes;
    }
}
