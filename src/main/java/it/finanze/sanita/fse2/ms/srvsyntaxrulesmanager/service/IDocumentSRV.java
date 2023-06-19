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

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDocumentSRV {

    /**
     * Retrieves the document by identifier
     * @param id The document id
     * @return The document matching the extension identifier
     * @throws OperationException If a data-layer error occurs
     * @throws DocumentNotFoundException If no document matching the identifier is found
     */
    SchemaDocumentDTO findDocById(String id) throws OperationException, DocumentNotFoundException;

    /**
     * Retrieves the documents by their extension identifier
     * @param extension The extension id
     * @param includeDeleted
     * @return The documents matching the extension identifier
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     */
    List<SchemaDocumentDTO> findDocsByExtensionId(String extension, boolean includeDeleted) throws OperationException, ExtensionNotFoundException;

    /**
     * Insert the given data inside the schema
     * @param root Root filename
     * @param extension Extension identifier
     * @param files The content schema to insert
     * @return List with filenames of elements insert into the schema
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionAlreadyExistsException If the given extension is already inserted into the schema
     * @throws DataProcessingException If an error occurs while converting raw data to entity type
     * @throws RootNotValidException If the given root filename is not present in the files array
     */
    int insertDocsByExtensionId(String root, String extension, MultipartFile[] files) throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException, SchemaValidatorException;

    /**
     * Update the documents content with the provided ones according to the extension, deleting the old ones
     * @param root The root identifier of the schemas
     * @param extension The extension id
     * @param files   The documents to use as replacement of the old ones
     * @return The number of inserted schema.
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException  If no documents matching the extension are found
     * @throws DocumentNotFoundException If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException If unable to convert the input raw data into a binary representation
     */
    int updateDocsByExtensionId(String root, String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException, RootNotValidException, SchemaValidatorException;

    /**
     * Deletes all the documents entities matching the given extensions
     * @param extension The extension id
     * @return List with filenames of elements removed from the collection
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If there is no document matching the given extension
     */
    int deleteDocsByExtensionId(String extension) throws OperationException, ExtensionNotFoundException, DataIntegrityException;

    /**
     * Patch the documents content with the provided ones according to the extension, adding the new ones
     * @param extension
     * @param files
     * @return
     */
    int patchDocsByExtensionId(String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException, SchemaValidatorException;

    /**
     * Find all active documents in DB
     *
     * @return
     * @throws OperationException
     */
    List<SchemaDTO> getExtensions(SchemaDocumentDTO.Options opts, boolean deleted) throws OperationException;
}
