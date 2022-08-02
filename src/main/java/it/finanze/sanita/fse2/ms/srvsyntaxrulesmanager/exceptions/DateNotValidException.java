package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

/**
 * Describe an issue while retrieving the date-time of a given changeset
 * @author G. Baittiner
 */
public class DateNotValidException extends Exception {
    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 616231106069395250L;

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public DateNotValidException(final String msg) {
        super(msg);
    }
}
