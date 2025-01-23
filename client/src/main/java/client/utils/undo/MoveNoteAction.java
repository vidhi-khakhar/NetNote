package client.utils.undo;

import client.scenes.MainCtrl;
import commons.Collection;
import commons.Note;

public record MoveNoteAction(Note movedNote,
                             Collection source,
                             Collection target,
                             MainCtrl mainCtrl) implements ClientAction {
    /**
     * Undoes the note move
     *
     * @throws CannotUndoException If the move cannot be performed
     *                             (perhaps because the target server is unavailable)
     */
    @Override
    public void undo() throws CannotUndoException {
        try {
            mainCtrl.commitMoveNote(movedNote, target, source);
        } catch (Exception e) {
            throw new CannotUndoException();
        }
    }

    /**
     * Redoes the previously undone note creation
     *
     * @throws CannotRedoException If the move cannot be performed
     */
    @Override
    public void redo() throws CannotRedoException {
        try {
            mainCtrl.commitMoveNote(movedNote, source, target);
        } catch (Exception e) {
            throw new CannotRedoException();
        }
    }
}
