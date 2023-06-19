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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataProcessingException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_ETY_BINARY_CONVERSION;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IChangeSetRepo.*;

/**
 * Model to save schema documents
 */
@Document(collection = "#{@schemaBean}")
@Data
@NoArgsConstructor
public class SchemaETY {

    public static final String FIELD_ID = "_id";
    public static final String FIELD_FILENAME = "name_schema";
    public static final String FIELD_CONTENT = "content_schema";
    public static final String FIELD_TYPE_ID_EXT = "type_id_extension";
    public static final String FIELD_ROOT_SCHEMA = "root_schema";

    @Id
    private String id;
    @Field(name = FIELD_FILENAME)
    private String nameSchema;
    @Field(name = FIELD_CONTENT)
    private Binary contentSchema;
    @Field(name = FIELD_TYPE_ID_EXT)
    private String typeIdExtension;
    @Field(name = FIELD_ROOT_SCHEMA)
    private Boolean rootSchema;
    @Field(name = FIELD_INSERTION_DATE)
    private Date insertionDate;
    @Field(name = FIELD_LAST_UPDATE)
    private Date lastUpdateDate;
    @Field(name = FIELD_DELETED)
    private boolean deleted;

    public void setContentSchema(MultipartFile file) throws DataProcessingException {
        try {
            this.contentSchema = new Binary(file.getBytes());
        } catch (IOException e) {
            throw new DataProcessingException(ERR_ETY_BINARY_CONVERSION, e);
        }
    }

    public void setContentSchema(Path path) throws DataProcessingException {
        try {
            this.contentSchema = new Binary(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new DataProcessingException(ERR_ETY_BINARY_CONVERSION, e);
        }
    }

    public static SchemaETY fromMultipart(MultipartFile file, String extension, boolean root) throws DataProcessingException {
        SchemaETY entity = new SchemaETY();
        Date now = new Date();
        entity.setNameSchema(file.getOriginalFilename());
        entity.setContentSchema(file);
        entity.setTypeIdExtension(extension);
        entity.setRootSchema(root);
        entity.setInsertionDate(now);
        entity.setLastUpdateDate(now);
        return entity;
    }

    public static SchemaETY fromPath(Path path, String extension, boolean root) throws DataProcessingException {
        SchemaETY entity = new SchemaETY();
        Date now = new Date();
        entity.setNameSchema(path.getFileName().toString());
        entity.setContentSchema(path);
        entity.setTypeIdExtension(extension);
        entity.setRootSchema(root);
        entity.setInsertionDate(now);
        entity.setLastUpdateDate(now);
        return entity;
    }

}
