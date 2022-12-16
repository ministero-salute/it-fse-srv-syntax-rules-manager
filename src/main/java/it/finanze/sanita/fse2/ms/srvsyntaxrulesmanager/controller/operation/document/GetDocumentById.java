/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller.operation.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.changes.data.GetDocumentByIdResDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.base.ErrorResponseDTO;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

// OpenAPI descriptor
@Operation(
    summary = "Retrieve document by id",
    description = "Retrieves the document matching the given identifier"
)
@ApiResponses(
    value = {
        @ApiResponse(
            responseCode = "200",
            description = "Documenti trovati sul database",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GetDocumentByIdResDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "I parametri forniti non sono validi",
            content = @Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Documenti non trovati sul database",
            content = @Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(
                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponseDTO.class))
        )
    }
)
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetDocumentById {
}
