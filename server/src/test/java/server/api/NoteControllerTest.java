package server.api;

import commons.Note;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class NoteControllerTest {

    private TestNoteRepository db;
    private NoteController sut; // System Under Test
    private Note n1, n2;

    @BeforeEach
    public void setUp() {
        db = new TestNoteRepository();
        n1 = new Note("Note1", "Note 1 content");
        n2 = new Note("Note2", "Note 2 content");
        db.save(n1);
        db.save(n2);
        sut = new NoteController(db);
    }

    @Test
    public void post() {
        Note n3 = new Note("Note3", "Note 3 content");
        sut.storeNote(n3);
        assertEquals(List.of(n1, n2, n3), db.findAll());
    }

    @Test
    public void get() {
        assertEquals(List.of(n1, n2), sut.getAllNotes());
    }

    @Test
    public void patchInvalidId() {
        assertEquals(404, sut.updateNote((long) 123, null)
                .getStatusCode()
                .value());
    }

    @Test
    public void patchInvalidBody() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newTitle", "Changed note title");  // Key should be "title"
        requestBody.put("newText", "Changed note content"); // Key should be "text"
        assertEquals(400, sut.updateNote((long) 1, requestBody)
                .getStatusCode()
                .value());
    }

    @Test
    public void patchTitleOnly() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", "Changed note title");
        assertEquals(200, sut.updateNote((long) 1, requestBody)
                .getStatusCode()
                .value());
        assertEquals(new Note("Changed note title", "Note 2 content"),
                db.getById((long) 1));
    }

    @Test
    public void patchTextOnly() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", "Changed note content");
        assertEquals(200, sut.updateNote((long) 0, requestBody)
                .getStatusCode()
                .value());
        assertEquals(new Note("Note1", "Changed note content"),
                db.getById((long) 0));
    }

    @Test
    public void patchBothTitleAndText() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("title", "Changed note title");
        requestBody.put("text", "Changed note content");
        assertEquals(200, sut.updateNote((long) 1, requestBody)
                .getStatusCode()
                .value());
        assertEquals(new Note("Changed note title", "Changed note content"),
                db.getById((long) 1));
    }
}
