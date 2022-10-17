package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import javax.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Max;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeleteDocumentsResDTO extends ResponseDTO {
    
    @Schema(description = "Numero di schema eliminati")
    @Min(1)
    @Max(1000)
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
