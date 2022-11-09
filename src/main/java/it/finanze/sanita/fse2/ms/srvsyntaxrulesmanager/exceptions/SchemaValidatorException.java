/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

public class SchemaValidatorException extends Exception {
    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public SchemaValidatorException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
