package client.utils.undo;

public class ActionAbortedException extends RuntimeException {

    /**
     * Constructor for the ActionAbortedException unchecked exception.
     */
    public ActionAbortedException() {
        super("abortedActionException");
    }
}
