/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

/**
 * Where the requested document is not found, this exception kicks in
 * @author G. Baittiner
 */
public class DocumentNotFoundException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 6134857493429760036L;

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public DocumentNotFoundException(final String msg) {
        super(msg);
    }

}