package client.utils.undo;

public interface ClientAction {

    /**
     * Undoes the action
     */
    void undo() throws CannotUndoException;

    /**
     * Redoes the previously undone action
     */
    void redo() throws CannotRedoException;
}
