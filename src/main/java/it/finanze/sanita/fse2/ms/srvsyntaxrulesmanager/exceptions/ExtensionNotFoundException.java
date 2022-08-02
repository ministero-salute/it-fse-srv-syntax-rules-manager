package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.exceptions;

/**
 * Where the requested extension is not found, this exception kicks in
 * @author G. Baittiner
 */
public class ExtensionNotFoundException extends Exception {

    /**
     * Serial version uid
     */
    private static final long serialVersionUID = 6134857493429760036L;

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public ExtensionNotFoundException(final String msg) {
        super(msg);
    }

}