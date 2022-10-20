/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

import lombok.Getter;

/**
 * If the given root filename doesn't match one of the provided schema files
 */
@Getter
public class RootNotValidException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = -5864543132277838362L;

    private final String field;

    /**
     * Message constructor.
     *
     * @param msg    Message to be shown.
     * @param field  Field name
     */
    public RootNotValidException(final String msg, final String field) {
        super(msg);
        this.field = field;
    }
}
