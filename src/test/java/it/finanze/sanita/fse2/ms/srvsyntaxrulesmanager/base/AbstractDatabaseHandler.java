package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.base;

import com.mongodb.MongoException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Collections.SCHEMA;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Profile.TEST_PREFIX;

public abstract class AbstractDatabaseHandler extends AbstractEntityHandler {

    /**
     * Represent the initialisation process status.
     * It's used to handle inconsistency which could be generated
     * if test methods are not called correctly (call-order).
     */
    private enum InitState {
        BOOTING,
        NONE,
        READY
    }
    /**
     * Test collection name
     */
    public static final String SCHEMA_TEST_COLLECTION = TEST_PREFIX + SCHEMA;

    /**
     * The number of schemas available into the database after {@code setupTestRepository()}
     * is called
     */
    public static final int SCHEMA_INTO_DB = 30;

    @Autowired
    protected MongoTemplate mongo;

    private InitState state;

    protected AbstractDatabaseHandler() {
        this.state = InitState.NONE;
    }

    protected void setupTestRepository() throws IOException, OperationException {
        if(this.state == InitState.NONE) {
            // Mark as booting
            this.state = InitState.BOOTING;
            // Verify if previous test database exists
            if (isTestSchemaAvailable()) {
                // Drop it, we are going to create a new one
                dropTestSchema();
            }
            // Make test collection
            createTestSchema();
            // Add entities to map instance
            setupTestEntities();
            // Add sample documents
            addTestEntityToSchema(SCHEMA_TEST_EXTS_A);
            addTestEntityToSchema(SCHEMA_TEST_EXTS_B);
            addTestEntityToSchema(SCHEMA_TEST_EXTS_C);
            // Check as init
            this.state = InitState.READY;
        }
    }

    protected void clearTestRepository() {
        this.dropTestSchema();
        this.clearTestEntities();
    }

    protected void dropTestSchema() {
        switch (this.state) {
            case BOOTING:
                mongo.dropCollection(SCHEMA_TEST_COLLECTION);
                break;
            case READY:
                mongo.dropCollection(SCHEMA_TEST_COLLECTION);
                this.state = InitState.NONE;
                break;
            case NONE:
                throw new IllegalStateException("You cannot drop the schema without initialisation");
        }
    }

    private boolean isTestSchemaAvailable() {
        return mongo.getCollectionNames().contains(SCHEMA_TEST_COLLECTION);
    }

    private void createTestSchema() {
        if(state == InitState.READY) {
            throw new IllegalStateException("You cannot create the schema after the initialisation");
        }
        mongo.createCollection(SCHEMA_TEST_COLLECTION);
    }

    private void addTestEntityToSchema(String extension) throws OperationException {
        try {
            // Insert
            mongo.insert(getEntities().get(extension).values(), SCHEMA_TEST_COLLECTION);
        }catch(MongoException e) {
            // Catch data-layer runtime exceptions and turn into a checked exception
            throw new OperationException("Unable to insert test documents for the given extension" , e);
        }
    }
}
