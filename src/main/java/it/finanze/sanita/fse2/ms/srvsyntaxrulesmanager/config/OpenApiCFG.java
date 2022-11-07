/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;


import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * OpenAPI configuration class
 */
@Configuration
public class OpenApiCFG {

    @Autowired
	private CustomSwaggerCFG customOpenapi;

    @Bean
    public OpenApiCustomiser disableAdditionalRequestProperties() {

        final List<String> required = new ArrayList<>();
		required.add("file");
        required.add("requestBody");

        return openApi -> {

            // Populating info section.
            openApi.getInfo().setTitle(customOpenapi.getTitle());
            openApi.getInfo().setVersion(customOpenapi.getVersion());
            openApi.getInfo().setDescription(customOpenapi.getDescription());
            openApi.getInfo().setTermsOfService(customOpenapi.getTermsOfService());

            // Adding contact to info section
            final Contact contact = new Contact();
            contact.setName(customOpenapi.getContactName());
            contact.setUrl(customOpenapi.getContactUrl());
            contact.setEmail(customOpenapi.getContactMail());
            openApi.getInfo().setContact(contact);

            // Adding extensions
            openApi.getInfo().addExtension("x-api-id", customOpenapi.getApiId());
            openApi.getInfo().addExtension("x-summary", customOpenapi.getApiSummary());

            for (final Server server : openApi.getServers()) {
                final Pattern pattern = Pattern.compile("^https://.*");
                if (!pattern.matcher(server.getUrl()).matches()) {
                    server.addExtension("x-sandbox", true);
                }
            }

            openApi.getComponents().getSchemas().values().forEach(schema -> {
				schema.setAdditionalProperties(false);
			});

            openApi.getPaths().values()
			.stream().map(this::getFileSchema)
			.filter(Objects::nonNull)
			.forEach(schema -> {
				schema.additionalProperties(false);
				if (schema.getProperties().containsKey("file")) {
                    schema.getProperties().get("file").setMaxLength(customOpenapi.getFileMaxLength());
                }
			});
        };
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

    private Schema<?> getFileSchema(PathItem item) {
		MediaType mediaType = getMultipartFile(item);
		if (mediaType == null) return null;
		return mediaType.getSchema();
	}

    private MediaType getMultipartFile(PathItem item) {
		Operation operation = getOperation(item);
		if (operation == null) return null;
		RequestBody body = operation.getRequestBody();
		if (body == null) return null;
		Content content = body.getContent();
		if (content == null) return null;
        return content.get(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE);
	}

    private Operation getOperation(PathItem item) {
		if (item.getPost() != null) return item.getPost();
		if (item.getPatch() != null) return item.getPatch();
		if (item.getPut() != null) return item.getPut();
		return null;
	}
}
