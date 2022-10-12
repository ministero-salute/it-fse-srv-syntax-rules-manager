package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;

public class UpdateDocumentsResDTO extends ResponseDTO {
    int updatedSchema;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     */
    public UpdateDocumentsResDTO(LogTraceInfoDTO traceInfo, int updatedSchema) {
        super(traceInfo);
        this.updatedSchema = updatedSchema;
    }
}
