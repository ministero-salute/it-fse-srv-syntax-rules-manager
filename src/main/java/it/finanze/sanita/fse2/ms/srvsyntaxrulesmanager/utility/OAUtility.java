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

public final class OAUtility {

    /**
     * Private constructor to disallow to access from other classes
     */
    private OAUtility() {}

    /**
     * Min size extension string
     */
    public static final int OA_EXTS_STRING_MIN = 1;

    /**
     * Max size extension string
     */
    public static final int OA_EXTS_STRING_MAX = 25;

    /**
     * Max size given string ids
     */
    public static final int OA_IDS_SIZE_MAX = 255;

    /**
     * Min size given string
     */
    public static final int OA_ANY_STRING_MIN = 1;

    /**
     * Max size given string
     */
    public static final int OA_ANY_STRING_MAX = 255;

    /**
     * Min array size files
     */
    public static final int OA_ARRAY_FILES_MIN = 1;
    /**
     * Max array size files
     */
    public static final int OA_ARRAY_FILES_MAX = 25;

    /**
     * Min change set array files
     */
    public static final int OA_ARRAY_CHANGESET_MIN = 0;
    /**
     * Max change set array files
     */
    public static final int OA_ARRAY_CHANGESET_MAX = 100;

    /**
     * Max file size
     */
    public static final int OA_FILE_CONTENT_MAX = Integer.MAX_VALUE / 2;

}
