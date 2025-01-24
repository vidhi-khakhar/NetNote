package client.utils.undo;

public class CannotRedoException extends Exception {

    /**
     * Constructor for the CannotRedoException checked exception.
     */
    public CannotRedoException() {
        super("cannotRedoException");
    }
}
