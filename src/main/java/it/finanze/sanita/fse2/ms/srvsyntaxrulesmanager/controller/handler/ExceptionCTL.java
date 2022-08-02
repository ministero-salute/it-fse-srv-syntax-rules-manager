package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.handler;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.base.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorBuilderDTO.*;

/**
 *	Exceptions handler
 *  @author G. Baittiner
 */
@ControllerAdvice
@Slf4j
public class ExceptionCTL extends ResponseEntityExceptionHandler {

    /**
     * Tracker log.
     */
    @Autowired
    private Tracer tracer;

    /**
     * Handle object id not valid
     *
     * @param ex exception
     */
    @ExceptionHandler(ObjectIdNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleObjectIdNotValidException(ObjectIdNotValidException ex) {
        // Log me
        log.warn("HANDLER handleObjectIdNotValidException()");
        log.error("HANDLER handleObjectIdNotValidException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createObjectIdNotValidError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle document not found exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(DocumentNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        // Log me
        log.warn("HANDLER handleDocumentNotFoundException()");
        log.error("HANDLER handleDocumentNotFoundException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDocumentNotFoundError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle date not valid exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(DateNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDateNotValidException(DateNotValidException ex) {
        // Log me
        log.warn("HANDLER handleDateNotValidException()");
        log.error("HANDLER handleDateNotValidException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDateNotValidError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle root not valid exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(RootNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleRootNotValidException(RootNotValidException ex) {
        // Log me
        log.warn("HANDLER handleRootNotValidException()");
        log.error("HANDLER handleRootNotValidException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createRootNotValidError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle data integrity exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(DataIntegrityException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDataIntegrityException(DataIntegrityException ex) {
        // Log me
        log.warn("HANDLER DataIntegrityException()");
        log.error("HANDLER DataIntegrityException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDataIntegrityError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle operation exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(OperationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleOperationException(OperationException ex) {
        // Log me
        log.warn("HANDLER handleOperationException()");
        log.error("HANDLER handleOperationException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createOperationError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle I/O exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(DataProcessingException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDataProcessingException(DataProcessingException ex) {
        // Log me
        log.warn("HANDLER handleDataProcessingException()");
        log.error("HANDLER handleDataProcessingException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDataProcessingError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle resource conflicts exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(ExtensionAlreadyExistsException.class)
    protected ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExistsException(ExtensionAlreadyExistsException ex) {
        // Log me
        log.warn("HANDLER handleResourceAlreadyExistsException()");
        log.error("HANDLER handleResourceAlreadyExistsException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createExtensionExistsError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle resource not found exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(ExtensionNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ExtensionNotFoundException ex) {
        // Log me
        log.warn("HANDLER handleResourceNotFoundException()");
        log.error("HANDLER handleResourceNotFoundException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createExtensionNotFoundError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle missing request part.
     *
     * @param ex		exception
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Log me
        log.warn("HANDLER handleMissingServletRequestPart()");
        log.error("HANDLER handleMissingServletRequestPart()", ex);
        // Create error DTO
        ErrorResponseDTO out = createMissingPartError(getLogTraceInfo(), ex);
        // Set HTTP headers
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle missing request parameters.
     *
     * @param ex		exception
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Log me
        log.warn("HANDLER handleMissingServletRequestParameter()");
        log.error("HANDLER handleMissingServletRequestParameter()", ex);
        // Create error DTO
        ErrorResponseDTO out = createMissingParameterError(getLogTraceInfo(), ex);
        // Set HTTP headers
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle validation exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        // Log me
        log.warn("HANDLER handleConstraintViolationException()");
        log.error("HANDLER handleConstraintViolationException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createConstraintError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handle validation exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        // Log me
        log.warn("HANDLER MethodArgumentTypeMismatchException()");
        log.error("HANDLER MethodArgumentTypeMismatchException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createArgumentMismatchError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }


    /**
     * Handle generic exception.
     *
     * @param ex		exception
     */
    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // Log me
        log.warn("HANDLER handleGenericException()");
        log.error("HANDLER handleGenericException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createGenericError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        // Bye bye
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Generate a new {@link LogTraceInfoDTO} instance
     * @return The new instance
     */
    private LogTraceInfoDTO getLogTraceInfo() {
        // Create instance
        LogTraceInfoDTO out = new LogTraceInfoDTO(null, null);
        // Verify if context is available
        if (tracer.currentSpan() != null) {
            out = new LogTraceInfoDTO(
                tracer.currentSpan().context().spanIdString(),
                tracer.currentSpan().context().traceIdString());
        }
        // Return the log trace
        return out;
    }
}
