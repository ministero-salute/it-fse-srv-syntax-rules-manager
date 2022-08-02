package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

import lombok.Getter;

import java.util.List;

/**
 * If the given root filename doesn't match one of the provided schema files
 */
@Getter
public class RootNotValidException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = -5864543132277838362L;

    /**
     * Provided value
     */
    private final String value;

    /**
     * Expected values
     */
    private final List<String> values;

    /**
     * Message constructor.
     *
     * @param msg    Message to be shown.
     * @param value  Provided value
     * @param values Expected values
     */
    public RootNotValidException(final String msg, String value, List<String> values) {
        super(msg);
        this.value = value;
        this.values = values;
    }
}
