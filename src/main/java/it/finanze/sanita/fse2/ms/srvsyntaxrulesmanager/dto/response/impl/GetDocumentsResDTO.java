/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO.Options;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.GetDocumentsResDTO.GetMultipleDocsPayloadDTO;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;

public class GetDocumentsResDTO extends ResponseDTO<GetMultipleDocsPayloadDTO> {

    @Getter
    public static class GetMultipleDocsPayloadDTO implements Serializable {
        /**
         * Documents retrieved
         */
        @ArraySchema(
                minItems = OA_ARRAY_FILES_MIN,
                maxItems = OA_ARRAY_FILES_MAX,
                schema = @Schema(maxLength = OA_ANY_STRING_MAX)
        )
        private final ArrayList<SchemaDocumentDTO> documents;

        public GetMultipleDocsPayloadDTO(ArrayList<SchemaDocumentDTO> documents, Options options) {
            this.documents = applyOptions(documents, options);
        }

        private ArrayList<SchemaDocumentDTO> applyOptions(ArrayList<SchemaDocumentDTO> documents, Options options) {
            return documents.stream().map(d -> d.applyOptions(options)).collect(Collectors.toCollection(ArrayList::new));
        }
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
