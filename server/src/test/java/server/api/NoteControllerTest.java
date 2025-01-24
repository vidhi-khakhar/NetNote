package server.api;

import commons.Note;
import org.junit.jupiter.api.*;
import server.utils.ServerUtils;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class NoteControllerTest {

    private TestNoteRepository db;
    private TestEmbeddedFileRepository fileDb;
    private NoteController sut; // System Under Test
    private Note n1, n2;

    @BeforeEach
    public void setUp() {
        db = new TestNoteRepository();
        fileDb = new TestEmbeddedFileRepository();
        n1 = new Note("Note1", "Note 1 content");
        n2 = new Note("Note2", "Note 2 content");
        db.save(n1);
        db.save(n2);
        sut = new NoteController(db, new ServerUtils(db, fileDb));
    }

    @Test
    public void storeNote() {
        Note n3 = new Note("Note3", "Note 3 content");
        sut.storeNote(n3);
        assertEquals(List.of(n1, n2, n3), db.findAll());
    }

    @Test
    public void getAllNotes() {
        assertEquals(List.of(n1, n2), sut.getAllNotes());
    }

    @Test
    public void getNoteByTitle() {
        assertEquals(n1, sut.getNoteByTitle("Note1").getBody());
    }

    @Test
    public void updateNoteInvalidTitle() {
        assertEquals(404, sut.updateNote("Note3", null)
                .getStatusCode()
                .value());
    }

    @Test
    public void updateNoteInvalidBody() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newTitle", "Changed note title");  // Key should be "title"
        requestBody.put("newText", "Changed note content"); // Key should be "text"
        assertEquals(400, sut.updateNote("Note2", requestBody)
                .getStatusCode()
                .value());
    }

    @Test
    public void updateNoteTitleOnly() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", "Changed note title");
        assertEquals(200, sut.updateNote("Note2", requestBody)
                .getStatusCode()
                .value());
        assertEquals(new Note("Changed note title", "Note 2 content"),
                db.getById((long) 1));
    }

    @Test
    public void updateNoteTextOnly() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", "Changed note content");
        assertEquals(200, sut.updateNote("Note1", requestBody)
                .getStatusCode()
                .value());
        assertEquals(new Note("Note1", "Changed note content"),
                db.getById((long) 0));
    }

    @Test
    public void updateNoteBothTitleAndText() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", "Changed note title");
        requestBody.put("text", "Changed note content");
        assertEquals(200, sut.updateNote("Note2", requestBody)
                .getStatusCode()
                .value());
        assertEquals(new Note("Changed note title", "Changed note content"),
                db.getById((long) 1));
    }

    @Test
    public void deleteNoteInvalidId() {
        assertEquals(404, sut.deleteNote("Note123").getStatusCode().value());
        assertEquals(List.of(n1, n2), db.findAll());
    }

    @Test
    public void deleteNoteSuccess() {
        assertEquals(200, sut.deleteNote("Note2").getStatusCode().value());
    }
    @Test
    public void storeNoteDuplicateTitle() {
        Note duplicate = new Note("Note1", "Duplicate content");
        sut.storeNote(duplicate);
        // Assuming duplicate titles are allowed, the new note is added
        assertEquals(List.of(n1, n2, duplicate), db.findAll());
    }

    @Test
    public void getNoteByTitleInvalidTitle() {
        assertEquals(404, sut.getNoteByTitle("NonExistentTitle").getStatusCode().value());
        assertNull(sut.getNoteByTitle("NonExistentTitle").getBody());
    }

    @Test
    public void updateNoteWithNoChanges() {
        Map<String, String> requestBody = new HashMap<>();
        assertEquals(200, sut.updateNote("Note1", requestBody).getStatusCode().value());
        assertEquals(n1, db.getById((long) 0));
    }


    @Test
    public void deleteNoteNonExistent() {
        assertEquals(404, sut.deleteNote("NonExistentTitle").getStatusCode().value());
        assertEquals(List.of(n1, n2), db.findAll());
    }


    @Test
    public void updateNoteWithExtraFields() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", "Updated title");
        requestBody.put("text", "Updated text");
        requestBody.put("extra", "Extra field");
        assertEquals(400, sut.updateNote("Note1", requestBody).getStatusCode().value());
        assertEquals(n1, db.getById((long) 0));
    }
    @Test
    public void storeNoteNewNote() {
        Note newNote = new Note("Note3", "New note content");
        sut.storeNote(newNote);
        assertEquals(List.of(n1, n2, newNote), db.findAll());
    }

    @Test
    public void getAllNotesNonEmpty() {
        List<Note> notes = sut.getAllNotes();
        assertEquals(2, notes.size());
        assertTrue(notes.contains(n1));
        assertTrue(notes.contains(n2));
    }


    @Test
    public void updateNoteTitleOnlyValid() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", "Updated Title");
        assertEquals(200, sut.updateNote("Note1", requestBody).getStatusCode().value());
        Note updatedNote = db.getById((long) 0);
        assertEquals("Updated Title", updatedNote.getTitle());
        assertEquals("Note 1 content", updatedNote.getText());
    }

    @Test
    public void updateNoteTextOnlyValid() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", "Updated text content");
        assertEquals(200, sut.updateNote("Note2", requestBody).getStatusCode().value());
        Note updatedNote = db.getById((long) 1);
        assertEquals("Note2", updatedNote.getTitle());
        assertEquals("Updated text content", updatedNote.getText());
    }


}
