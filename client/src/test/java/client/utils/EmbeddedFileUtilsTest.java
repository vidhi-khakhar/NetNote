package client.utils;

import client.utils.note.NoteServerUtils;
import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class EmbeddedFileServerUtilsTest {

    private EmbeddedFileServerUtils embeddedFileServerUtils;
    private NoteServerUtils noteServerUtils;
    private Note noteWithFile;
    private Note noteWithoutFile;
    private EmbeddedFile file1;
    private String serverAddress;  // Declare a serverAddress variable

    @BeforeEach
    void setup() {

        embeddedFileServerUtils = new EmbeddedFileServerUtils();
        noteServerUtils = new NoteServerUtils();

        // Initialize the server address
        serverAddress = noteServerUtils.getServerAddress();  // Fetch the server address

        // Allow the tests only if the server is available
        assumeTrue(embeddedFileServerUtils.isServerAvailable(serverAddress), "Server is not available," +
                "skipping tests.");

        // Create notes and an embedded file
        noteWithFile = new Note("NoteWithFile", "This note has a file.");
        noteWithoutFile = new Note("NoteWithoutFile", "This note has no files.");
        file1 = new EmbeddedFile("File 1", ".pdf", new byte[]{1, 2, 3, 4}, noteWithFile);

        // Store the notes and attach a file to one of them
        noteServerUtils.storeNote(noteWithFile);
        noteServerUtils.storeNote(noteWithoutFile);
        embeddedFileServerUtils.storeEmbeddedFile(serverAddress, noteWithFile.getTitle(), file1);
    }

//    @Test
//    void attachFileToNote() {
//        // Verify the file was attached successfully
//        List<EmbeddedFile> files = embeddedFileServerUtils.getFilesFromNote(serverAddress, noteWithFile.getTitle());
//        assertEquals(1, files.size());
//        assertEquals(file1.getName(), files.get(0).getName());
//    }
//
//    @Test
//    void getFilesForNoteWithFiles() {
//        // Retrieve files for the note
//        List<EmbeddedFile> files = embeddedFileServerUtils.getFilesFromNote(serverAddress, noteWithFile.getTitle());
//
//        // Verify the correct file is retrieved
//        assertNotNull(files);
//        assertFalse(files.isEmpty());
//        assertEquals(file1.getName(), files.get(0).getName());
//    }

    @Test
    void getFilesForNoteWithoutFiles() {
        // Retrieve files for a note without any files
        List<EmbeddedFile> files = embeddedFileServerUtils.getFilesFromNote(serverAddress, noteWithoutFile.getTitle());

        // Verify the result is an empty list
        assertNotNull(files);
        assertTrue(files.isEmpty());
    }

    @Test
    void deleteFileFromNote() {
        // Delete the file
        embeddedFileServerUtils.deleteEmbeddedFile(serverAddress, noteWithFile.getTitle(), file1.getName());

        // Verify the file was deleted
        List<EmbeddedFile> files = embeddedFileServerUtils.getFilesFromNote(serverAddress, noteWithFile.getTitle());
        assertTrue(files.isEmpty());
    }

    @Test
    void deleteNoteWithFiles() {
        noteServerUtils.deleteNote(noteWithFile.getTitle());

        List<Note> notes = noteServerUtils.getAllNotes();
        boolean noteExists = notes.stream().anyMatch(note -> note.getTitle().equals(noteWithFile.getTitle()));
        assertFalse(noteExists);

        List<EmbeddedFile> files = embeddedFileServerUtils.getFilesFromNote(serverAddress, noteWithFile.getTitle());
        assertTrue(files.isEmpty());
    }

    @AfterEach
    void cleanup() {
        // Clean up: Delete the notes and their associated files after each test
        assumeTrue(embeddedFileServerUtils.isServerAvailable(serverAddress));

        embeddedFileServerUtils.deleteEmbeddedFile(serverAddress, noteWithFile.getTitle(), file1.getName());
        noteServerUtils.deleteNote(noteWithFile.getTitle());
        noteServerUtils.deleteNote(noteWithoutFile.getTitle());
    }
}
