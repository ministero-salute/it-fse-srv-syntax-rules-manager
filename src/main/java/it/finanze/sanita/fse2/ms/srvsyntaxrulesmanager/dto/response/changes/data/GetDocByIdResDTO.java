/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.data;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;

@Getter
public class GetDocByIdResDTO extends ResponseDTO {

    @Schema(implementation = SchemaDocumentDTO.class)
    private final SchemaDocumentDTO document;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param document      The data object
     */
    public GetDocByIdResDTO(LogTraceInfoDTO traceInfo, SchemaDocumentDTO document) {
        super(traceInfo);
        this.document = document;
    }
}
