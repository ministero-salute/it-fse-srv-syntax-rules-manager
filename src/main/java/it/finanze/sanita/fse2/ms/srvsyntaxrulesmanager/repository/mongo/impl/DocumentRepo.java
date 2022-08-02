package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.impl;

import com.mongodb.MongoException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.CollectionNaming;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo.FIELD_DELETED;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo.FIELD_LAST_UPDATE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@Slf4j
public class DocumentRepo implements IDocumentRepo {

    @Autowired
    private MongoTemplate mongo;

    @Autowired
    private CollectionNaming naming;

    /**
     * Retrieves the documents entities by their extension identifier
     * @param extension The extension id
     * @return The documents matching the extension identifier or an empty list if none match
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> findDocsByExtensionId(String extension) throws OperationException {
        // Init empty collection
        List<SchemaETY> out;
        // Create query
        Query query = new Query();
        query.addCriteria(
            where(FIELD_TYPE_ID_EXT).is(extension).and(FIELD_DELETED).is(false)
        );
        try {
            // Execute
            out = mongo.find(query, SchemaETY.class);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to retrieve documents by extension identifier", e);
        }
        // Return data
        return out;
    }

    /**
     * Verify if exists at least one document with the given extension identifier
     * @param extension The extension id
     * @return {@code True} if at least one document exists otherwise {@code False}
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public boolean isExtensionInserted(String extension) throws OperationException {
        // Init result variable
        boolean res;
        // Create query
        Query query = new Query();
        query.addCriteria(
            where(FIELD_TYPE_ID_EXT).is(extension).and(FIELD_DELETED).is(false)
        );
        try {
            // Execute
            res = mongo.exists(query, SchemaETY.class);
        } catch(MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to verify if given extension is inserted" , e);
        }
        // Return data
        return res;
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
    public Map<String, SchemaETY> isDocumentInserted(String extension, List<String> filenames) throws OperationException {
        // Init empty collection
        Map<String, SchemaETY> out;
        // Create query
        Query query = new Query();
        query.addCriteria(where(FIELD_TYPE_ID_EXT).is(extension));
        query.addCriteria(where(FIELD_FILENAME).in(filenames));
        query.addCriteria(where(FIELD_DELETED).is(false));
        try {
            // Execute
            List<SchemaETY> entities = mongo.find(query, SchemaETY.class);
            // Convert to map
            out = entities.stream().collect(
                Collectors.toMap(SchemaETY::getNameSchema, entity -> entity)
            );
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to match documents with filenames for the given extension" , e);
        }
        // Bye bye
        return out;
    }

    /**
     * Inserts all the given entities inside the schema
     * @param entities The entities to insert
     * @return The entities inserted
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> insertDocsByExtensionId(List<SchemaETY> entities) throws OperationException {
        // Init inserted docs list
        List<SchemaETY> inserted;
        try {
            // Execute
            inserted = new ArrayList<>(mongo.insertAll(entities));
        } catch(MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to insert documents for the given extension" , e);
        }
        // Return processed data
        return inserted;
    }

    /**
     * Update all the given entities inside the schema
     * @param entities The new entities to replace with the old ones
     * @return The {@link BulkWriteResult} object
     * @throws OperationException If a data-layer error occurs
     */
    @Override
    public List<SchemaETY> updateDocsByExtensionId(List<SchemaETY> entities)
        throws OperationException, DataIntegrityException {
        // List to hold queries
        List<UpdateOneModel<Document>> queries = new ArrayList<>();
        BulkWriteResult result;
        // Create queries
        // They will be sent as a single call to the database
        for (SchemaETY e : entities) {
            // Verify not null
            Objects.requireNonNull(e.getId(), "The document id cannot be null");
            // Create query
            Query query = new Query();
            query.addCriteria(
                where(FIELD_ID).is(new ObjectId(e.getId()))
            );
            query.addCriteria(
                where(FIELD_DELETED).is(false)
            );
            // Creating query
            queries.add(new UpdateOneModel<>(
                    // Retrieve by id
                    query.getQueryObject(),
                    // Update operator
                    Updates.combine(
                        Updates.set(FIELD_LAST_UPDATE, e.getLastUpdateDate()),
                        Updates.set(FIELD_CONTENT, e.getContentSchema())
                    )
                )
            );
        }
        // Now, we reach the database instance with the queries
        try {
            // Retrieve schema document
            MongoCollection<Document> collection = mongo.getCollection(naming.getSchemaCollection());
            // Execute
            result = collection.bulkWrite(queries);
        }catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to update documents for the given extension" , e);
        }
        // Assert we modified the expected data size
        if(entities.size() != result.getMatchedCount() || entities.size() != result.getModifiedCount()) {
            throw new DataIntegrityException(
                String.format("Expected document count doesn't match modified document count on %s\nExpected: %d/Matched: %d/Modified: %d",
                    entities.get(0).getTypeIdExtension(), entities.size(), result.getMatchedCount(), result.getModifiedCount()
                )
            );
        }
        // Bye bye
        return entities;
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
        entities = findDocsByExtensionId(extension);
        try {
            // Execute
            result = mongo.updateMulti(query, update, SchemaETY.class);
        } catch(MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to delete documents by extension" , e);
        }
        // Assert we modified the expected data size
        if(entities.size() != result.getMatchedCount() || entities.size() != result.getModifiedCount()) {
            throw new DataIntegrityException(
                "Expected document count doesn't match deleted document count on " + entities.get(0).getTypeIdExtension()
            );
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
        // Working var
        SchemaETY object;
        // Create query
        Query q = query(where(FIELD_ID).is(new ObjectId(id)));
        try {
            // Execute
            object = mongo.findOne(q, SchemaETY.class);
        } catch (MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to retrieve the requested active document", e);
        }
        return object;
    }
}
