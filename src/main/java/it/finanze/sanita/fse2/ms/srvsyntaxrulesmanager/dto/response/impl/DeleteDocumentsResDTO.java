package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.DeleteDocumentsResDTO.DeletePayloadDTO;

@Value
@EqualsAndHashCode(callSuper = true)
public class DeleteDocumentsResDTO extends ResponseDTO<DeletePayloadDTO> implements Serializable {
    int deletedSchema;

    @Getter
    @AllArgsConstructor
    public static class DeletePayloadDTO implements Serializable {
        private String extension;
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo     The {@link LogTraceInfoDTO} instance
     * @param data          The data object
     * @param deletedSchema
     */
    public DeleteDocumentsResDTO(LogTraceInfoDTO traceInfo, DeletePayloadDTO data, int deletedSchema) {
        super(traceInfo, null);
        this.deletedSchema = deletedSchema;
    }
}
