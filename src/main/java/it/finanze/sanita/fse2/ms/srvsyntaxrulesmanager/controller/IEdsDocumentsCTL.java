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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.document.GetDocumentById;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.documents.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.data.GetDocByIdResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.crud.*;
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
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.OAUtility.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility.RoutesUtility.*;

/**
 * Documents retriever controller
 *
 */
@RequestMapping(path = API_DOCUMENT_MAPPER)
@Tag(name = API_DOCUMENTS_TAG)
@Validated
public interface IEdsDocumentsCTL {

    @GetMapping(API_GET_ONE_BY_ID)
    @GetDocumentById
    GetDocByIdResDTO getDocumentById(
            @PathVariable(name = API_PATH_ID_VAR) @Parameter(description = "Document identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_ID_BLANK) @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX, message = "Document id does not match the expected size") @ValidObjectId(message = ERR_VAL_ID_NOT_VALID) String id)
            throws DocumentNotFoundException, OperationException;

    @GetMapping(API_GET_BY_EXTS)
    @GetDocumentsByExt
    GetDocsResDTO getDocumentsByExtension(
            @PathVariable(name = API_PATH_EXTS_VAR) @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestParam(name = API_QP_BINARY, defaultValue = "false") @Parameter(description = "Include binary content") boolean binary,
            @RequestParam(defaultValue = "false") @Parameter(description = "Include deleted schema") boolean includeDeleted)
            throws ExtensionNotFoundException, OperationException;

    @PostMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @UploadDocumentsByExt
    @ResponseStatus(HttpStatus.CREATED)
    PostDocsResDTO uploadDocuments(
            @RequestPart @Parameter(description = "Root filename (eg. Test.xsd)", schema = @Schema(minLength = OA_ANY_STRING_MIN, maxLength = OA_ANY_STRING_MAX)) @NotBlank(message = ERR_VAL_ROOT_BLANK) @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX, message = "Root filename does not match the expected size") String root,
            @RequestPart @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestPart @Parameter(description = "Files", array = @ArraySchema(minItems = OA_ARRAY_FILES_MIN, maxItems = OA_ARRAY_FILES_MAX, schema = @Schema(type = "string", format = "binary", maxLength = OA_FILE_CONTENT_MAX))) @Size(min = OA_ARRAY_FILES_MIN, max = OA_ARRAY_FILES_MAX, message = "File array does not match the expected size") @UniqueMultipart(message = ERR_VAL_FILES_DUPLICATED) MultipartFile[] files)
        throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException, InvalidContentException, SchemaValidatorException;

    @PutMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @UpdateDocumentsByExt
    PutDocsResDTO updateDocuments(
            @RequestPart @Parameter(description = "Root filename (eg. Test.xsd)", schema = @Schema(minLength = OA_ANY_STRING_MIN, maxLength = OA_ANY_STRING_MAX)) @NotBlank(message = ERR_VAL_ROOT_BLANK) @Size(min = OA_ANY_STRING_MIN, max = OA_ANY_STRING_MAX, message = "Root filename does not match the expected size") String root,
            @RequestPart @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestPart @Parameter(description = "Files", array = @ArraySchema(minItems = OA_ARRAY_FILES_MIN, maxItems = OA_ARRAY_FILES_MAX, schema = @Schema(type = "string", format = "binary", maxLength = OA_FILE_CONTENT_MAX))) @Size(min = OA_ARRAY_FILES_MIN, max = OA_ARRAY_FILES_MAX, message = "File array does not match the expected size") @UniqueMultipart(message = ERR_VAL_FILES_DUPLICATED) MultipartFile[] files)
        throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException,
        DataIntegrityException, InvalidContentException, RootNotValidException, SchemaValidatorException;

    @PatchMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PatchDocumentsByExt
    PatchDocsResDTO patchDocuments(
            @RequestPart @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension,
            @RequestPart @Parameter(description = "Files", array = @ArraySchema(minItems = OA_ARRAY_FILES_MIN, maxItems = OA_ARRAY_FILES_MAX, schema = @Schema(type = "string", format = "binary", maxLength = OA_FILE_CONTENT_MAX))) @Size(min = OA_ARRAY_FILES_MIN, max = OA_ARRAY_FILES_MAX, message = "File array does not match the expected size") @UniqueMultipart(message = ERR_VAL_FILES_DUPLICATED) MultipartFile[] files)
        throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException,
        DataIntegrityException, InvalidContentException, SchemaValidatorException;

    @DeleteMapping(API_DELETE_BY_EXTS)
    @DeleteDocumentsByExt
    DeleteDocsResDTO deleteDocuments(
            @PathVariable(name = API_PATH_EXTS_VAR) @Parameter(description = "Extension identifier", schema = @Schema(minLength = OA_EXTS_STRING_MIN, maxLength = OA_EXTS_STRING_MAX)) @NotBlank(message = ERR_VAL_EXT_BLANK) @Size(min = OA_EXTS_STRING_MIN, max = OA_EXTS_STRING_MAX, message = "Extension does not match the expected size") String extension)
            throws OperationException, ExtensionNotFoundException, DataIntegrityException;

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @GetAllDocuments
    GetDocsResDTO getAllDocuments(
        @RequestParam(value = API_QP_BINARY, defaultValue = "false") @Parameter(description = "Include binary content") boolean binary,
        @RequestParam(value = API_QP_INCLUDE_DELETED, defaultValue = "false") @Parameter(description = "Include deleted content") boolean deleted
    ) throws DocumentNotFoundException, OperationException;
}
