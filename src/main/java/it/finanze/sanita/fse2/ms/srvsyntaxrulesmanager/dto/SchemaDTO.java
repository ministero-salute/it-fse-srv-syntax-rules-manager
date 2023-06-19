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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.ExtensionETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_DTO_EMPTY_ITEMS;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_DTO_NO_ROOT_FOUND;
import static java.util.Comparator.comparing;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDTO {

    private String nameRoot;
    private String typeIdExtension;
    private OffsetDateTime insertionDate;
    private OffsetDateTime lastUpdateDate;
    private boolean deleted;
    private List<SchemaDocumentDTO> content;

    public static SchemaDTO fromExtension(ExtensionETY ext, SchemaDocumentDTO.Options options) {
        return fromItems(
            ext.getId(),
            ext.getItems().stream().map(SchemaDocumentDTO::fromEntity).collect(Collectors.toList()),
            options
        );
    }

    public static SchemaDTO fromItems(
        String typeIdExtension,
        List<SchemaDocumentDTO> items,
        SchemaDocumentDTO.Options options
    ) {
        SchemaDTO schema = new SchemaDTO();
        schema.setTypeIdExtension(typeIdExtension);
        schema.setNameRoot(extractRoot(typeIdExtension, items));
        schema.setInsertionDate(extractInsertionDate(typeIdExtension, items));
        schema.setLastUpdateDate(extractLastUpdateDate(typeIdExtension, items));
        schema.setDeleted(extractDeleted(typeIdExtension, items));
        schema.setContent(applyOptions(items, options));
        return schema;
    }

    private static String extractRoot(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Two cases
        // [1] Everything has been deleted
        // [2] Not everything has been deleted
        String name;
        boolean deleted = items.stream().allMatch(SchemaDocumentDTO::isDeleted);

        if(deleted) {
            // Just check for any root schema
            name = items.stream().filter(SchemaDocumentDTO::getRootSchema).findFirst().orElseThrow(
                () -> new IllegalStateException(String.format(ERR_DTO_NO_ROOT_FOUND, typeIdExtension))
            ).getNameSchema();
        } else {
            // Otherwise check for not-deleted root schema
            name = items.stream().filter(i -> !i.isDeleted() && i.getRootSchema()).findFirst().orElseThrow(
                () -> new IllegalStateException(String.format(ERR_DTO_NO_ROOT_FOUND, typeIdExtension))
            ).getNameSchema();
        }
        return name;
    }

    private static OffsetDateTime extractInsertionDate(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Emptiness check
        if(items.isEmpty()) throw new IllegalStateException(String.format(ERR_DTO_EMPTY_ITEMS, typeIdExtension));
        // Sort at run-time
        items.sort(comparing(SchemaDocumentDTO::getInsertionDate));
        // Extract
        return items.get(0).getInsertionDate();
    }

    private static OffsetDateTime extractLastUpdateDate(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Emptiness check
        if(items.isEmpty()) throw new IllegalStateException(String.format(ERR_DTO_EMPTY_ITEMS, typeIdExtension));
        // Sort at run-time
        items.sort(comparing(SchemaDocumentDTO::getLastUpdateDate).reversed());
        // Extract
        return items.get(0).getLastUpdateDate();
    }

    private static boolean extractDeleted(String typeIdExtension, List<SchemaDocumentDTO> items) {
        // Emptiness check
        if(items.isEmpty()) throw new IllegalStateException(String.format(ERR_DTO_EMPTY_ITEMS, typeIdExtension));
        // Extract
        return items.stream().allMatch(SchemaDocumentDTO::isDeleted);
    }

    private static List<SchemaDocumentDTO> applyOptions(List<SchemaDocumentDTO> items, SchemaDocumentDTO.Options options) {
        // Apply
        items.forEach(d -> d.applyOptions(options));
        // Return initial object
        return items;
    }

}
