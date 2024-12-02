package client.utils.undo;

public class CannotUndoException extends Exception {

    /**
     * Constructor for the CannotUndoException checked exception.
     */
    public CannotUndoException() {
        super("You have reached the end of the undo stack. " +
                "You cannot go further back in the past.");
    }
}
