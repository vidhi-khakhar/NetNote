package client.utils.note;

import commons.Note;

import java.util.Map;
import java.util.*;

public class MockNoteServerUtils extends NoteServerUtils {

    public static Map<String, List<Note>> notes = new HashMap<>();

    @Override
    public List<Note> getAllNotes(String serverAddress) {
        return notes.get(serverAddress);
    }

    @Override
    public void storeNote(Note note, String serverAddress) {
        if (!notes.containsKey(serverAddress)) {
            notes.put(serverAddress, new ArrayList<>());
        }
        notes.get(serverAddress).add(note);
    }

    @Override
    public void updateNote(String title, Map<String, String> noteData, String serverAddress) {
        Optional<Note> noteOptional = notes.get(serverAddress).stream().filter(n -> n.getTitle().equals(title)).findFirst();
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
    public void deleteNote(String title, String serverAddress) {
        Optional<Note> noteOptional = notes.get(serverAddress).stream().filter(n -> n.getTitle().equals(title)).findFirst();
        if (noteOptional.isEmpty()) return;
        Note note = noteOptional.get();
        notes.get(serverAddress).remove(note);
    }
}
