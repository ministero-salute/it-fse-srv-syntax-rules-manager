/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;

/**
 * DTO for Change Set status endpoint response.
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeSetResDTO {

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
     * Last update date to consider while retrieving the change set items
     */
    private Date lastUpdate;
    /**
     * The response date-time (usually used as the next lastUpdate)
     */
    private Date timestamp;

    /**
     * List containing all items inserted since the lastUpdate
     */
    @ArraySchema(minItems = OA_ARRAY_CHANGESET_MIN, maxItems = OA_ARRAY_CHANGESET_MAX, uniqueItems = true)
    private List<ChangeSetDTO> insertions;

    /**
     * List containing all items deleted since the lastUpdate
     */
    @ArraySchema(minItems = OA_ARRAY_CHANGESET_MIN, maxItems = OA_ARRAY_CHANGESET_MAX, uniqueItems = true)
    private List<ChangeSetDTO> deletions;

    /**
     * The total number of items returned (inserted/modified/deleted)
     */
    @Schema(minimum = OA_ARRAY_CHANGESET_MIN + "", maximum = OA_ARRAY_CHANGESET_MAX + "")
    private long totalNumberOfElements;

    /**
     * The current active collection items
     */
    private long collectionSize;

}