package client.scenes;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {
    private MainController controller;

    @BeforeEach
    void setUp() {
        controller = new MainController();

        // Correct types for display fields (Label)
        controller.setInputTitle(new TextField());
        controller.setInputNote(new TextArea());
        controller.setDisplayTitle(new Label());
        controller.setDisplayNote(new Label());
    }

    @Test
    void testRenderNoteWithEmptyInputs() {
        // Simulate empty input fields
        controller.getInputTitle().setText("");
        controller.getInputNote().setText("");

        // Trigger the render method
        controller.renderNote(null);

        // Assertions
        assertEquals("The title is empty", controller.getDisplayTitle().getText());
        assertEquals("The note is empty", controller.getDisplayNote().getText());
    }

    @Test
    void testRenderNoteWithValidInputs() {
        // Simulate valid input fields
        controller.getInputTitle().setText("My Note Title");
        controller.getInputNote().setText("This is the content of the note.");

        // Trigger the render method
        controller.renderNote(null);

        // Assertions
        assertEquals("My Note Title", controller.getDisplayTitle().getText());
        assertEquals("This is the content of the note.", controller.getDisplayNote().getText());
    }
}
