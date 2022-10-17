package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityMisc;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;

@Data
@AllArgsConstructor
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

    public static SchemaDocumentDTO fromEntity(SchemaETY e) {
        return new SchemaDocumentDTO(
            e.getId(),
            e.getNameSchema(),
            UtilityMisc.encodeBase64(e.getContentSchema().getData()),
            e.getTypeIdExtension(),
            e.getRootSchema(),
            UtilityMisc.convertToOffsetDateTime(e.getLastUpdateDate())
        );
    }

}
