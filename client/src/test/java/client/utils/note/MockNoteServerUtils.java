package client.utils.note;

import commons.Note;

import java.util.Map;
import java.util.*;

public class MockNoteServerUtils extends NoteServerUtils {

    private final List<Note> notes = new ArrayList<>();

    @Override
    public List<Note> getAllNotes() {
        return notes;
    }

    @Override
    public void storeNote(Note note) {
        notes.add(note);
    }

    @Override
    public void updateNote(String title, Map<String, String> noteData) {
        Optional<Note> noteOptional = notes.stream().filter(n -> n.getTitle().equals(title)).findFirst();
        if (noteOptional.isEmpty()) return;
        Note note = noteOptional.get();

        String newTitle = noteData.get("title");
        if (newTitle != null) {
            note.setTitle(newTitle);
        }
        String newText = noteData.get("text");
        if (newText != null) {
            note.setText(newText);
        }
    }

    @Override
    public void deleteNote(String title) {
        Optional<Note> noteOptional = notes.stream().filter(n -> n.getTitle().equals(title)).findFirst();
        if (noteOptional.isEmpty()) return;
        Note note = noteOptional.get();
        notes.remove(note);
    }
}
