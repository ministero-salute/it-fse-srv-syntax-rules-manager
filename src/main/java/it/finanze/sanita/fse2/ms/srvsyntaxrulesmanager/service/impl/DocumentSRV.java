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
        // Get document
        SchemaETY doc = repository.findDocById(id);
        // Verify data
        if (doc == null) {
            throw new DocumentNotFoundException("The requested document does not exists");
        }
        // Bye bye
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
    public List<SchemaDocumentDTO> findDocsByExtensionId(String extension)
        throws OperationException, ExtensionNotFoundException {
        // List containing the mapping ETY -> DTO
        List<SchemaDocumentDTO> out;
        // Execute query from repository and get entity list
        List<SchemaETY> docs = repository.findDocsByExtensionId(extension);
        // Verify data
        if (docs == null || docs.isEmpty()) {
            throw new ExtensionNotFoundException("The requested extension does not exists");
        }
        // Convert entity to dto representation
        out = docs.stream().map(SchemaDocumentDTO::fromEntity).collect(Collectors.toList());
        // Bye bye
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
    public List<String> insertDocsByExtensionId(String root, String extension, MultipartFile[] files)
        throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException {
        // Generate multipart filename list
        List<String> filenames = Stream.of(files).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        // Check if given root file matches any file provided
        Optional<String> rootName = filenames.stream().filter(root::equals).findFirst();
        // Verify existence
        if(!rootName.isPresent()) {
            throw new RootNotValidException(
                "Root filename doesn't match any of the possible values",
                Fields.ROOT,
                root,
                filenames
            );
        }
        // Check if given extension already exists
        if(repository.isExtensionInserted(extension)) {
            // Let the caller know about it
            throw new ExtensionAlreadyExistsException("Cannot insert the given extension, it already exists");
        }
        // Create list for RAW->ETY model conversion
        List<SchemaETY> entities = new ArrayList<>();
        // Start conversion
        for (MultipartFile f : files) {
            // Verify if root file
            boolean isRoot = root.equals(f.getOriginalFilename());
            // Convert to entities
            entities.add(SchemaETY.fromMultipart(f, extension, isRoot));
        }
        // Insert documents
        List<SchemaETY> inserted = repository.insertDocsByExtensionId(entities);
        // Return filenames of the inserted elements
        return inserted.stream().map(SchemaETY::getNameSchema).collect(Collectors.toList());
    }

    /**
     * Update the documents content with the provided ones according to the extension
     *
     * @param extension The extension id
     * @param files   The documents to use as replacement of the old ones
     * @return List with filenames of elements updated into the schema
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException  If no documents matching the extension are found
     * @throws DocumentNotFoundException If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException If unable to convert the input raw data into a binary representation
     */
    @Override
    public List<String> updateDocsByExtensionId(String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException {
        // Check if given extension already exists
        if(!repository.isExtensionInserted(extension)) {
            // Let the caller know about it
            throw new ExtensionNotFoundException("Unable to update the given extension, it does not exists");
        }
        // Verify if files we want to update exists inside the schema
        List<String> filenames = Arrays.stream(files).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        // Let's query the database and see which files can be replaced
        Map<String, SchemaETY> inserted = repository.isDocumentInserted(extension, filenames);
        // Iterate on array files and verify if any document is missing
        for (MultipartFile f : files) {
            // The file we are looking for
            String filename = f.getOriginalFilename();
            // The file must already exist on the database in order to be modified
            if (!inserted.containsKey(filename)) {
                throw new DocumentNotFoundException(filename + " does not exists on the schema instance");
            }
        }
        // Create mapping OLD->NEW files
        Map<SchemaETY, SchemaETY> entities = new HashMap<>();
        // Start conversion
        for (MultipartFile f : files) {
            // The associated entity
            SchemaETY current = inserted.get(f.getOriginalFilename());
            SchemaETY newest = SchemaETY.fromMultipart(f, extension, current.getRootSchema());
            // Convert to entities
            entities.put(current, newest);
        }
        // Invoke update on db
        repository.updateDocsByExtensionId(entities);
        // Return
        return new ArrayList<>(inserted.keySet());
    }

    /**
     * Deletes all the documents entities matching the given extensions
     * @param extension The extension id
     * @return List with filenames of the elements removed from the collection
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If there is no document matching the given extension
     */
    @Override
    public List<String> deleteDocsByExtensionId(String extension) throws OperationException, ExtensionNotFoundException, DataIntegrityException {
        // Removed documents list
        List<SchemaETY> removed;
        // Verify we are trying to delete something that exists
        if(repository.isExtensionInserted(extension)) {
            // Let's remove all the documents matching the version
            removed = repository.deleteDocsByExtensionId(extension);
        }else{
            // Let the caller know about it
            throw new ExtensionNotFoundException("No document with the given extension exists");
        }
        // Return filenames of the removed elements
        return removed.stream().map(SchemaETY::getNameSchema).collect(Collectors.toList());
    }
}
