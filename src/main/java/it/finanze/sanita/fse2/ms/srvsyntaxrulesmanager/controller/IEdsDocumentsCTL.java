package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.document.GetDocumentById;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.documents.DeleteDocumentsByExt;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.documents.GetDocumentsByExt;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.documents.UpdateDocumentsByExt;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.documents.UploadDocumentsByExt;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.impl.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.UniqueMultipart;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.validators.ValidObjectId;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsOA.*;
import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utils.UtilsRoutes.*;

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
        @PathVariable(name = API_PATH_ID_VAR)
        @Parameter(
            description = "Document identifier",
            schema = @Schema(
                minLength = OA_EXTS_STRING_MIN,
                maxLength = OA_EXTS_STRING_MAX
            )
        )
        @NotBlank(message = "Document id cannot be blank")
        @Size(
            min = OA_ANY_STRING_MIN,
            max = OA_ANY_STRING_MAX,
            message = "Document id does not match the expected size"
        )
        @ValidObjectId(message = "Document id not valid")
        String id
    ) throws DocumentNotFoundException, OperationException;

    @GetMapping(API_GET_BY_EXTS)
    @GetDocumentsByExt
    GetDocumentsResDTO getDocumentsByExtension(
        @PathVariable(name = API_PATH_EXTS_VAR)
        @Parameter(
            description = "Extension identifier",
            schema = @Schema(
                minLength = OA_EXTS_STRING_MIN,
                maxLength = OA_EXTS_STRING_MAX
            )
        )
        @NotBlank(message = "Extension cannot be blank")
        @Size(
            min = OA_EXTS_STRING_MIN,
            max = OA_EXTS_STRING_MAX,
            message = "Extension does not match the expected size"
        )
        String extension
    ) throws ExtensionNotFoundException, OperationException;

    @PostMapping(
        produces = {MediaType.APPLICATION_JSON_VALUE},
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @UploadDocumentsByExt
    UploadDocumentsResDTO uploadDocuments(
        @RequestPart
        @Parameter(
            description = "Root filename (eg. Test.xsd)",
            schema = @Schema(
                minLength = OA_ANY_STRING_MIN,
                maxLength = OA_ANY_STRING_MAX
            )
        )
        @NotBlank(message = "Root filename cannot be blank")
        @Size(
            min = OA_ANY_STRING_MIN,
            max = OA_ANY_STRING_MAX,
            message = "Root filename does not match the expected size"
        )
        String root,
        @RequestPart
        @Parameter(
            description = "Extension identifier",
            schema = @Schema(
                minLength = OA_EXTS_STRING_MIN,
                maxLength = OA_EXTS_STRING_MAX
            )
        )
        @NotBlank(message = "Extension cannot be blank")
        @Size(
            min = OA_EXTS_STRING_MIN,
            max = OA_EXTS_STRING_MAX,
            message = "Extension does not match the expected size"
        )
        String extension,
        @RequestPart
        @Parameter(
            description = "Files",
            array = @ArraySchema(
                minItems = OA_ARRAY_FILES_MIN,
                maxItems = OA_ARRAY_FILES_MAX,
                schema = @Schema(
                    type = "string",
                    format = "binary",
                    maxLength = OA_FILE_CONTENT_MAX
                )
            )
        )
        @Size(
            min = OA_ARRAY_FILES_MIN,
            max = OA_ARRAY_FILES_MAX,
            message = "File array does not match the expected size"
        )
        @UniqueMultipart
        MultipartFile[] files
    ) throws OperationException, ExtensionAlreadyExistsException, DataProcessingException, RootNotValidException;

    @PutMapping(
        value = API_PUT_BY_EXTS,
        produces = {MediaType.APPLICATION_JSON_VALUE},
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    @UpdateDocumentsByExt
    UpdateDocumentsResDTO updateDocuments(
        @PathVariable(name = API_PATH_EXTS_VAR)
        @Parameter(
            description = "Extension identifier",
            schema = @Schema(
                minLength = OA_EXTS_STRING_MIN,
                maxLength = OA_EXTS_STRING_MAX
            )
        )
        @NotBlank(message = "Extension cannot be blank")
        @Size(
            min = OA_EXTS_STRING_MIN,
            max = OA_EXTS_STRING_MAX,
            message = "Extension does not match the expected size"
        )
        String extension,
        @RequestPart
        @Parameter(
            description = "Files",
            array = @ArraySchema(
                minItems = OA_ARRAY_FILES_MIN,
                maxItems = OA_ARRAY_FILES_MAX,
                schema = @Schema(
                    type = "string",
                    format = "binary",
                    maxLength = OA_FILE_CONTENT_MAX
                )
            )
        )
        @Size(
            min = OA_ARRAY_FILES_MIN,
            max = OA_ARRAY_FILES_MAX,
            message = "File array does not match the expected size"
        )
        @UniqueMultipart
        MultipartFile[] files
    ) throws OperationException, ExtensionNotFoundException, DocumentNotFoundException, DataProcessingException, DataIntegrityException;

    @DeleteMapping(API_DELETE_BY_EXTS)
    @DeleteDocumentsByExt
    DeleteDocumentsResDTO deleteDocuments(
        @PathVariable(name = API_PATH_EXTS_VAR)
        @Parameter(
            description = "Extension identifier",
            schema = @Schema(
                minLength = OA_EXTS_STRING_MIN,
                maxLength = OA_EXTS_STRING_MAX
            )
        )
        @NotBlank(message = "Extension cannot be blank")
        @Size(
            min = OA_EXTS_STRING_MIN,
            max = OA_EXTS_STRING_MAX,
            message = "Extension does not match the expected size"
        )
        String extension
    ) throws OperationException, ExtensionNotFoundException, DataIntegrityException;
}
