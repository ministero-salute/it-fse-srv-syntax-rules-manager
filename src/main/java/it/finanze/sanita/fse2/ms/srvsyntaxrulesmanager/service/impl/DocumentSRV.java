package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.impl;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.SchemaDocumentDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorInstance.Fields;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.entity.SchemaETY;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.repository.mongo.IDocumentRepo;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.service.IDocumentSRV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.*;

@Service
@Slf4j
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
     * @param includeDeleted
     * @return The documents matching the extension identifier
     * @throws OperationException If a data-layer error occurs
     * @throws ExtensionNotFoundException If no documents matching the extension are found
     */
    @Override
    public List<SchemaDocumentDTO> findDocsByExtensionId(String extension, boolean includeDeleted) throws OperationException, ExtensionNotFoundException {
        List<SchemaETY> docs = repository.findDocsByExtensionId(extension, includeDeleted);
        if (CollectionUtils.isEmpty(docs)) {
            throw new ExtensionNotFoundException(ERR_SRV_EXT_NOT_FOUND);
        }
        return docs.stream().map(SchemaDocumentDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * Insert the given data inside the schema
     * @param root Root filename
     * @param extension Extension identifier
     * @param files The content schema to insert
     * @return number of files inserted into the schema
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

        if (!rootName.isPresent()) {
            throw new RootNotValidException(String.format(ERR_SRV_ROOT_NOT_FOUND, root, filenames), Fields.ROOT);
        }

        if (repository.isExtensionInserted(extension)) {
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
     * @param root The root identifier of schema
     * @param extension The extension id
     * @param files   The documents to use as replacement of the old ones
     * @return Number of schema updated.
     * @throws OperationException        If a data-layer error occurs
     * @throws ExtensionNotFoundException  If no documents matching the extension are found
     * @throws DocumentNotFoundException If at least one document to be replaced is not found inside the collection
     * @throws DataProcessingException If unable to convert the input raw data into a binary representation
     */
    @Override
    public int updateDocsByExtensionId(String root, String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DataProcessingException, DataIntegrityException, RootNotValidException {
        List<String> filenames = Stream.of(files).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        Optional<String> rootName = filenames.stream().filter(root::equals).findFirst();

        if (!rootName.isPresent()) {
            throw new RootNotValidException(String.format(ERR_SRV_ROOT_NOT_FOUND, root, filenames), Fields.ROOT);
        }

        if (!repository.isExtensionInserted(extension)) {
            throw new ExtensionNotFoundException(ERR_SRV_EXT_NOT_FOUND);
        }

        List<SchemaETY> toInsert = new ArrayList<>();

        for (MultipartFile f : files) {
            boolean isRoot = root.equals(f.getOriginalFilename());
            SchemaETY newest = SchemaETY.fromMultipart(f, extension, isRoot);
            toInsert.add(newest);
        }


        List<SchemaETY> updatedSchema = repository.deleteDocsByExtensionId(extension);
        if (!CollectionUtils.isEmpty(updatedSchema)) {
            log.debug("Number of deleted schema: {}", updatedSchema.size());
        }
        List<SchemaETY> inserted = repository.insertDocsByExtensionId(toInsert);
        return inserted != null ? inserted.size() : 0;
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

    @Override
    public int patchDocsByExtensionId(String extension, MultipartFile[] files) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException {
        if (!repository.isExtensionInserted(extension)) {
            throw new ExtensionNotFoundException(ERR_SRV_EXT_NOT_FOUND);
        }

        List<String> filenames = Arrays.stream(files).map(MultipartFile::getOriginalFilename).collect(Collectors.toList());
        List<SchemaETY> deletedFromDB = repository.deleteDocsByExtensionIdAndFilenames(extension, filenames);
        Map<String, SchemaETY> toReplaceFromDB = deletedFromDB.stream().collect(Collectors.toMap(SchemaETY::getNameSchema, entity -> entity));

        // Merge old and new files
        int replacedNumber = 0;
        log.debug("Old files n.: {}", toReplaceFromDB.size());
        log.debug("Received files: n.: {}", files.length);
        for (MultipartFile f : files) {
            String filename = f.getOriginalFilename();
            if (toReplaceFromDB.containsKey(filename)) {
                SchemaETY old = toReplaceFromDB.get(filename);
                SchemaETY newest = SchemaETY.fromMultipart(f, extension, old.getRootSchema());
                toReplaceFromDB.put(old.getNameSchema(), newest);
                replacedNumber++;
            } else {
                SchemaETY newest = SchemaETY.fromMultipart(f, extension, false);
                toReplaceFromDB.put(filename, newest);
            }
        }
        log.debug("Final n. {}, initialDB: {}, replaced: {}", toReplaceFromDB.size(), toReplaceFromDB.size(), replacedNumber);

        List<SchemaETY> replacedSchema = repository.insertDocsByExtensionId(new ArrayList<>(toReplaceFromDB.values()));
        return replacedSchema != null ? replacedSchema.size() : 0;
    }

    @Override
    public List<SchemaDocumentDTO> findAllActiveDocuments() throws OperationException {
        List<SchemaETY> etyList = repository.findAllActive();
        if (etyList == null || etyList.isEmpty()) {
            log.warn("No active schema documents found");
            return new ArrayList<>();
        }
        return buildDtoFromEty(etyList);
    }

    private List<SchemaDocumentDTO> buildDtoFromEty(List<SchemaETY> etyList) {
        List<SchemaDocumentDTO> schemaDocumentDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(etyList)) {
            for (SchemaETY ety : etyList) {
                schemaDocumentDTOList.add(SchemaDocumentDTO.fromEntity(ety));
            }
        }
        return schemaDocumentDTOList;
    }
}
