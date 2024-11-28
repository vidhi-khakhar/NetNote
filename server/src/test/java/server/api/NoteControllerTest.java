package server.api;

import commons.Note;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class NoteControllerTest {

    private TestNoteRepository db;
    private NoteController sut;
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
}
