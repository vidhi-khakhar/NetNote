package client.scenes;

import commons.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainCtrlTest {

    private MainCtrl sut;

    /**
     * Setting up the Controller for the tests.
     */
    @BeforeEach
    void setup() {
        sut = new MainCtrl();
        sut.setSavedNotes(FXCollections.observableArrayList());
    }

    @Test
    void initializeSavedNotesTest() {
        assertNotNull(sut.getSavedNotes());
        assertTrue(sut.getSavedNotes().isEmpty());
    }

    @Test
    void getSavedNotesTest() {
        Note test = new Note("Title", "Text");
        sut.getSavedNotes().add(test);
        assertEquals(1, sut.getSavedNotes().size());
        assertEquals("Title", sut.getSavedNotes().get(0).getTitle());
        assertEquals("Text", sut.getSavedNotes().get(0).getText());
    }

    @Test
    void setSavedNotesTest() {
        ObservableList<Note> newNotes = FXCollections.observableArrayList(new Note("Title", "Text"));
        sut.setSavedNotes(newNotes);
        assertEquals(newNotes, sut.getSavedNotes());
    }

    @Test
    void handleDeleteNoteTest() {
        Note note = new Note("Title", "Text");
        sut.getSavedNotes().add(note);
        sut.getSavedNotes().remove(0);
        assertTrue(sut.getSavedNotes().isEmpty());
    }

    @Test
    void renderNoteTest() {
        Note newNote = new Note("Title", "Text");
        sut.getSavedNotes().add(newNote);

        assertEquals(1, sut.getSavedNotes().size());
        assertEquals("Title", sut.getSavedNotes().get(0).getTitle());
        assertEquals("Text", sut.getSavedNotes().get(0).getText());
    }

    @Test
    void handleAddNoteTest(){
        //test still to be implemented
    }
    @Test
    void handleRefreshNoteTest(){
        //method not implemented yet
    }

    @Test
    void updateDisplayTest(){
        //method not implemented yet
    }

    @Test
    public void testHighlightKeywordInText() {
        String text = "This is a test content with the word Test in it.";
        String keyword = "test";
        String expected = "This is a <span style=\"background-color: yellow;\">test</span> content with the word <span style=\"background-color: yellow;\">Test</span> in it.";

        String result = sut.highlightKeywordInText(text, keyword);

        assertEquals(expected, result);
    }

    @Test
    public void testEmptyKeyword() {
        String text = "This is some text.";
        String keyword = "";
        String expected = "This is some text."; // No change expected

        String result = sut.highlightKeywordInText(text, keyword);

        assertEquals(expected, result);
    }

    @Test
    public void testHighlightKeywordInTextWithMultipleOccurrences() {
        // Test case where the keyword occurs multiple times
        String text = "This is a test. Test again, test.";
        String keyword = "test";
        String expected = "This is a <span style=\"background-color: yellow;\">test</span>. <span style=\"background-color: yellow;\">Test</span> again, <span style=\"background-color: yellow;\">test</span>.";

        String result = sut.highlightKeywordInText(text, keyword);

        assertEquals(expected, result);
    }


    @Test
    void updateTitleTest(){
        //test still to be implemented
    }
}
