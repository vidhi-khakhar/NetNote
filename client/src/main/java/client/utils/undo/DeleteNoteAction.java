package client.utils.undo;

import client.scenes.MainCtrl;
import commons.Note;

public record DeleteNoteAction(Note deletedNote,
                               String originalTitle,
                               String originalText,
                               int selectedNoteIndex,
                               MainCtrl mainCtrl) implements ClientAction {

    /**
     * Undoes the note deletion
     */
    @Override
    public void undo() {
        mainCtrl.handleUndoDelete(deletedNote, selectedNoteIndex);
    }

    /**
     * Redoes the previously undone note deletion
     */
    @Override
    public void redo() throws CannotRedoException {
        if (!deletedNote.getText().equals(originalText) ||
                !deletedNote.getTitle().equals(originalTitle))
            throw new CannotRedoException();
        mainCtrl.commitDeleteNote(selectedNoteIndex);
    }
}
