/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.base;

import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.AbstractDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Data;
import org.apache.http.HttpStatus;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.OA_ANY_STRING_MAX;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.OA_IDS_SIZE_MAX;

/**
 * Error response
 */
@Data
public class ErrorResponseDTO implements AbstractDTO {

    /**
     * Trace id log.
     */
    @Schema(
        description = "Identificativo univoco della richiesta dell'utente",
        maxLength = OA_IDS_SIZE_MAX
    )
    private String traceID;

    /**
     * Span id log.
     */
    @Schema(
        description = "Identificativo univoco di un task della richiesta dell'utente (differisce dal traceID solo in caso di chiamate sincrone in cascata)",
        maxLength = OA_IDS_SIZE_MAX
    )
    private String spanID;

    /**
     * Issue identifier
     */
    @Schema(
        description = "Identificativo del problema verificatosi",
        maxLength = OA_ANY_STRING_MAX
    )
    private String type;

    /**
     * Issue summary
     */
    @Schema(
        description = "Sintesi del problema (invariante per occorrenze diverse dello stesso problema)",
        maxLength = OA_ANY_STRING_MAX
    )
    private String title;

    /**
     * Issue description
     */
    @Schema(
        description = "Descrizione del problema",
        maxLength = OA_ANY_STRING_MAX
    )
    private String detail;

    /**
     * HTTP status
     */
    @Schema(
        description = "Stato http",
        minimum = HttpStatus.SC_CONTINUE + "",
        maximum = HttpStatus.SC_INSUFFICIENT_STORAGE + ""
    )
    private Integer status;

    /**
     * Additional information
     */
    @Schema(
        description = "URI che potrebbe fornire ulteriori informazioni riguardo l'occorrenza del problema",
        maxLength = OA_ANY_STRING_MAX
    )
    private String instance;

    public ErrorResponseDTO(final LogTraceInfoDTO traceInfo, final String inType, final String inTitle, final String inDetail, final Integer inStatus, final String inInstance) {
        traceID = traceInfo.getTraceID();
        spanID = traceInfo.getSpanID();
        type = inType;
        title = inTitle;
        detail = inDetail;
        status = inStatus;
        instance = inInstance;
    }

}

