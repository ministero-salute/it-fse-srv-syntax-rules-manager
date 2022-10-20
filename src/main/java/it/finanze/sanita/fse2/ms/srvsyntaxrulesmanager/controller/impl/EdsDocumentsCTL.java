/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.impl;


import java.util.ArrayList;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.AbstractCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.IEdsDocumentsCTL;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.DeleteDocumentsResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.GetDocumentResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.GetDocumentsResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.UpdateDocumentsResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.UploadDocumentsResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataIntegrityException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DataProcessingException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.DocumentNotFoundException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.ExtensionAlreadyExistsException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.ExtensionNotFoundException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.InvalidContentException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.OperationException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.RootNotValidException;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IDocumentSRV;

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
    public GetDocumentResDTO getDocumentById(String id) throws OperationException, DocumentNotFoundException {
        // Retrieve document by id
        SchemaDocumentDTO out = service.findDocById(id);
        // Return response
        return new GetDocumentResDTO(getLogTraceInfo(), new GetDocumentResDTO.GetOneDocPayloadDTO(out));
    }

    /**
     * Retrieves the documents by their extension identifier
     *
     * @param extension The extension id
     * @return The documents matching the extension identifier
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     */
    @Override
    public GetDocumentsResDTO getDocumentsByExtension(String extension, boolean includeDeleted)
        throws ExtensionNotFoundException, OperationException {
        // Retrieve documents by extension
        ArrayList<SchemaDocumentDTO> out = new ArrayList<>(service.findDocsByExtensionId(extension, includeDeleted));
        // Return response
        return new GetDocumentsResDTO(getLogTraceInfo(), new GetDocumentsResDTO.GetMultipleDocsPayloadDTO(out));
    }

    /**
     * Insert the given data inside the schema
     * @param root Root filename
     * @param extension Extension identifier
     * @param files The content schema to insert
     * @return List with filenames of elements inserted into the schema
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionAlreadyExistsException If the given extension is already inserted into the schema
     * @throws DataProcessingException If an error occurs while converting raw data to entity type
     * @throws RootNotValidException If the given root filename is not present in the files array
     * @throws InvalidContentException If at least one files has an invalid content that means is empty or not a proper schema file
     */
    @Override
    public UploadDocumentsResDTO uploadDocuments(String root, String extension, MultipartFile[] files)
        throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException, InvalidContentException {

        String checkedRoot = checkRootExtension(root);
        if (validateFiles(files)) {
            int insertedSchema = service.insertDocsByExtensionId(checkedRoot, extension, files);
            return new UploadDocumentsResDTO(getLogTraceInfo(), new UploadDocumentsResDTO.UploadPayloadDTO(extension), insertedSchema);
        } else {
            throw new InvalidContentException(String.format(Constants.Logs.ERR_INVALID_CONTENT, extension));
        }
    }

    /**
     * Update the documents content with the provided ones according to the extension
     * @param extension The extension id
     * @param files The documents to use as replacement of the old ones
     * @param root Root identifier
     * @return List with filenames of elements updated into the schema
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     * @throws DocumentNotFoundException If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException If unable to convert the input raw data into a binary representation
     * @throws InvalidContentException If at least one files has an invalid content that means is empty or not a proper schema file
     */
    @Override
    public UpdateDocumentsResDTO updateDocuments(String root, String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException, InvalidContentException, RootNotValidException {
        String checkedRoot = checkRootExtension(root);
        if (validateFiles(files)) {
            int updatedSchema = service.updateDocsByExtensionId(checkedRoot, extension, files);
            return new UpdateDocumentsResDTO(getLogTraceInfo(), new UpdateDocumentsResDTO.UpdatePayloadDTO(extension), updatedSchema);
        } else {
            throw new InvalidContentException(String.format(Constants.Logs.ERR_INVALID_CONTENT, extension));
        }
    }

    /**
     * Deletes all the documents entities matching the given extension
     * @param extension The extension id
     * @return List with filenames of elements removed from the collection
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If there is no document matching the given extension
     */
    @Override
    public DeleteDocumentsResDTO deleteDocuments(String extension) throws OperationException, ExtensionNotFoundException, DataIntegrityException {
        int deletedSchema = service.deleteDocsByExtensionId(extension);
        return new DeleteDocumentsResDTO(getLogTraceInfo(), new DeleteDocumentsResDTO.DeletePayloadDTO(extension), deletedSchema);
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
    public UpdateDocumentsResDTO patchDocuments(String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException, InvalidContentException, RootNotValidException {
        
        if (validateFiles(files)) {
            int patchedDocuments = service.patchDocsByExtensionId(extension, files);
            return new UpdateDocumentsResDTO(getLogTraceInfo(), new UpdateDocumentsResDTO.UpdatePayloadDTO(extension), patchedDocuments);
        } else {
            throw new InvalidContentException(String.format(Constants.Logs.ERR_INVALID_CONTENT, extension));
        }
    }

    /**
     * Retrieves all the active documents
     *
     * @return The documents found on DB
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     */
    @Override
    public GetDocumentsResDTO getAllDocuments() throws OperationException {
        // Retrieve documents by extension
        ArrayList<SchemaDocumentDTO> out = new ArrayList<>(service.findAllActiveDocuments());
        // Return response
        return new GetDocumentsResDTO(getLogTraceInfo(), new GetDocumentsResDTO.GetMultipleDocsPayloadDTO(out));
    }
}
