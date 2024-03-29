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
import java.util.Date;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorBuilderDTO.*;

/**
 *	Exceptions handler
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
     * Handles any document not found due to invalid references.
     *
     * @param ex exception
     */
    @ExceptionHandler(DocumentNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDocumentNotFoundException(DocumentNotFoundException ex) {
        // Log me
        log.error("HANDLER handleDocumentNotFoundException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDocumentNotFoundError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by a root value that does not conform to the accepted ones.
     *
     * @param ex exception
     */
    @ExceptionHandler(RootNotValidException.class)
    protected ResponseEntity<ErrorResponseDTO> handleRootNotValidException(RootNotValidException ex) {
        // Log me
        log.error("HANDLER handleRootNotValidException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createRootNotValidError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the database when
     * the number of expected operations does not match
     * with the ones executed.
     *
     * @param ex exception
     */
    @ExceptionHandler(DataIntegrityException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDataIntegrityException(DataIntegrityException ex) {
        // Log me
        log.error("HANDLER DataIntegrityException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDataIntegrityError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the data-layer. (e.g database write/read errors)
     *
     * @param ex exception
     */
    @ExceptionHandler(OperationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleOperationException(OperationException ex) {
        // Log me
        log.error("HANDLER handleOperationException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createOperationError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions generated by the conversion into bytes of the data passed by the user.
     * (e.g. UTF-8 is required but user provided ISO-8859)
     *
     * @param ex exception
     */
    @ExceptionHandler(DataProcessingException.class)
    protected ResponseEntity<ErrorResponseDTO> handleDataProcessingException(DataProcessingException ex) {
        // Log me
        log.error("HANDLER handleDataProcessingException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createDataProcessingError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the request to overwrite an already existing resource.
     *
     * @param ex exception
     */
    @ExceptionHandler(ExtensionAlreadyExistsException.class)
    protected ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExistsException(ExtensionAlreadyExistsException ex) {
        // Log me
        log.error("HANDLER handleResourceAlreadyExistsException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createExtensionExistsError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown from the read/write request of a non-existent resource.
     *
     * @param ex exception
     */
    @ExceptionHandler(ExtensionNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ExtensionNotFoundException ex) {
        // Log me
        log.error("HANDLER handleResourceNotFoundException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createExtensionNotFoundError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the absence of one or more required parts of a request.
     *
     * @param ex exception
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Log me
        log.error("HANDLER handleMissingServletRequestPart()", ex);
        // Create error DTO
        ErrorResponseDTO out = createMissingPartError(getLogTraceInfo(), ex);
        // Set HTTP headers
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the absence of one or more required parameter of a request.
     *
     * @param ex exception
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // Log me
        log.error("HANDLER handleMissingServletRequestParameter()", ex);
        // Create error DTO
        ErrorResponseDTO out = createMissingParameterError(getLogTraceInfo(), ex);
        // Set HTTP headers
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    /**
     * Handles exceptions thrown by the validation check performed on the request submitted by the user.
     *
     * @param ex exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        // Log me
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
     * Handles exceptions thrown by the inability to convert a certain value from a type X to a type Y.
     * (e.g. {@link String} to {@link Date})
     *
     * @param ex exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        // Log me
        log.error("HANDLER MethodArgumentTypeMismatchException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createArgumentMismatchError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }


    /**
     * Handles generic or unknown exceptions, unexpected thrown during the execution of any operation.
     *
     * @param ex exception
     */
    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // Log me
        log.error("HANDLER handleGenericException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createGenericError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    @ExceptionHandler(InvalidContentException.class)
    protected ResponseEntity<ErrorResponseDTO> handleInvalidContentException(InvalidContentException ex) {
        // Log me
        log.error("HANDLER handleInvalidContentException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createInvalidContentError(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return new ResponseEntity<>(out, headers, out.getStatus());
    }

    @ExceptionHandler(SchemaValidatorException.class)
    protected ResponseEntity<ErrorResponseDTO> handleSchemaValidatorException(SchemaValidatorException ex) {
        // Log me
        log.error("HANDLER handleSchemaValidatorException()", ex);
        // Create error DTO
        ErrorResponseDTO out = createSchemaValidatorException(getLogTraceInfo(), ex);
        // Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
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
