package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.*;

import java.io.Serializable;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.UploadDocumentsResDTO.UploadPayloadDTO;

@Value
@EqualsAndHashCode(callSuper = true)
public class UploadDocumentsResDTO extends ResponseDTO<UploadPayloadDTO> {

    int insertedSchema;
    @Getter
    @AllArgsConstructor
    public static class UploadPayloadDTO implements Serializable {
        private String extension;
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo      The {@link LogTraceInfoDTO} instance
     * @param data           The data object
     * @param insertedSchema
     */
    public UploadDocumentsResDTO(LogTraceInfoDTO traceInfo, UploadPayloadDTO data, int insertedSchema) {
        super(traceInfo, null);
        this.insertedSchema = insertedSchema;
    }
}
