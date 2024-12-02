package client.utils.undo;

public interface ClientAction {

    /**
     * Undoes the action
     */
    void undo();

    /**
     * Redoes the previously undone action
     */
    void redo();
}
