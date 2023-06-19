/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "#{@schemaBean}")
@Data
@NoArgsConstructor
public class ExtensionETY {

    public static final String FIELD_ITEMS = "items";

    private String id;
    private List<SchemaETY> items;
}