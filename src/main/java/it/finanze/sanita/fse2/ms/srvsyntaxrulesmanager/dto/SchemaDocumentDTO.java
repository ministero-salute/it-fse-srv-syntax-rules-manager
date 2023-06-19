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

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.MiscUtility.convertToOffsetDateTime;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.MiscUtility.encodeBase64;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDocumentDTO {
    @Schema(maxLength = OA_ANY_STRING_MAX)
    private String id;
    @Schema(maxLength = OA_ANY_STRING_MAX)
    private String nameSchema;
    @Schema(maxLength = OA_FILE_CONTENT_MAX)
    private String contentSchema;
    @Schema(maxLength = OA_EXTS_STRING_MAX)
    private String typeIdExtension;
    private Boolean rootSchema;
    private OffsetDateTime insertionDate;

    private OffsetDateTime lastUpdateDate;

    private boolean deleted;

    @AllArgsConstructor
    public static class Options {
        private final boolean binary;
    }

    public static SchemaDocumentDTO fromEntity(SchemaETY e) {
        return new SchemaDocumentDTO(
            e.getId(),
            e.getNameSchema(),
            encodeBase64(e.getContentSchema().getData()),
            e.getTypeIdExtension(),
            e.getRootSchema(),
            convertToOffsetDateTime(e.getInsertionDate()),
            convertToOffsetDateTime(e.getLastUpdateDate()),
            e.isDeleted()
        );
    }

    public SchemaDocumentDTO applyOptions(Options o) {
        if(!o.binary) contentSchema = null;
        return this;
    }

}
