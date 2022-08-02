package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Collections.SCHEMA;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Profile.TEST_PREFIX;

@Slf4j
@Configuration
public class CollectionNaming {
    @Autowired
    private UtilsProfile profiles;

    @Bean("schemaBean")
    public String getSchemaCollection() {
        return profiles.isTestProfile() ? TEST_PREFIX + SCHEMA : SCHEMA;
    }
}