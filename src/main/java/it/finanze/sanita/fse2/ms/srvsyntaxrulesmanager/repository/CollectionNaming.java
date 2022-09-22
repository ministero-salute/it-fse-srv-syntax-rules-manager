package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Collections.SCHEMA;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Profile.TEST_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsProfile;

@Configuration
public class CollectionNaming {
    @Autowired
    private UtilsProfile profiles;

    @Bean("schemaBean")
    public String getSchemaCollection() {
        return profiles.isTestProfile() ? TEST_PREFIX + SCHEMA : SCHEMA;
    }
}