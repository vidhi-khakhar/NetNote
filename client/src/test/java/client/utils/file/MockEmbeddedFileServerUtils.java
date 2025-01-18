package client.utils.file;

import client.utils.note.MockNoteServerUtils;
import commons.EmbeddedFile;
import commons.Note;

import java.util.*;

public class MockEmbeddedFileServerUtils extends EmbeddedFileServerUtils {

    @Override
    public List<EmbeddedFile> getFilesFromNote(String serverAddress, String noteName) {
        Optional<Note> noteOptional = MockNoteServerUtils.notes.get(serverAddress).stream().filter(n -> n.getTitle().equals(noteName)).findFirst();
        if (noteOptional.isEmpty()) return new ArrayList<>();
        Note note = noteOptional.get();
        return note.getEmbeddedFiles();
    }

    @Override
    public void storeEmbeddedFile(String serverAddress, String noteName, EmbeddedFile file) {
        Optional<Note> noteOptional = MockNoteServerUtils.notes.get(serverAddress).stream().filter(n -> n.getTitle().equals(noteName)).findFirst();
        if (noteOptional.isEmpty()) return;
        Note note = noteOptional.get();
        note.getEmbeddedFiles().add(file);
    }

    @Override
    public void renameEmbeddedFile(String serverAddress, String oldTitle, String newTitle) {
        // TODO
    }

    @Override
    public void deleteEmbeddedFile(String serverAddress, String noteName, String fileName) {
        // TODO
    }
}
