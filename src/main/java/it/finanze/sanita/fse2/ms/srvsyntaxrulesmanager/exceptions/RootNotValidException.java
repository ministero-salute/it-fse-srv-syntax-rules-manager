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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

import lombok.Getter;

/**
 * If the given root filename doesn't match one of the provided schema files
 */
@Getter
public class RootNotValidException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = -5864543132277838362L;

    private final String field;

    /**
     * Message constructor.
     *
     * @param msg    Message to be shown.
     * @param field  Field name
     */
    public RootNotValidException(final String msg, final String field) {
        super(msg);
        this.field = field;
    }
}
