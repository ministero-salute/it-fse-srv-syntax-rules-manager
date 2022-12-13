/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.ResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class DeleteDocumentsResDTO extends ResponseDTO {
    int deletedSchema;

    /**
     * Instantiates a new response DTO.
     *
     * @param traceInfo     The {@link LogTraceInfoDTO} instance
     * @param deletedSchema
     */
    public DeleteDocumentsResDTO(LogTraceInfoDTO traceInfo, int deletedSchema) {
        super(traceInfo);
        this.deletedSchema = deletedSchema;
    }
}
