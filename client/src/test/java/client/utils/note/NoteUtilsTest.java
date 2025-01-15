package client.utils.note;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import commons.Note;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NoteUtilsTest {

    private NoteUtils sut = new NoteUtils();
    private Note n1, n2, n3, n4;
    private List<Note> outgoing, incoming;

    @BeforeEach
    void setUp() {
        sut = new NoteUtils();
        n1 = new Note("Note 1", "");
        n2 = new Note("Note 2", "");
        n3 = new Note("Note 3", "");
        n4 = new Note("Note 4", "");
        outgoing = new ArrayList<>();
        outgoing.add(n1);
        outgoing.add(n2);
        outgoing.add(n3);
        incoming = new ArrayList<>();
        incoming.add(n1);
        incoming.add(n2);
        incoming.add(n3);
    }

    @Test
    public void doesNotChangeSyncedDataTest() {
        sut.sync(outgoing, incoming);
        assertEquals(List.of(n1, n2, n3), outgoing);
    }

    @Test
    public void doesNotDeleteOutgoingNotesTest() {
        incoming.remove(n1);
        incoming.remove(n2);
        sut.sync(outgoing, incoming);
        assertEquals(List.of(n1, n2, n3), outgoing);
    }

    @Test
    public void addsNewIncomingNotesTest() {
        incoming.add(n4);
        sut.sync(outgoing, incoming);
        assertEquals(List.of(n1, n2, n3, n4), outgoing);
    }

    @Test
    public void ignoresChangesInSameTitledNotes() {
        Note newNote = new Note("Note 1", "12345");
        incoming.remove(n1);
        incoming.add(newNote);
        assertEquals(List.of(n1, n2, n3), outgoing);
    }
}
