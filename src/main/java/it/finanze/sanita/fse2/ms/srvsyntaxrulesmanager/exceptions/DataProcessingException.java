/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

import java.io.IOException;

/**
 * Describe data elaboration exception (I/O conversions)
 * @author G. Baittiner
 */
public class DataProcessingException extends IOException {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 6134857493429760036L;

    /**
     * Complete constructor.
     *
     * @param msg	Message to be shown.
     * @param e		Exception to be shown.
     */
    public DataProcessingException(final String msg, final Exception e) {
        super(msg, e);
    }

}