/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.mongo;

import com.mongodb.ReadConcern;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Factory to create database instances
 */
@Configuration
@EnableMongoRepositories(basePackages = Constants.ComponentScan.CONFIG_MONGO)
public class MongoDatabaseCFG {

    /**
     * Creates a new factory instance with the given connection string (properties.yml)
     * @return The new {@link SimpleMongoClientDatabaseFactory} instance
     */
    @Bean
    public MongoDatabaseFactory createFactory(MongoPropertiesCFG props) {
        return new SimpleMongoClientDatabaseFactory(props.getUri());
    }

    /**
     * Create a new transaction manager according to the specs
     * @param factory The factory interface instance
     * @return The transaction manager
     */
    @Bean
    public MongoTransactionManager createTransactions(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(
            factory,
            TransactionOptions.builder()
                .readConcern(ReadConcern.DEFAULT)
                // We ask MongoDB to wait for the primary cluster confirmation
                // in order to validate the operation
                .writeConcern(WriteConcern.W1)
                .build()
        );
    }

    /**
     * Creates a new template instance used to perform operations on the schema
     * @return The new {@link MongoTemplate} instance
     */
    @Bean
    @Primary
    public MongoTemplate createTemplate(ApplicationContext context, MongoPropertiesCFG props) {
        // Create new connection instance
        MongoDatabaseFactory factory = createFactory(props);
        // Create mapping context
        MongoMappingContext mapper = new MongoMappingContext();
        // Set application context
        mapper.setApplicationContext(context);
        // Apply default mapper
        MappingMongoConverter converter = new MappingMongoConverter(
            new DefaultDbRefResolver(factory),
            mapper
        );
        // Set the default type mapper (removes custom "_class" column)
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        // Return the new instance
        return new MongoTemplate(factory, converter);
    }

}
