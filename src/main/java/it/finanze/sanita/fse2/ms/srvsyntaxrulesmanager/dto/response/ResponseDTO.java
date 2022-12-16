/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.AbstractDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.OA_IDS_SIZE_MAX;

/**
 * Base response
 */
@Getter
@NoArgsConstructor
public abstract class ResponseDTO implements AbstractDTO {

    /**
     * Trace id log.
     */
    @Schema(maxLength = OA_IDS_SIZE_MAX)
    private String traceID;

    /**
     * Span id log.
     */
    @Schema(maxLength = OA_IDS_SIZE_MAX)
    private String spanID;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     */
    protected ResponseDTO(final LogTraceInfoDTO traceInfo) {
        this.traceID = traceInfo.getTraceID();
        this.spanID = traceInfo.getSpanID();
    }
}