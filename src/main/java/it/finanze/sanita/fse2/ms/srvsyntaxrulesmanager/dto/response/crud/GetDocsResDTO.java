/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.crud;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.Getter;

import java.util.List;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.*;
import static java.util.Collections.singletonList;

@Getter
public class GetDocsResDTO extends ResponseDTO {

    @ArraySchema(
            minItems = OA_ARRAY_FILES_MIN,
            maxItems = OA_ARRAY_FILES_MAX,
            schema = @Schema(maxLength = OA_ANY_STRING_MAX)
    )
    private final List<SchemaDTO> items;

    private final long numberOfItems;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param item     The available document object
     */
    public GetDocsResDTO(LogTraceInfoDTO traceInfo, SchemaDTO item) {
        super(traceInfo);
        List<SchemaDTO> items = singletonList(item);
        this.items = items;
        this.numberOfItems = items.size();
    }

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo The {@link LogTraceInfoDTO} instance
     * @param items     The available documents object
     */
    public GetDocsResDTO(LogTraceInfoDTO traceInfo, List<SchemaDTO> items) {
        super(traceInfo);
        this.items = items;
        this.numberOfItems = items.size();
    }

}
