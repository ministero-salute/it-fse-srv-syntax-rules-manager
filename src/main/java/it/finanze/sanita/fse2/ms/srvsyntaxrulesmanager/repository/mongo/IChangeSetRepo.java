package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;

import java.util.Date;
import java.util.List;

public interface IChangeSetRepo<T> {

    String FIELD_INSERTION_DATE = "insertion_date";
    String FIELD_LAST_UPDATE = "last_update_date";
    String FIELD_DELETED = "deleted";

    /**
     * Retrieves the latest insertions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing insertions
     * @throws OperationException If a data-layer error occurs
     */
    List<T> getInsertions(Date lastUpdate) throws OperationException;

    /**
     * Retrieves the latest deletions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing deletions
     * @throws OperationException If a data-layer error occurs
     */
    List<T> getDeletions(Date lastUpdate) throws OperationException;

    /**
     * Retrieves all the not-deleted extensions with their documents data
     *
     * @return Any available schema
     * @throws OperationException If a data-layer error occurs
     */
    List<T> getEveryActiveDocument() throws OperationException;
}
