package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.DeleteDocumentsResDTO.DeletePayloadDTO;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;

public class DeleteDocumentsResDTO extends ResponseDTO<DeletePayloadDTO> {
    @Getter
    @AllArgsConstructor
    public static class DeletePayloadDTO implements Serializable {
        /**
         * Extension identifier
         */
        @Schema(maxLength = OA_EXTS_STRING_MAX)
        private String extension;
        /**
         * Elements uploaded
         */
        @ArraySchema(
                minItems = OA_ARRAY_FILES_MIN,
                maxItems = OA_ARRAY_FILES_MAX,
                schema = @Schema(maxLength = OA_ANY_STRING_MAX)
        )
        private int deletedSchema;
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data      The data object
     */
    public DeleteDocumentsResDTO(LogTraceInfoDTO traceInfo, DeletePayloadDTO data) {
        super(traceInfo, data);
    }
}
