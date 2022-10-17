package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.UpdateDocumentsResDTO.UpdatePayloadDTO;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;

public class UpdateDocumentsResDTO extends ResponseDTO<UpdatePayloadDTO> {
    @Getter
    @AllArgsConstructor
    public static class UpdatePayloadDTO implements Serializable {
        /**
         * Extension identifier
         */
        @Schema(maxLength = OA_EXTS_STRING_MAX)
        private String extension;
        /**
         * Elements updated
         */
        @ArraySchema(
                minItems = OA_ARRAY_FILES_MIN,
                maxItems = OA_ARRAY_FILES_MAX,
                schema = @Schema(maxLength = OA_ANY_STRING_MAX)
        )
        private int updatedSchema;
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data      The data object
     */
    public UpdateDocumentsResDTO(LogTraceInfoDTO traceInfo, UpdatePayloadDTO data) {
        super(traceInfo, data);
    }
}
