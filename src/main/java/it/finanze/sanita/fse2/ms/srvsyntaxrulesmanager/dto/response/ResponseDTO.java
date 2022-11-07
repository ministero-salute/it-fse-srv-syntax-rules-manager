/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.AbstractDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.OA_IDS_SIZE_MAX;

/**
 * Base response
 */
@Getter
public abstract class ResponseDTO<T> implements AbstractDTO {

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
     * Data object
     */
    private final T data;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param data The data object
     */
    protected ResponseDTO(final LogTraceInfoDTO traceInfo, T data) {
        this.traceID = traceInfo.getTraceID();
        this.spanID = traceInfo.getSpanID();
        this.data = data;
    }
}