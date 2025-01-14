package client.utils.note;

import commons.Note;

import java.util.*;

public class NoteUtils {

    /**
     * Syncs two lists of notes with different changes. Modifies the outgoing list.
     *
     * @param outgoing The list of notes to be pushed (e.g. from the client)
     * @param incoming The list of notes to be pulled (e.g. from the server)
     */
    public void sync(List<Note> outgoing, List<Note> incoming) {
        List<String> titlesOfCurrentNotes = outgoing
                .stream()
                .map(Note::getTitle)
                .toList();
        incoming.forEach(note -> {
            // This ignores the case in which the server has a different
            // version of a note with the same title.
            if (!titlesOfCurrentNotes.contains(note.getTitle())) {
                outgoing.add(note);
            }
        });
    }
}
