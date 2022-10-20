/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.document.GetDocumentById;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.documents.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.UniqueMultipart;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.ValidObjectId;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityOA.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.UtilityRoutes.*;

/**
 * Documents retriever controller
 *
 * @author G. Baittiner
 */
@RequestMapping(path = API_DOCUMENT_MAPPER)
@Tag(name = API_DOCUMENTS_TAG)
@Validated
public interface IEdsDocumentsCTL {

    @GetMapping(API_GET_ONE_BY_ID)
    @GetDocumentById
    GetDocumentResDTO getDocumentById(
            @PathVariable(name = API_PATH_ID_VAR) @Parameter(description = "Document identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_ID_BLANK) @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX, message = "Document id does not match the expected size") @ValidObjectId(message = ERR_VAL_ID_NOT_VALID) String id)
            throws DocumentNotFoundException, OperationException;

    @GetMapping(API_GET_BY_EXTS)
    @GetDocumentsByExt
    GetDocumentsResDTO getDocumentsByExtension(
            @PathVariable(name = API_PATH_EXTS_VAR) @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Include deleted schema") boolean includeDeleted)
            throws ExtensionNotFoundException, OperationException;

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @UploadDocumentsByExt
    @ResponseStatus(HttpStatus.CREATED)
    UploadDocumentsResDTO uploadDocuments(
            @RequestPart @Parameter(description = "Root filename (eg. Test.xsd)", schema = @Schema(minLength = OA_ANY_STRING_MIN, maxLength = OA_ANY_STRING_MAX)) @NotBlank(message = ERR_VAL_ROOT_BLANK) @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX, message = "Root filename does not match the expected size") String root,
            @RequestPart @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestPart @Parameter(description = "Files", array = @ArraySchema(minItems = OA_ARRAY_FILES_MIN, maxItems = OA_ARRAY_FILES_MAX, schema = @Schema(type = "string", format = "binary", maxLength = OA_FILE_CONTENT_MAX))) @Size(min = OA_ARRAY_FILES_MIN, max = OA_ARRAY_FILES_MAX, message = "File array does not match the expected size") @UniqueMultipart(message = ERR_VAL_FILES_DUPLICATED) MultipartFile[] files)
            throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException, InvalidContentException;

    @PutMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @UpdateDocumentsByExt
    UpdateDocumentsResDTO updateDocuments(
            @RequestPart @Parameter(description = "Root filename (eg. Test.xsd)", schema = @Schema(minLength = OA_ANY_STRING_MIN, maxLength = OA_ANY_STRING_MAX)) @NotBlank(message = ERR_VAL_ROOT_BLANK) @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX, message = "Root filename does not match the expected size") String root,
            @RequestPart @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestPart @Parameter(description = "Files", array = @ArraySchema(minItems = OA_ARRAY_FILES_MIN, maxItems = OA_ARRAY_FILES_MAX, schema = @Schema(type = "string", format = "binary", maxLength = OA_FILE_CONTENT_MAX))) @Size(min = OA_ARRAY_FILES_MIN, max = OA_ARRAY_FILES_MAX, message = "File array does not match the expected size") @UniqueMultipart(message = ERR_VAL_FILES_DUPLICATED) MultipartFile[] files)
            throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException,
            DataIntegrityException, InvalidContentException, RootNotValidException;

    @PatchMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PatchDocumentsByExt
    UpdateDocumentsResDTO patchDocuments(
            @RequestPart @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestPart @Parameter(description = "Files", array = @ArraySchema(minItems = OA_ARRAY_FILES_MIN, maxItems = OA_ARRAY_FILES_MAX, schema = @Schema(type = "string", format = "binary", maxLength = OA_FILE_CONTENT_MAX))) @Size(min = OA_ARRAY_FILES_MIN, max = OA_ARRAY_FILES_MAX, message = "File array does not match the expected size") @UniqueMultipart(message = ERR_VAL_FILES_DUPLICATED) MultipartFile[] files)
            throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException,
            DataIntegrityException, InvalidContentException, RootNotValidException;

    @DeleteMapping(API_DELETE_BY_EXTS)
    @DeleteDocumentsByExt
    DeleteDocumentsResDTO deleteDocuments(
            @PathVariable(name = API_PATH_EXTS_VAR) @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension)
            throws OperationException, ExtensionNotFoundException, DataIntegrityException;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @GetAllDocuments
    GetDocumentsResDTO getAllDocuments() throws DocumentNotFoundException, OperationException;
}
