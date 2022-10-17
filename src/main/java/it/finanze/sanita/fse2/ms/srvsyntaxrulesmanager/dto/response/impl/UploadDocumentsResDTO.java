package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UploadDocumentsResDTO extends ResponseDTO {

	@Schema(description = "Numero di schema inseriti")
    @Min(1)
    @Max(1000)
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
