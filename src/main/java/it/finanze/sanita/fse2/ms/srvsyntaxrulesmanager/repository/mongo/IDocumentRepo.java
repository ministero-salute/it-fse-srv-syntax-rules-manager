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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo;

import com.mongodb.bulk.BulkWriteResult;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.ExtensionETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;

import java.util.List;
import java.util.Map;

/**
 * Repository interface
 */
public interface IDocumentRepo {
    /**
     * Retrieves the documents entities by their extension identifier
     * @param extension The extension id
     * @param allDocuments
     * @return The documents matching the extension identifier or an empty list if none match
     * @throws OperationException If a data-layer error occurs
     */
    List<SchemaETY> findDocsByExtensionId(String extension, boolean allDocuments) throws OperationException;

    /**
     * Verify if exists at least one document with the given extension identifier
     * @param extension The extension id
     * @return {@code True} if at least one document exists otherwise {@code False}
     * @throws OperationException If a data-layer error occurs
     */
    boolean isExtensionInserted(String extension) throws OperationException;

    /**
     * Verify if given filename exists with the given extension identifier
     *
     * @param extension The extension id
     * @return List containing documents entities
     * @throws OperationException If a data-layer error occurs
     */
    List <SchemaETY> getInsertedDocumentsByExtension(String extension) throws OperationException;

    /**
     * Inserts all the given entities inside the schema
     * @param entities The entities to insert
     * @return The entities inserted
     * @throws OperationException If a data-layer error occurs
     */
    List<SchemaETY> insertDocsByExtensionId(List<SchemaETY> entities) throws OperationException;

    /**
     * Update all the given entities inside the schema
     * @param entities Map containing keys as the old document and values as the new ones
     * @return The {@link BulkWriteResult} object
     * @throws OperationException If a data-layer error occurs
     */
    List<SchemaETY> updateDocsByExtensionId(Map<SchemaETY, SchemaETY> entities) throws OperationException, DataIntegrityException;

    /**
     * Deletes all the documents entities matching the given extensions
     *
     * @param extension The extension id
     * @return The list containing all removed entities from the collection
     * @throws OperationException If a data-layer error occurs
     */
    List<SchemaETY> deleteDocsByExtensionId(String extension) throws OperationException, DataIntegrityException;

    /**
     * Retrieves one single document given identifier
     *
     * @param id The document id
     * @return The requested document
     * @throws OperationException If a data-layer error occurs
     */
    SchemaETY findDocById(String id) throws OperationException;

    /**
     * Insert documents
     * @param extension
     * @param filenames
     * @return
     * @throws OperationException
     */
    List<SchemaETY> findExistingDocumentsByExtensionAndFilenames(String extension, List<String> filenames) throws OperationException;

    /**
     * Logically delete documents by filenames and extension
     * @param extension
     * @param filenames
     * @return
     */
    List<SchemaETY> deleteDocsByExtensionIdAndFilenames(String extension, List<String> filenames) throws OperationException, DataIntegrityException;

    /**
     * Retrieves all active documents on DB
     *
     * @return All active documents
     * @throws OperationException If a data-layer error occurs
     */
    List<ExtensionETY> groupByExtension(boolean deleted) throws OperationException;
}
