/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Collections.SCHEMA;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Profile.TEST_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityProfile;

@Configuration
public class CollectionNaming {
    @Autowired
    private UtilityProfile profiles;

    @Bean("schemaBean")
    public String getSchemaCollection() {
        return profiles.isTestProfile() ? TEST_PREFIX + SCHEMA : SCHEMA;
    }
}