/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.mongo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Database properties
 */
@Data
@Component
public class MongoPropertiesCFG implements Serializable {

    /**
     *  Serial version uid
     */
    private static final long serialVersionUID = 3858996306276323941L;

    /**
     *  Connection string
     */
    @Value("${data.mongodb.uri}")
    private String uri;
}
