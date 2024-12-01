package client.scenes;

import commons.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private MainController sut;

    /**
     * Setting up the Controller for the tests.
     */
    @BeforeEach
    void setup() {
        sut = new MainController();
        sut.setSavedNotes(FXCollections.observableArrayList());
    }

    @Test
    void getSavedNotesTest() {
        assertNotNull(sut.getSavedNotes());
        assertTrue(sut.getSavedNotes().isEmpty());
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
    void handleRefreshTest(){
        //method not implemented yet
    }

    @Test
    void updateDisplayTest(){
        //method not implemented yet
    }

}
