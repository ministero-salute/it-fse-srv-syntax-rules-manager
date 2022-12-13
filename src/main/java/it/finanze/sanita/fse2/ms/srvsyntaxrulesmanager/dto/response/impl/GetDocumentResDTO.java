/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class GetDocumentResDTO extends ResponseDTO {

    @Getter
    @AllArgsConstructor
    public static class GetOneDocPayloadDTO implements Serializable {
        /**
         * Documents retrieved
         */
        @Schema(implementation = SchemaDocumentDTO.class)
        private SchemaDocumentDTO document;
    }

    private final GetOneDocPayloadDTO data;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data      The data object
     */
    public GetDocumentResDTO(LogTraceInfoDTO traceInfo, GetOneDocPayloadDTO data) {
        super(traceInfo);
        this.data = data;
    }
}
