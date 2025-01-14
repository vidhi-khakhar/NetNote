package client.utils.undo;

import client.scenes.MainCtrl;
import commons.Note;

public record CreateNoteAction(Note createdNote,
                               String originalTitle,
                               String originalText,
                               int noteIndex,
                               MainCtrl mainCtrl) implements ClientAction {
    /**
     * Undoes the note creation
     *
     * @throws CannotUndoException If the note to be deleted
     *                             has been modified since its creation
     */
    @Override
    public void undo() throws CannotUndoException {
        if (!createdNote.getText().equals(originalText) ||
                !createdNote.getTitle().equals(originalTitle))
            throw new CannotUndoException();
        if (!mainCtrl.handleDeleteNote(noteIndex, false))
            throw new ActionAbortedException();
    }

    /**
     * Redoes the previously undone note creation
     */
    @Override
    public void redo() {
        mainCtrl.commitAddNote(createdNote, noteIndex);
    }
}
