/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClients;

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
    	  ConnectionString connectionString = new ConnectionString(props.getUri());
          MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
              .applyConnectionString(connectionString)
              .build();
          return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongoClientSettings), props.getSchemaName());
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
