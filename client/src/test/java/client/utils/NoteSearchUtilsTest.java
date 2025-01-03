package client.utils;

import commons.Note;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NoteSearchUtilsTest {
    @Test
    public void testSearchWithNullQuery() {
        List<Note> notes = Arrays.asList(
                new Note("Note1", "This is a note."),
                new Note("Note2", "This is another note.")
        );

        List<Note> resultNotes = NoteSearchUtils.search(notes, null);

        assertEquals(2, notes.size());
        assertEquals("Note1", resultNotes.get(0).getTitle());
        assertEquals("Note2", resultNotes.get(1).getTitle());
    }

    @Test
    public void testSearchWithEmptyQuery() {
        List<Note> notes = Arrays.asList(
                new Note("Note1", "This is a note."),
                new Note("Note2", "This is a second note."),
                new Note("Note3", "This is another note")
            );

        List<Note> result = NoteSearchUtils.search(notes, "");

        assertEquals(3, result.size());
        assertEquals("Note1", result.get(0).getTitle());
        assertEquals("Note2", result.get(1).getTitle());
        assertEquals("Note3", result.get(2).getTitle());
    }

    @Test
    public void testSearchWithSingleWordQuery() {
        List<Note> notes = Arrays.asList(
                new Note("Important", "This is important information."),
                new Note("Random", "random text."),
                new Note("Meeting", "Summary of project meeting.")
        );

        List<Note> result = NoteSearchUtils.search(notes, "important");

        assertEquals(1, result.size());
        assertEquals("Important", result.get(0).getTitle());
    }

    @Test
    public void testSearchWithMultipleWordQuery() {
        List<Note> notes = Arrays.asList(
                new Note("Grocery shopping list", "This is a grocery shopping list."),
                new Note("Random", "random text"),
                new Note("Meeting", "Summary of project meeting.")
        );

        List<Note> result = NoteSearchUtils.search(notes, "random meeting");

        assertEquals(0, result.size());
    }

    @Test
    public void testSearchWithMultipleMatches() {
        List<Note> notes = Arrays.asList(
                new Note("Meeting Notes", "Meeting about next milestone."),
                new Note("Code of conduct", "Code of conduct for the new project."),
                new Note("Team Meeting", "Summary of team meeting.")
        );

        List<Note> result = NoteSearchUtils.search(notes, "meeting");

        assertEquals(2, result.size());
        assertEquals("Meeting Notes", result.get(0).getTitle());
        assertEquals("Team Meeting", result.get(1).getTitle());
    }

    @Test
    public void testSearchWithNoMatches() {
        List<Note> notes = Arrays.asList(
                new Note("Note1", "This is a note"),
                new Note("Note2", "This is another note")
        );

        List<Note> result = NoteSearchUtils.search(notes, "nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testSearchWithEmptyNotesList() {
        List<Note> notes = Collections.emptyList();

        List<Note> result = NoteSearchUtils.search(notes, "query");

        assertTrue(result.isEmpty());
    }
}


