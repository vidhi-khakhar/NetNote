package client.utils.undo;

import client.scenes.MainCtrl;
import commons.EmbeddedFile;

public record AddFileAction(EmbeddedFile file, MainCtrl mainCtrl) implements ClientAction {

    /**
     * Undoes the file addition
     *
     * @throws CannotUndoException If the file cannot be removed
     */
    @Override
    public void undo() throws CannotUndoException {
        try {
            mainCtrl.undoAddFile(file);
        } catch (Exception e) {
            throw new CannotUndoException();
        }
    }

    /**
     * Redoes the previously undone file addition
     *
     * @throws CannotRedoException If the file cannot be added
     */
    @Override
    public void redo() throws CannotRedoException {
        try {
            mainCtrl.redoAddFile(file);
        } catch (Exception e) {
            throw new CannotRedoException();
        }
    }
}
