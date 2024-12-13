package client.utils.undo;

public class ActionAbortedException extends RuntimeException {

    /**
     * Constructor for the ActionAbortedException unchecked exception.
     */
    public ActionAbortedException() {
        super("The action cannot be undone/redone because the user cancelled it");
    }
}
