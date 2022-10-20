/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

import com.mongodb.MongoException;

/**
 * Mainly used to catch database issues, it's used to describe a data-layer failure
 * By-design MongoDB drivers uses {@link RuntimeException} which are difficult to handle
 * without a consistent compiler-enforcing policy (e.g Checked Exceptions)
 * To simplify the handling of operations issues, this class takes in the {@link com.mongodb.MongoException}
 * and add a reasonable descriptive message to find the routine which lead to the error.
 * This exception is supposed to be generated and re-thrown as soon as a {@link com.mongodb.MongoException} is caught.
 * @author G. Baittiner
 */
public class OperationException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 6134857493429760036L;

    /**
     * Complete constructor.
     *
     * @param msg	Message to be shown.
     *              It should describe what the operation was trying to accomplish.
     * @param e		The original MongoExceptions.
     */
    public OperationException(final String msg, final MongoException e) {
        super(msg, e);
    }
}