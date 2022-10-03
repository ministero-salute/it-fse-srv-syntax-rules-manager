package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.media.Content;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * OpenAPI configuration class
 * @author G. Baittiner
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        extensions = {
            @Extension(properties = {
                @ExtensionProperty(name = "x-api-id", value = "1"),
                @ExtensionProperty(name = "x-summary", value = "Handles information requests from-to EDS")
            })
        },
        title = "EDS Syntax Service",
        version = "1.0.0",
        description = "Handles all information requests from-to EDS",
        termsOfService = "${docs.info.termsOfService}",
        contact = @Contact(
            name = "${docs.info.contact.name}",
            url = "${docs.info.contact.url}",
            email = "${docs.info.contact.mail}"
        )
    ),
    servers = {
        @Server(
            description = "EDS Syntax Service Development URL",
            url = "http://localhost:${server.port}",
            extensions = {
                @Extension(properties = {
                    @ExtensionProperty(name = "x-sandbox", parseValue = true, value = "true")
                })
            }
        )
    })
public class OpenApiCFG {

    private void disableAdditionalPropertiesToMultipart(Content content) {
        if(content.containsKey(MULTIPART_FORM_DATA_VALUE)) {
            content.get(MULTIPART_FORM_DATA_VALUE).getSchema().setAdditionalProperties(false);
        }
    }

    /**
     * Disable additional properties on every request object
     * @return The {@link OpenApiCustomiser} instance
     */
    @Bean
    public OpenApiCustomiser disableAdditionalRequestProperties() {
        return openApi -> openApi
            .getPaths()
            .values()
            .stream()
            .filter(item -> item.getPost() != null || item.getPut() != null)
            .forEach(item -> {
                if(item.getPut() != null) {
                    disableAdditionalPropertiesToMultipart(
                        item.getPut().getRequestBody().getContent()
                    );
                }
                if(item.getPost() != null) {
                    disableAdditionalPropertiesToMultipart(
                        item.getPost().getRequestBody().getContent()
                    );
                }
            }
        );
    }

    /**
     * Disable additional properties on every response object
     * @return The {@link OpenApiCustomiser} instance
     */
    @Bean
    public OpenApiCustomiser disableAdditionalResponseProperties() {
        return openApi -> openApi.getComponents().
            getSchemas().
            values().
            forEach( s -> s.setAdditionalProperties(false));
    }
}
