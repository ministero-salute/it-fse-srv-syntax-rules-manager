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
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class OpenApiCFG {

    @Autowired
    private CustomSwaggerCFG customOpenapi;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("default")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    private OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // Popolamento sezione info
            openApi.getInfo().setTitle(customOpenapi.getTitle());
            openApi.getInfo().setVersion(customOpenapi.getVersion());
            openApi.getInfo().setDescription(customOpenapi.getDescription());
            openApi.getInfo().setTermsOfService(customOpenapi.getTermsOfService());

            // Contatto
            Contact contact = new Contact();
            contact.setName(customOpenapi.getContactName());
            contact.setUrl(customOpenapi.getContactUrl());
            contact.setEmail(customOpenapi.getContactMail());
            openApi.getInfo().setContact(contact);

            // Estensioni custom
            openApi.getInfo().addExtension("x-api-id", customOpenapi.getApiId());
            openApi.getInfo().addExtension("x-summary", customOpenapi.getApiSummary());

            // Flag sandbox per server HTTP
            for (Server server : openApi.getServers()) {
                if (!Pattern.matches("^https://.*", server.getUrl())) {
                    server.addExtension("x-sandbox", true);
                }
            }

            // Disabilita additionalProperties negli schema globali
            openApi.getComponents().getSchemas().values()
                    .forEach(schema -> schema.setAdditionalProperties(false));

            // Disabilita additionalProperties nei body delle request
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getRequestBody() != null) {
                        operation.getRequestBody().getContent().values().forEach(mediaType -> {
                            Schema<?> schema = mediaType.getSchema();
                            if (schema != null) {
                                schema.setAdditionalProperties(false);
                            }
                        });
                    }
                });
            });
        };
    }

    // Metodi ausiliari (eventualmente usabili)
    private Schema<?> getFileSchema(PathItem item) {
        MediaType mediaType = getMultipartFile(item);
        return mediaType != null ? mediaType.getSchema() : null;
    }

    private MediaType getMultipartFile(PathItem item) {
        Operation operation = getOperation(item);
        if (operation == null || operation.getRequestBody() == null) return null;
        Content content = operation.getRequestBody().getContent();
        return content.get(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private Operation getOperation(PathItem item) {
        if (item.getPost() != null) return item.getPost();
        if (item.getPatch() != null) return item.getPatch();
        if (item.getPut() != null) return item.getPut();
        return null;
    }
}
