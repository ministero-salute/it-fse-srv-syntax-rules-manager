package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class UploadDocumentsResDTO extends ResponseDTO {

    private int insertedSchema;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     */
    public UploadDocumentsResDTO(LogTraceInfoDTO traceInfo, int inInsertedSchema) {
        super(traceInfo);
        this.insertedSchema = inInsertedSchema;
    }
}
