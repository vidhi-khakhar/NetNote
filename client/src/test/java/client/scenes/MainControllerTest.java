package client.scenes;

import commons.Note;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private MainController sut;

    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }

    /**
     * Setting up the Controller for the tests.
     */
    @BeforeEach
    void setup() {
        sut = new MainController();

        sut.setSavedNotes(FXCollections.observableArrayList());
        sut.setInputNote(new TextArea());
        sut.setInputTitle(new TextField());
        sut.setDisplayNote(new Label());
        sut.setDisplayTitle(new Label());
    }

    @Test
    void getSavedNotesTest() {
        assertNotNull(sut.getSavedNotes());
        assertTrue(sut.getSavedNotes().isEmpty());
    }

    @Test
    void setSavedNotesTest() {
        ObservableList<Note> newNotes = FXCollections.observableArrayList(new Note());
        sut.setSavedNotes(newNotes);
        assertEquals(newNotes, sut.getSavedNotes());
    }

    @Test
    void handleDeleteNoteTest() {
        Note note = new Note();
        note.setTitle("Test123");
        sut.getSavedNotes().add(note);
        sut.getSavedNotes().remove(0);
        assertTrue(sut.getSavedNotes().isEmpty());
    }

    @Test
    void displaySavedNoteTest() {
        Note note = new Note();
        note.setTitle("Title");
        note.setText("Text");
        sut.getSavedNotes().add(note);

        sut.getInputTitle().setText(note.getTitle());
        sut.getInputNote().setText(note.getText());

        assertEquals("Title", sut.getInputTitle().getText());
        assertEquals("Text", sut.getInputNote().getText());
    }

    @Test
    void renderNoteTest() {
        sut.getInputTitle().setText("Title");
        sut.getInputNote().setText("Text");
        sut.renderNote(null);

        assertEquals(1, sut.getSavedNotes().size());
        assertEquals("Title", sut.getSavedNotes().get(0).getTitle());
        assertEquals("Text", sut.getSavedNotes().get(0).getText());
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
