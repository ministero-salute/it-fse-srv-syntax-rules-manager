package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteDocumentsResDTO extends ResponseDTO {
    private int deletedSchema;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param deletedSchema     
     */
    public DeleteDocumentsResDTO(LogTraceInfoDTO traceInfo, int deletedSchema) {
        super(traceInfo);
        this.deletedSchema = deletedSchema;
    }
}
