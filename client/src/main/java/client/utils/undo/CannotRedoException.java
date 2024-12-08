package client.utils.undo;

public class CannotRedoException extends Exception {

    /**
     * Constructor for the CannotRedoException checked exception.
     */
    public CannotRedoException() {
        super("You have reached the most recent state of the program. " +
                "Cannot redo further.");
    }
}
