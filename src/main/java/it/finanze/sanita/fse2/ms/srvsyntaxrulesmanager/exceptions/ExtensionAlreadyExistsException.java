/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

/**
 * When a given extension conflicts with another one already available on the domain
 * @author G. Baittiner
 */
public class ExtensionAlreadyExistsException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 6134857493429760036L;

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public ExtensionAlreadyExistsException(final String msg) {
        super(msg);
    }

}