package client.utils;

import commons.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class NoteServerUtilsTest {

    private NoteServerUtils noteServerUtils;
    private Note n1;

    @BeforeEach
    void setup() {

        noteServerUtils = new NoteServerUtils();

        //allow the testing only if server is available
        assumeTrue(noteServerUtils.isServerAvailable(noteServerUtils.getServerAddress()), "Server is not available, skipping tests.");

        n1 = new Note("Note1Test1", "Note 1 content");

        // Store the notes before each test
        noteServerUtils.storeNote(n1);
    }

    @Test
    void cannotConnectToServerWithInvalidAddressTest() {
        String serverAddress = "test://test.test:test/";
        assertFalse(noteServerUtils.isServerAvailable(serverAddress));
    }

    @Test
    void cannotConnectToServerWithIncompatibleAPITest() {
        String serverAddress = "https://www.google.com/";
        assertFalse(noteServerUtils.isServerAvailable(serverAddress));
    }

    @Test
    void canConnectToLocalHostTest() {
        String serverAddress = "http://localhost:8080/";
        assertTrue(noteServerUtils.isServerAvailable(serverAddress));
    }

    @Test
    void getAllNotes() {
        // Retrieve all notes
        List<Note> notes = noteServerUtils.getAllNotes();

        // Verify that the notes list contains the notes that were stored
        boolean containsNote1 = notes.stream().anyMatch(note -> note.getTitle().equals(n1.getTitle()) && note.getText().equals(n1.getText()));
        assertTrue(containsNote1, "Note 1 should be present in the list of notes.");
    }

    @Test
    void storeNote() {
        // Verify the note was stored successfully
        boolean isTitlePresentAndTextMatchesTest = noteServerUtils.getAllNotes().stream()
                .anyMatch(note -> note.getTitle().equals(n1.getTitle()) && note.getText().equals(n1.getText()));
        assertTrue(isTitlePresentAndTextMatchesTest);
    }

    @Test
    void updateNoteTitleOnly() {
        // Prepare data to update the note
        HashMap<String, String> patch = new HashMap<>();
        patch.put("title", "updatedTitle");

        // Perform the update
        noteServerUtils.updateNote(n1.getTitle(), patch);

        // Verify that the title was updated
        boolean isTitlePresent = noteServerUtils.getAllNotes().stream()
                .anyMatch(note -> note.getTitle().equals("updatedTitle"));
        assertTrue(isTitlePresent);

        // Clean up: reset the note to its original title
        patch = new HashMap<>();
        patch.put("title", n1.getTitle());
        noteServerUtils.updateNote("updatedTitle", patch);
    }

    @Test
    void deleteNote() {
        // Store a new note to delete
        Note noteToDelete = new Note("NoteToDelete", "Content to delete");
        noteServerUtils.storeNote(noteToDelete);

        // Delete the note (assuming you have a deleteNote method)
        noteServerUtils.deleteNote(noteToDelete.getTitle());

        // Verify that the note is deleted
        boolean isNoteDeleted = noteServerUtils.getAllNotes().stream()
                .noneMatch(note -> note.getTitle().equals(noteToDelete.getTitle()));
        assertTrue(isNoteDeleted);
    }

    @AfterEach
    void cleanup() {
        // Clean up: Delete the notes after each test, only if tests are available
        assumeTrue(noteServerUtils.isServerAvailable(noteServerUtils.getServerAddress()));
        noteServerUtils.deleteNote(n1.getTitle());
    }
}
