package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.UpdateDocumentsResDTO.UpdatePayloadDTO;

@Value
@EqualsAndHashCode(callSuper = true)
public class UpdateDocumentsResDTO extends ResponseDTO<UpdatePayloadDTO> implements Serializable {
    int updatedSchema;
    @Getter
    @AllArgsConstructor
    public static class UpdatePayloadDTO implements Serializable {
        private String extension;
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo      The {@link LogTraceInfoDTO} instance
     * @param data           The data object
     * @param updatedSchema
     */
    public UpdateDocumentsResDTO(LogTraceInfoDTO traceInfo, UpdatePayloadDTO data, int updatedSchema) {
        super(traceInfo, null);
        this.updatedSchema = updatedSchema;
    }}
