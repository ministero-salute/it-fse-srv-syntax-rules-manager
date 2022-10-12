package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstract base controller
 * @author G. Baittiner
 */
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

    protected boolean validateFiles(MultipartFile[] files) {
		boolean isValid = true;
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				if (!isValidSchema(file)) {
					isValid = false;
					break;
				}
			}
		} else {
			isValid = false;
		}

		return isValid;
	}

    private boolean isValidSchema(MultipartFile file) {

		boolean isValid = false;
		if (file != null && !file.isEmpty()) {
			try {
				final Path path = Paths.get(file.getOriginalFilename());
				final String mimeType = Files.probeContentType(path);
				final String content = new String(file.getBytes(), StandardCharsets.UTF_8);

				isValid = MimeTypeUtils.TEXT_XML_VALUE.equals(mimeType) && content.startsWith("<?xml") && content.contains("xs:schema");
			} catch (Exception e) {
				isValid = false;
			}
		}

		return isValid;
	}

}
