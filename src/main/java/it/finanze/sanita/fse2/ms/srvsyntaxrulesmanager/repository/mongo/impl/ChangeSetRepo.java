package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.impl;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class ChangeSetRepo implements IChangeSetRepo<SchemaETY> {
    @Autowired
    private MongoTemplate mongo;

    /**
     * Retrieves the latest insertions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing insertions
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> getInsertions(Date lastUpdate) throws OperationException {
        // Working var
        List<SchemaETY> objects;
        // Create query
        Query q = query(
            where(FIELD_INSERTION_DATE).gt(lastUpdate).and(FIELD_DELETED).ne(true)
        );
        try {
            // Execute
            objects = mongo.find(q, SchemaETY.class);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to retrieve change-set insertions", e);
        }
        return objects;
    }

    /**
     * Retrieves the latest deletions according to the given timeframe
     *
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing deletions
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> getDeletions(Date lastUpdate) throws OperationException {
        // Working var
        List<SchemaETY> objects;
        // Create query
        Query q = query(
            where(FIELD_LAST_UPDATE).gt(lastUpdate)
                // If a given file is insert at t1 and
                // lastUpdate is called with t1 value
                // without the lte operator we would
                // miss its own deletion
                .and(FIELD_INSERTION_DATE).lte(lastUpdate)
                .and(FIELD_DELETED).is(true)
        );
        try {
            // Execute
            objects = mongo.find(q, SchemaETY.class);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to retrieve change-set deletions", e);
        }
        return objects;
    }

    /**
     * Retrieves all the not-deleted extensions with their data
     *
     * @return Any available schema
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> getEveryActiveDocument() throws OperationException {
        // Working var
        List<SchemaETY> objects;
        // Create query
        Query q = query(where(FIELD_DELETED).ne(true));
        try {
            // Execute
            objects = mongo.find(q, SchemaETY.class);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to retrieve every available extension with their documents", e);
        }
        return objects;
    }
}
