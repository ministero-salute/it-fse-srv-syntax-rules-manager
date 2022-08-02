package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.controller;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.dto.response.log.LogTraceInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;

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
}
