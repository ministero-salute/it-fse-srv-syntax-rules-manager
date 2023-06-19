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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.impl;


import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.IEdsDocumentsCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.data.GetDocByIdResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.crud.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IDocumentSRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
public class EdsDocumentsCTL extends AbstractCTL implements IEdsDocumentsCTL {

    /**
     * Document service layer
     */
    @Autowired
    private IDocumentSRV service;

    /**
     * Retrieves the document by extension identifier and filename
     * @param id The document id
     * @return The document matching the identifier
     * @throws OperationException If a data-layer error occurs
     * @throws DocumentNotFoundException If no document matching the id is found
     */
    @Override
    public GetDocByIdResDTO getDocumentById(String id) throws OperationException, DocumentNotFoundException {
        return new GetDocByIdResDTO(getLogTraceInfo(), service.findDocById(id));
    }

    /**
     * Retrieves the documents by their extension identifier
     *
     * @param binary If response should display binary content for each document
     * @param extension The extension id
     * @return The documents matching the extension identifier
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     */
    @Override
    public GetDocsResDTO getDocumentsByExtension(String extension, boolean binary, boolean deleted)
        throws ExtensionNotFoundException, OperationException {
        // Create options
        SchemaDocumentDTO.Options opts = new SchemaDocumentDTO.Options(binary);
        // Retrieve documents by extension
        ArrayList<SchemaDocumentDTO> out = new ArrayList<>(service.findDocsByExtensionId(extension, deleted));
        // Return response
        return new GetDocsResDTO(getLogTraceInfo(), SchemaDTO.fromItems(extension, out, opts));
    }

    /**
     * Insert the given data inside the schema
     *
     * @param root      Root filename
     * @param extension Extension identifier
     * @param files     The content schema to insert
     * @return List with filenames of elements inserted into the schema
     * @throws OperationException              If a data-layer error occurs
     * @throws ExtensionAlreadyExistsException If the given extension is already inserted into the schema
     * @throws DataProcessingException         If an error occurs while converting raw data to entity type
     * @throws RootNotValidException           If the given root filename is not present in the files array
     * @throws InvalidContentException         If at least one files has an invalid content that means is empty or not a proper schema file
     */
    @Override
    public PostDocsResDTO uploadDocuments(String root, String extension, MultipartFile[] files)
        throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException, InvalidContentException, SchemaValidatorException {

        String checkedRoot = checkRootExtension(root);
        if (validateFiles(files)) {
            int insertedSchema = service.insertDocsByExtensionId(checkedRoot, extension, files);
            return new PostDocsResDTO(getLogTraceInfo(), insertedSchema);
        } else {
            throw new InvalidContentException(String.format(Constants.Logs.ERR_INVALID_CONTENT, extension));
        }
    }

    /**
     * Update the documents content with the provided ones according to the extension
     *
     * @param extension The extension id
     * @param files     The documents to use as replacement of the old ones
     * @param root      Root identifier
     * @return List with filenames of elements updated into the schema
     * @throws OperationException         If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     * @throws DocumentNotFoundException  If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException    If unable to convert the input raw data into a binary representation
     * @throws InvalidContentException    If at least one files has an invalid content that means is empty or not a proper schema file
     */
    @Override
    public PutDocsResDTO updateDocuments(String root, String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException, InvalidContentException, RootNotValidException, SchemaValidatorException {
        String checkedRoot = checkRootExtension(root);
        if (validateFiles(files)) {
            int updatedSchema = service.updateDocsByExtensionId(checkedRoot, extension, files);
            return new PutDocsResDTO(getLogTraceInfo(), updatedSchema);
        } else {
            throw new InvalidContentException(String.format(Constants.Logs.ERR_INVALID_CONTENT, extension));
        }
    }

    /**
     * Deletes all the documents entities matching the given extension
     *
     * @param extension The extension id
     * @return List with filenames of elements removed from the collection
     * @throws OperationException         If a data-layer error occurs
     * @throws ExtensionNotFoundException If there is no document matching the given extension
     */
    @Override
    public DeleteDocsResDTO deleteDocuments(String extension) throws OperationException, ExtensionNotFoundException, DataIntegrityException {
        int deletedSchema = service.deleteDocsByExtensionId(extension);
        return new DeleteDocsResDTO(getLogTraceInfo(), deletedSchema);
    }

    /**
     * Patch the documents content with the provided ones according to the extension
     *
     * @param extension The extension id
     * @param files     The documents to use as replacement of the old ones
     * @return List with filenames of elements updated into the schema
     * @throws OperationException         If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     * @throws DocumentNotFoundException  If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException    If unable to convert the input raw data into a binary representation
     * @throws InvalidContentException    If at least one files has an invalid content that means is empty or not a proper schema file
     */
    @Override
    public PatchDocsResDTO patchDocuments(String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException, InvalidContentException, SchemaValidatorException {
        
        if (validateFiles(files)) {
            int patchedDocuments = service.patchDocsByExtensionId(extension, files);
            return new PatchDocsResDTO(getLogTraceInfo(), patchedDocuments);
        } else {
            throw new InvalidContentException(String.format(Constants.Logs.ERR_INVALID_CONTENT, extension));
        }
    }

    /**
     * Retrieves all the active documents
     * @param binary If response should display binary content for each document
     * @param deleted If response should evaluate deleted content
     * @return The documents found on DB
     * @throws OperationException        If a data-layer error occurs
     */
    @Override
    public GetDocsResDTO getAllDocuments(boolean binary, boolean deleted) throws OperationException {
        // Create options
        SchemaDocumentDTO.Options opts = new SchemaDocumentDTO.Options(binary);
        // Retrieve documents by extension
        List<SchemaDTO> out = new ArrayList<>(service.getExtensions(opts, deleted));
        // Return response
        return new GetDocsResDTO(getLogTraceInfo(), out);
    }
}
