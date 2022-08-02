package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.*;

public class GetDocumentsResDTO extends ResponseDTO<GetDocumentsResDTO.GetMultipleDocsPayloadDTO> {

    @Getter
    @AllArgsConstructor
    public static class GetMultipleDocsPayloadDTO implements Serializable {
        /**
         * Documents retrieved
         */
        @ArraySchema(
            minItems = OA_ARRAY_FILES_MIN,
            maxItems = OA_ARRAY_FILES_MAX,
            schema = @Schema(maxLength = OA_ANY_STRING_MAX)
        )
        private ArrayList<SchemaDocumentDTO> documents;
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data      The data object
     */
    public GetDocumentsResDTO(LogTraceInfoDTO traceInfo, GetMultipleDocsPayloadDTO data) {
        super(traceInfo, data);
    }
}
