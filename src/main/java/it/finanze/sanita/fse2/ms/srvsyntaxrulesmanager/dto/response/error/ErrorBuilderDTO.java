package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.base.ErrorResponseDTO.*;
import static org.apache.http.HttpStatus.*;

public final class ErrorBuilderDTO {

    public static final String VALIDATION_TITLE = "Validation error";
    public static final String GENERIC_TITLE = "Unknown error";
    public static final String EXTENSION_NOT_FOUND_TITLE = "Extension not found";
    public static final String OPERATION_TITLE = "Internal error";
    public static final String INTEGRITY_TITLE = "Compromised Data Integrity";
    public static final String EXTENSION_CONFLICT_TITLE = "Extension conflict";

    public static final String DATA_PROCESSING_TITLE = "Unfulfilled request";
    public static final String DOCUMENT_NOT_FOUND_TITLE = "Document not found";

    /**
     * Private constructor to disallow to access from other classes
     */
    private ErrorBuilderDTO() {}

    public static ErrorResponseDTO createConstraintError(LogTraceInfoDTO trace, ConstraintViolationException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            new ArrayList<>(ex.getConstraintViolations()).get(0).getMessage(),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }

    public static ErrorResponseDTO createArgumentMismatchError(LogTraceInfoDTO trace, MethodArgumentTypeMismatchException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            String.format(
                "Failed to convert %s to %s",
                ex.getName(),
                ex.getParameter().getParameter().getType().getSimpleName()
            ),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }

    public static ErrorResponseDTO createRootNotValidError(LogTraceInfoDTO trace, RootNotValidException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            String.format(
                "Root filename %s doesn't match any of the possible values: %s",
                ex.getValue(),
                Arrays.toString(ex.getValues().toArray())
            ),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }

    public static ErrorResponseDTO createDateNotValidError(LogTraceInfoDTO trace, DateNotValidException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            ex.getMessage(),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }

    public static ErrorResponseDTO createMissingPartError(LogTraceInfoDTO trace, MissingServletRequestPartException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            ex.getMessage(),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }

    public static ErrorResponseDTO createMissingParameterError(LogTraceInfoDTO trace, MissingServletRequestParameterException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            ex.getMessage(),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }

    public static ErrorResponseDTO createObjectIdNotValidError(LogTraceInfoDTO trace, ObjectIdNotValidException ex) {
        return new ErrorResponseDTO(
            trace,
            CONSTRAINT_ERROR,
            VALIDATION_TITLE,
            ex.getMessage(),
            SC_BAD_REQUEST,
            CONSTRAINT_ERROR
        );
    }


    public static ErrorResponseDTO createGenericError(LogTraceInfoDTO trace, Exception ex) {
        return new ErrorResponseDTO(
            trace,
            GENERIC_ERROR,
            GENERIC_TITLE,
            ex.getMessage(),
            SC_INTERNAL_SERVER_ERROR,
            GENERIC_ERROR
        );
    }

    public static ErrorResponseDTO createExtensionNotFoundError(LogTraceInfoDTO trace, ExtensionNotFoundException ex) {
        return new ErrorResponseDTO(
            trace,
            NOT_FOUND_ERROR,
            EXTENSION_NOT_FOUND_TITLE,
            ex.getMessage(),
            SC_NOT_FOUND,
            NOT_FOUND_ERROR
        );
    }

    public static ErrorResponseDTO createOperationError(LogTraceInfoDTO trace, OperationException ex) {
        return new ErrorResponseDTO(
            trace,
            OPERATION_ERROR,
            OPERATION_TITLE,
            ex.getMessage(),
            SC_INTERNAL_SERVER_ERROR,
            OPERATION_ERROR
        );
    }

    public static ErrorResponseDTO createExtensionExistsError(LogTraceInfoDTO trace, ExtensionAlreadyExistsException ex) {
        return new ErrorResponseDTO(
            trace,
            CONFLICT_ERROR,
            EXTENSION_CONFLICT_TITLE,
            ex.getMessage(),
            SC_CONFLICT,
            CONFLICT_ERROR
        );
    }

    public static ErrorResponseDTO createDataProcessingError(LogTraceInfoDTO trace, DataProcessingException ex) {
        return new ErrorResponseDTO(
            trace,
            DATA_PROCESSING_ERROR,
            DATA_PROCESSING_TITLE,
            ex.getMessage(),
            SC_UNPROCESSABLE_ENTITY,
            DATA_PROCESSING_ERROR
        );
    }

    public static ErrorResponseDTO createDocumentNotFoundError(LogTraceInfoDTO trace, DocumentNotFoundException ex) {
        return new ErrorResponseDTO(
            trace,
            NOT_FOUND_ERROR,
            DOCUMENT_NOT_FOUND_TITLE,
            ex.getMessage(),
            SC_NOT_FOUND,
            NOT_FOUND_ERROR
        );
    }

    public static ErrorResponseDTO createDataIntegrityError(LogTraceInfoDTO trace, DataIntegrityException ex) {
        return new ErrorResponseDTO(
            trace,
            DATA_INTEGRITY_ERROR,
            INTEGRITY_TITLE,
            ex.getMessage(),
            SC_INTERNAL_SERVER_ERROR,
            NOT_FOUND_ERROR
        );
    }

}
