/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class CustomSwaggerCFG {

    @Value("${docs.info.api-id}")
	private String apiId;

	@Value("${docs.info.summary}")
	private String apiSummary;

    @Value("${docs.info.title}")
    private String title;

    @Value("${info.app.version}")
    private String version;

    @Value("${docs.info.description}")
    private String description;

    @Value("${docs.info.termsOfService}")
    private String termsOfService;

    @Value("${docs.info.contact.name}")
    private String contactName;

    @Value("${docs.info.contact.url}")
    private String contactUrl;

    @Value("${docs.info.contact.mail}")
    private String contactMail;

    @Value("${server.port}")
    private Integer port;

    @Value("${validation.file-max-size}")
    private Integer fileMaxLength;

}
