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

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.error.ErrorInstance;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions.RootNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants.Logs.ERR_SRV_INVALID_ROOT_EXT;

/**
 * Abstract base controller
 */
@Slf4j
public abstract class AbstractCTL {

    /**
     * Tracker log.
     */
    @Autowired
    private Tracer tracer;

    /**
     * Generate a new {@link LogTraceInfoDTO} instance
     * @return The new instance
     */
    protected LogTraceInfoDTO getLogTraceInfo() {
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

	protected String checkRootExtension(String root) throws RootNotValidException {
		if (FilenameUtils.getExtension(root).equals("")) {
			log.warn("Root is missing extension name, 'xsd' will be added as default");
			root += ".xsd";
		} else if (!FilenameUtils.isExtension(root, "xsd")) {
			throw new RootNotValidException(String.format(ERR_SRV_INVALID_ROOT_EXT, root), ErrorInstance.Fields.ROOT);
		}
		return root;
	}

    protected boolean validateFiles(MultipartFile[] files) {
		boolean isValid = true;
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				if (!isValidFile(file)) {
					isValid = false;
					break;
				}
			}
		} else {
			isValid = false;
		}

		return isValid;
	}

    private boolean isValidFile(MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			try {
				final String extension = Optional.ofNullable(FilenameUtils.getExtension(file.getOriginalFilename())).orElse("");
				return extension.equals("xsd");
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}
}
