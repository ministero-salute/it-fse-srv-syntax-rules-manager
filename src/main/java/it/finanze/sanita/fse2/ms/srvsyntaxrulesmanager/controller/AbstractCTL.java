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
 * @author G. Baittiner
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
