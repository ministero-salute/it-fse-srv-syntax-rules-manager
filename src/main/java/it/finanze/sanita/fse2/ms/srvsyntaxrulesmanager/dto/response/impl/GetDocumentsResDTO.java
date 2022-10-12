package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;

import java.util.ArrayList;


public class GetDocumentsResDTO extends ResponseDTO {

    private ArrayList<SchemaDocumentDTO> documents;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data      The data object
     */
    public GetDocumentsResDTO(LogTraceInfoDTO traceInfo, ArrayList<SchemaDocumentDTO> data) {
        super(traceInfo);
        this.documents = data;
    }
}
