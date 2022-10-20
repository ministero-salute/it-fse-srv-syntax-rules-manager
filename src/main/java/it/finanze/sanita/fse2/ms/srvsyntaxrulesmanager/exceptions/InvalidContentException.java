/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;



public class InvalidContentException extends Exception {

	/**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public InvalidContentException(final String msg) {
        super(msg);
    }
}
