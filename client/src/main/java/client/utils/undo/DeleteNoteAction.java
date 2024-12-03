package client.utils.undo;

import client.scenes.MainCtrl;
import commons.Note;

public record DeleteNoteAction(Note deletedNote,
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
    public void redo() {
        // TODO
    }
}
