package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.impl;

import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo.FIELD_DELETED;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo.FIELD_LAST_UPDATE;
import static org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
public class DocumentRepo implements IDocumentRepo {

    @Autowired
    private MongoTemplate mongo;

    /**
     * Retrieves the documents entities by their extension identifier
     * @param extension The extension id
     * @param includeDeleted
     * @return The documents matching the extension identifier or an empty list if none match
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> findDocsByExtensionId(String extension, boolean includeDeleted) throws OperationException {
        Query query = Query.query(Criteria.where(FIELD_TYPE_ID_EXT).is(extension));

        if (!includeDeleted) {
            query.addCriteria(Criteria.where(FIELD_DELETED).is(false));
        }

        try {
           return mongo.find(query, SchemaETY.class);
        } catch (MongoException e) {
            throw new OperationException(ERR_REP_DOCS_NOT_FOUND, e);
        }
    }

    /**
     * Verify if exists at least one document with the given extension identifier
     * @param extension The extension id
     * @return {@code True} if at least one document exists otherwise {@code False}
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public boolean isExtensionInserted(String extension) throws OperationException {
        Query query = Query.query(Criteria.where(FIELD_TYPE_ID_EXT).is(extension).and(FIELD_DELETED).is(false));
        try {
            return mongo.exists(query, SchemaETY.class);
        } catch(MongoException e) {
            throw new OperationException(ERR_REP_IS_EXT_INSERTED , e);
        }
    }

    /**
     * Returns all documents matching extension and one of the given filenames
     *
     * @param extension The extension id
     * @return Map containing all matching documents entities by filename
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> getInsertedDocumentsByExtension(String extension) throws OperationException {
        Query query = Query.query(Criteria.where(FIELD_TYPE_ID_EXT).is(extension).and(FIELD_DELETED).is(false));
        try {
            return mongo.find(query, SchemaETY.class);
        } catch (MongoException e) {
            throw new OperationException(ERR_REP_IS_DOCS_INSERTED , e);
        }
    }

    /**
     * Inserts all the given entities inside the schema
     * @param entities The entities to insert
     * @return The entities inserted
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> insertDocsByExtensionId(List<SchemaETY> entities) throws OperationException {
        try {
            return new ArrayList<>(mongo.insertAll(entities));
        } catch(MongoException e) {
            throw new OperationException(ERR_REP_INS_DOCS_BY_EXT , e);
        }
    }

    /**
     * Update all the given entities inside the schema
     * @param entities Map containing keys as the old document and values as the new ones
     * @return The {@link BulkWriteResult} object
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> updateDocsByExtensionId(Map<SchemaETY, SchemaETY> entities)
        throws OperationException, DataIntegrityException {
        // List to hold queries
        BulkOperations ops = mongo.bulkOps(UNORDERED, SchemaETY.class);
        BulkWriteResult result;
        // Create queries
        // They will be sent as a single call to the database
        entities.forEach((current, newest) -> {
            // Create query to match the required file
            Query query = new Query();
            query.addCriteria(
                where(FIELD_ID).is(new ObjectId(current.getId()))
            );
            query.addCriteria(
                where(FIELD_DELETED).is(false)
            );
            // Set fields to modify
            Update update = new Update();
            update.set(FIELD_LAST_UPDATE, new Date());
            update.set(FIELD_DELETED, true);
            // Creating query to mark as deleted the old file
            ops.updateOne(query, update);
            // Creating query to insert the new ones
            ops.insert(newest);
        });

        // Now, we reach the database instance with the queries
        try {
            // Execute
            result = ops.execute();
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException(ERR_REP_UPD_DOCS_BY_EXT , e);
        }
        // Assert we modified the expected data size
        if(entities.size() != result.getMatchedCount() || entities.size() != result.getModifiedCount()) {
            throw new DataIntegrityException(
                String.format(ERR_REP_UPD_MISMATCH, result.getModifiedCount(), entities.size())
            );
        }
        // Bye bye
        return new ArrayList<>(entities.values());
    }

    /**
     * Deletes all the documents entities matching the given extensions
     * @param extension The extension id
     * @return The list containing all removed entities from the collection
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> deleteDocsByExtensionId(String extension) throws OperationException, DataIntegrityException {
        // Working vars
        List<SchemaETY> entities;
        UpdateResult result;
        // Create query
        Query query = new Query();
        query.addCriteria(where(FIELD_TYPE_ID_EXT).is(extension));
        query.addCriteria(where(FIELD_DELETED).is(false));
        // Create update definition
        Update update = new Update();
        update.set(FIELD_LAST_UPDATE, new Date());
        update.set(FIELD_DELETED, true);
        // Get docs to remove
        entities = findDocsByExtensionId(extension, false);
        try {
            // Execute
            result = mongo.updateMulti(query, update, SchemaETY.class);
        } catch(MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException(ERR_REP_DEL_DOCS_BY_EXT , e);
        }
        // Assert we modified the expected data size
        if(entities.size() != result.getMatchedCount() || entities.size() != result.getModifiedCount()) {
            throw new DataIntegrityException(String.format(ERR_REP_DEL_MISMATCH, result.getModifiedCount(), entities.size()));
        }
        // Return modified entities
        return entities;
    }

    /**
     * Retrieves one single document given identifier
     *
     * @param id The document id
     * @return The requested document
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public SchemaETY findDocById(String id) throws OperationException {
        // Create query
        Query query = query(where(FIELD_ID).is(new ObjectId(id)).and(FIELD_DELETED).ne(true));
        try {
            // Execute
            return mongo.findOne(query, SchemaETY.class);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException(ERR_REP_GET_BY_ID, e);
        }
    }

    /**
     * Returns all documents matching extension and one of the given filenames
     *
     * @param extension   The extension id
     * @param filenames The filenames to verify
     * @return Map containing all matching documents entities by filename
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> findExistingDocumentsByExtensionAndFilenames(String extension, List<String> filenames) throws OperationException {
        Query query = Query.query(Criteria.where(FIELD_TYPE_ID_EXT).is(extension).and(FIELD_DELETED).is(false).and(FIELD_FILENAME).in(filenames));
        try {
            return mongo.find(query, SchemaETY.class);
        } catch (MongoException e) {
            throw new OperationException("Unable to match documents with filenames for the given extension" , e);
        }
    }

    /**
     * Deletes all the documents entities matching the given extensions
     * @param extension The extension id
     * @param filenames files to be logically deleted
     * @return The list containing all removed entities from the collection
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> deleteDocsByExtensionIdAndFilenames(String extension, List<String> filenames) throws OperationException, DataIntegrityException {
        Query query = Query.query(Criteria.where(FIELD_TYPE_ID_EXT).is(extension).and(FIELD_DELETED).is(false).and(FIELD_FILENAME).in(filenames));
        Update update = new Update();
        update.set(FIELD_LAST_UPDATE, new Date());
        update.set(FIELD_DELETED, true);
        List<SchemaETY> entities = findExistingDocumentsByExtensionAndFilenames(extension, filenames);
        try {
            UpdateResult result = mongo.updateMulti(query, update, SchemaETY.class);
            if (entities.size() != result.getMatchedCount() || entities.size() != result.getModifiedCount()) {
                throw new DataIntegrityException(String.format(ERR_REP_DEL_MISMATCH, result.getModifiedCount(), entities.size()));
            }
        } catch(MongoException e) {
            throw new OperationException(ERR_REP_DEL_DOCS_BY_EXT , e);
        }
        return entities;
    }
}
