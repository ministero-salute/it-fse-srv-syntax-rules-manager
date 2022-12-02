/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityMisc.convertToOffsetDateTime;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityMisc.encodeBase64;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDocumentDTO implements Serializable {
    @Schema(maxLength = OA_ANY_STRING_MAX)
    private String id;
    @Schema(maxLength = OA_ANY_STRING_MAX)
    private String nameSchema;
    @Schema(maxLength = OA_FILE_CONTENT_MAX)
    private String contentSchema;
    @Schema(maxLength = OA_EXTS_STRING_MAX)
    private String typeIdExtension;
    private Boolean rootSchema;
    private OffsetDateTime lastUpdateDate;

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
            convertToOffsetDateTime(e.getLastUpdateDate())
        );
    }

    public SchemaDocumentDTO applyOptions(Options o) {
        if(!o.binary) contentSchema = null;
        return this;
    }

}
