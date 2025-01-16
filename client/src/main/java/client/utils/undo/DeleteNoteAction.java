package client.utils.undo;

import client.scenes.MainCtrl;
import commons.Note;

public record DeleteNoteAction(Note deletedNote,
                               String originalTitle,
                               String originalText,
                               int noteIndex,
                               MainCtrl mainCtrl) implements ClientAction {

    /**
     * Undoes the note deletion
     */
    @Override
    public void undo() {
        mainCtrl.commitAddNote(deletedNote, noteIndex);
    }

    /**
     * Redoes the previously undone note deletion
     *
     * @throws CannotRedoException If the note to be re-deleted was modified
     *                             after the delete operation was undone
     */
    @Override
    public void redo() throws CannotRedoException {
        if (!deletedNote.getText().equals(originalText) ||
                !deletedNote.getTitle().equals(originalTitle))
            throw new CannotRedoException();
        if (!mainCtrl.handleDeleteNote(noteIndex, false))
            throw new ActionAbortedException();
    }
}
