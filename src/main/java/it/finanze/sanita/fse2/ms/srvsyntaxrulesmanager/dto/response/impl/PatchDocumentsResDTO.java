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
public class PatchDocumentsResDTO extends ResponseDTO {
    
    @Schema(description = "Numero di schema aggiornati")
    @Min(1)
    @Max(1000)
    private int patchedSchema;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     */
    public PatchDocumentsResDTO(LogTraceInfoDTO traceInfo, int patchedSchema) {
        super(traceInfo);
        this.patchedSchema = patchedSchema;
    }
}
