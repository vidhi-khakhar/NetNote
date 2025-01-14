package client.utils.note;

import commons.Note;

import java.util.*;

public class NoteUtils {

    /**
     * Checks if a note can be added to the current list of notes
     * It won't be added if it has an empty or duplicate title.
     *
     * @param currentNotes The current list of notes
     * @param newNoteTitle The title of the note to add
     * @return True if the note can be added, false otherwise
     */
    public boolean canAddNote(List<Note> currentNotes, String newNoteTitle) {
        if (currentNotes.stream().anyMatch(n -> n.getTitle().equals(newNoteTitle)))
            return false;
        return !newNoteTitle.isBlank();
    }

    /**
     * Adds a new note with the given title to the list of notes
     *
     * @param currentNotes      The current list of notes
     * @param newNoteTitle      The title of the note to add
     * @param selectedNoteIndex The initial index of the selected note (or -1 if no
     *                          note is selected).
     * @return The new index of the selected note in the list of notes
     */
    public int addNote(List<Note> currentNotes, String newNoteTitle, int selectedNoteIndex) {
        if (!canAddNote(currentNotes, newNoteTitle))
            return selectedNoteIndex;
        currentNotes.add(new Note(newNoteTitle, ""));
        return currentNotes.size() - 1;
    }

    /**
     * Syncs two lists of notes with different changes.
     *
     * @param outgoing The list of notes to be pushed (e.g. from the client)
     * @param incoming The list of notes to be pulled (e.g. from the server)
     * @return A list of notes resulting from combining the two lists.
     */
    public List<Note> sync(List<Note> outgoing, List<Note> incoming) {
        List<Note> result = new ArrayList<>();
        List<String> titlesOfCurrentNotes = outgoing
                .stream()
                .map(Note::getTitle)
                .toList();
        incoming.forEach(note -> {
            // This ignores the case in which the server has a different
            // version of a note with the same title.
            if (!titlesOfCurrentNotes.contains(note.getTitle())) {
                result.add(note);
            }
        });
        return result;
    }
}
