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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface IChangeSetSRV {
    /**
     * Retrieves the latest insertions according to the given timeframe
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing insertions
     * @throws OperationException If a data-layer error occurs
     */
    List<ChangeSetDTO> getInsertions(@Nullable Date lastUpdate) throws OperationException;

    /**
     * Retrieves the latest deletions according to the given timeframe
     * @param lastUpdate The timeframe to consider while calculating
     * @return The missing deletions
     * @throws OperationException If a data-layer error occurs
     */
    List<ChangeSetDTO> getDeletions(@Nullable Date lastUpdate) throws OperationException;

    /**
     * Retrieves the expected collection size after the alignment
     * @return The collection size
     * @throws OperationException If a data-layer error occurs
     */
    long getCollectionSize() throws OperationException;
}
