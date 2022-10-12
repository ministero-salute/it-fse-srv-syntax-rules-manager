package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorInstance.Fields;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IDocumentSRV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.*;

@Service
public class DocumentSRV implements IDocumentSRV {

    @Autowired
    IDocumentRepo repository;

    /**
     * Retrieves the document by identifier
     *
     * @param id The document id
     * @return The document matching the extension identifier
     * @throws OperationException        If a data-layer error occurs
     * @throws DocumentNotFoundException If no document matching the identifier is found
     */
    @Override
    public SchemaDocumentDTO findDocById(String id) throws OperationException, DocumentNotFoundException {
        SchemaETY doc = repository.findDocById(id);
        if (doc == null) {
            throw new DocumentNotFoundException(ERR_SRV_DOC_NOT_FOUND);
        }
        return SchemaDocumentDTO.fromEntity(doc);
    }

    /**
     * Retrieves the documents by their extension identifier
     * @param extension The extension id
     * @return The documents matching the extension identifier
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     */
    @Override
    public List<SchemaDocumentDTO> findDocsByExtensionId(String extension) throws OperationException, ExtensionNotFoundException {
        List<SchemaETY> docs = repository.findDocsByExtensionId(extension);
        if (docs == null || docs.isEmpty()) {
            throw new ExtensionNotFoundException(ERR_SRV_EXT_NOT_FOUND);
        }
        List<SchemaDocumentDTO> out = docs.stream().map(SchemaDocumentDTO::fromEntity).collect(Collectors.toList());
        return out;
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
     */
    @Override
    public int insertDocsByExtensionId(String root, String extension, MultipartFile[] files)
        throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException {

        List<String> filenames = Stream.of(files).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        Optional<String> rootName = filenames.stream().filter(root::equals).findFirst();

        if(!rootName.isPresent()) {
            throw new RootNotValidException(String.format(ERR_SRV_ROOT_NOT_FOUND, root, filenames), Fields.ROOT);
        }

        if(repository.isExtensionInserted(extension)) {
            throw new ExtensionAlreadyExistsException(ERR_SRV_EXT_ALREADY_ESISTS);
        }

        List<SchemaETY> entities = new ArrayList<>();
        for (MultipartFile f : files) {
            boolean isRoot = root.equals(f.getOriginalFilename());
            entities.add(SchemaETY.fromMultipart(f, extension, isRoot));
        }

        List<SchemaETY> inserted = repository.insertDocsByExtensionId(entities);
        return inserted != null ? inserted.size() : 0;
    }

    /**
     * Update the documents content with the provided ones according to the extension
     *
     * @param extension The extension id
     * @param files   The documents to use as replacement of the old ones
     * @return Number of schema updated.
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException  If no documents matching the extension are found
     * @throws DocumentNotFoundException If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException If unable to convert the input raw data into a binary representation
     */
    @Override
    public int updateDocsByExtensionId(String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException {
        if (!repository.isExtensionInserted(extension)) {
            throw new ExtensionNotFoundException(ERR_SRV_EXT_NOT_FOUND);
        }
        List<String> filenames = Arrays.stream(files).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        Map<String, SchemaETY> inserted = repository.isDocumentInserted(extension, filenames);
        
        for (MultipartFile f : files) {
            String filename = f.getOriginalFilename();
            if (!inserted.containsKey(filename)) {
                throw new DocumentNotFoundException(String.format(ERR_SRV_EXT_DOC_NOT_FOUND, filename));
            }
        }

        Map<SchemaETY, SchemaETY> entities = new HashMap<>();
        for (MultipartFile f : files) {
            SchemaETY current = inserted.get(f.getOriginalFilename());
            SchemaETY newest = SchemaETY.fromMultipart(f, extension, current.getRootSchema());
            entities.put(current, newest);
        }
        List<SchemaETY> updatedSchema = repository.updateDocsByExtensionId(entities);
        return updatedSchema != null ? updatedSchema.size() : 0;
    }

    /**
     * Deletes all the documents entities matching the given extensions
     * @param extension The extension id
     * @return List with filenames of the elements removed from the collection
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If there is no document matching the given extension
     */
    @Override
    public int deleteDocsByExtensionId(String extension) throws OperationException, ExtensionNotFoundException, DataIntegrityException {
        // Verify we are trying to delete something that exists
        if(repository.isExtensionInserted(extension)) {
            // Let's remove all the documents matching the version
            List<SchemaETY> removed = repository.deleteDocsByExtensionId(extension);
            return removed != null ? removed.size() : 0;
        } else{
            // Let the caller know about it
            throw new ExtensionNotFoundException(ERR_SRV_EXT_NOT_FOUND);
        }
    }
}
