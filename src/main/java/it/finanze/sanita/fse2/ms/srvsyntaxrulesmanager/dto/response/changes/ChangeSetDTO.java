/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes;

import lombok.Value;
import org.bson.types.ObjectId;

import javax.validation.constraints.Size;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.OA_ANY_STRING_MAX;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.OA_ANY_STRING_MIN;

/**
 * The changeset item DTO
 */
@Value
public class ChangeSetDTO {

    /**
     * Identifier, usually an {@link ObjectId}
     */
    @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
    String id;
    /**
     * Holds human-readable information, differ by each microservice
     */
    Payload description;

    /**
     * The changeset item payload DTO
     */
    @Value
    public static class Payload {
        /**
         * The resource extension identifier
         */
        @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
        String extension;
        /**
         * The resource filename
         */
        @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX)
        String filename;
    }
}
