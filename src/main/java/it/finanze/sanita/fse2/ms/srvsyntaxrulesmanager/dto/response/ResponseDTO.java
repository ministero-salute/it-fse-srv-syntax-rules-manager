package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.AbstractDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.OA_IDS_SIZE_MAX;

/**
 * Base response
 * @author G. Baittiner
 */
@Getter
public abstract class ResponseDTO implements AbstractDTO {

    /**
     * Trace id log.
     */
    @Schema(maxLength = OA_IDS_SIZE_MAX)
    private final String traceID;

    /**
     * Span id log.
     */
    @Schema(maxLength = OA_IDS_SIZE_MAX)
    private final String spanID;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data The data object
     */
    protected ResponseDTO(final LogTraceInfoDTO traceInfo) {
        this.traceID = traceInfo.getTraceID();
        this.spanID = traceInfo.getSpanID();
    }
}