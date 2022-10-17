package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UpdateDocumentsResDTO extends ResponseDTO {
    
    @Schema(description = "Numero di schema inseriti in aggiornamento")
    @Min(1)
    @Max(1000)
    private int updatedSchema;

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
