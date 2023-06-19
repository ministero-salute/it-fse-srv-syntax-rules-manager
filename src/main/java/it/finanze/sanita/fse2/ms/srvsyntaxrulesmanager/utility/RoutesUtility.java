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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility;

public final class RoutesUtility {

    /**
     * Private constructor to disallow to access from other classes
     */
    private RoutesUtility() {}

    public static final String API_VERSION = "v1";
    public static final String API_CHANGESET = "changeset";
    public static final String API_QP_LAST_UPDATE = "lastUpdate";
    public static final String API_STATUS = "status";
    public static final String API_SCHEMA = "schema";
    public static final String API_ID = "id";
    public static final String API_DOCUMENT_MAPPER = "/" + API_VERSION + "/" + API_SCHEMA;

    public static final String API_CHANGESET_STATUS = "/" + API_VERSION + "/" + API_CHANGESET + "/" + API_SCHEMA + "/" + API_STATUS ;
    public static final String API_PARAM_ROOT = "root";
    public static final String API_PARAM_FILES = "files";
    public static final String API_PATH_ID_VAR = "id";
    public static final String API_QP_INCLUDE_DELETED = "includeDeleted";
    public static final String API_QP_BINARY = "includeBinary";
    public static final String API_PATH_EXTS_VAR = "extension";
    public static final String API_PATH_EXTS = "/{" + API_PATH_EXTS_VAR + "}";
    public static final String API_ID_EXTS = "/{" + API_PATH_ID_VAR + "}";
    public static final String API_GET_BY_EXTS = API_PATH_EXTS;
    public static final String API_DELETE_BY_EXTS = API_PATH_EXTS;
    public static final String API_GET_ONE_BY_ID = API_ID + API_ID_EXTS;
    public static final String API_GET_ONE_BY_ID_FULL = API_DOCUMENT_MAPPER + "/" + API_GET_ONE_BY_ID;
    public static final String API_GET_BY_EXTS_FULL = API_DOCUMENT_MAPPER + API_GET_BY_EXTS;
    public static final String API_DELETE_BY_EXTS_FULL = API_DOCUMENT_MAPPER + API_DELETE_BY_EXTS;
    public static final String API_CHANGESET_TAG = "ChangeSet";
    public static final String API_DOCUMENTS_TAG = "Documents";

}
