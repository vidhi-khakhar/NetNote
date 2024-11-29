package client.scenes;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainControllerTest {

    private MainController controller;
    private TextField inputTitle;
    private TextArea inputNote;
    private Label displayTitle;
    private Label displayNote;

    @BeforeEach
    public void setUp() {
        // Initialize the controller and UI components (mocked or real if necessary)
        controller = new MainController();

        // Manually instantiate the components as they would be in FXML
        inputTitle = new TextField();
        inputNote = new TextArea();
        displayTitle = new Label();
        displayNote = new Label();

        // Inject these components into the controller (simulating what FXML does)
        controller.setInputTitle(inputTitle);
        controller.setInputNote(inputNote);
        controller.setDisplayTitle(displayTitle);
        controller.setDisplayNote(displayNote);
    }

    @Test
    public void testRenderNoteWithValidInput() {
        // Given
        String title = "Test Title";
        String note = "This is a test note.";

        inputTitle.setText(title);
        inputNote.setText(note);

        // When
        controller.renderNote(null); // Pass null because we are not testing MouseEvent here

        // Then
        assertEquals(title, displayTitle.getText(), "The title should be updated.");
        assertEquals(note, displayNote.getText(), "The note should be updated.");
    }
}
