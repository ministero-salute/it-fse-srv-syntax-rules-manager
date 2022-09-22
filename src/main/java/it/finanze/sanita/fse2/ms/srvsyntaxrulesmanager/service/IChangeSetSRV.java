package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface IChangeSetSRV {
    /**
     * Retrieves the latest insertions according to the given timeframe
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing insertions
     * @throws OperationException If a data-layer error occurs
     */
    List<ChangeSetDTO> getInsertions(@Nullable Date lastUpdate) throws OperationException;

    /**
     * Retrieves the latest deletions according to the given timeframe
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing deletions
     * @throws OperationException If a data-layer error occurs
     */
    List<ChangeSetDTO> getDeletions(@Nullable Date lastUpdate) throws OperationException;
}
