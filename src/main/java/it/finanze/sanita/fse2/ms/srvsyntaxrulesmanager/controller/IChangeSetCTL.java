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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.changeset.GetChangeSet;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.ChangeSetResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.NoFutureDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_VAL_FUTURE_DATE;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.RoutesUtility.*;

/**
 * ChangeSet retriever controller
 *
 */
@Tag(name = API_CHANGESET_TAG)
@Validated
public interface IChangeSetCTL {

    @GetChangeSet
    @GetMapping(API_CHANGESET_STATUS)
    ChangeSetResDTO changeSet(
        @RequestParam(value=API_QP_LAST_UPDATE, required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @NoFutureDate(message = ERR_VAL_FUTURE_DATE)
        Date lastUpdate
    ) throws OperationException;
}
