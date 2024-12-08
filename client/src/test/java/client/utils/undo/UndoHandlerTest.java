package client.utils.undo;

import java.util.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UndoHandlerTest {

    private UndoHandler sut;
    private List<String> testData = new ArrayList<>();

    @BeforeEach
    void setUp() {
        sut = new UndoHandler(3);
        testData = new ArrayList<>();
        testData.add("AB");
        sut.pushAction(new TestAction(testData, "AB"));
        testData.add("CD");
        sut.pushAction(new TestAction(testData, "CD"));
    }

    @Test
    void pushActionLeavesNoRedoableActions() {
        try {
            sut.undoAction();
            testData.add("EF");
            sut.pushAction(new TestAction(testData, "EF"));
            assertThrows(CannotRedoException.class, () -> sut.redoAction());
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void pushActionStillAllowsFurtherUndos() {
        try {
            sut.undoAction();
            testData.add("EF");
            sut.pushAction(new TestAction(testData, "EF"));
            sut.undoAction();
            sut.undoAction();
            assertEquals(List.of(), testData);
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void undoActionSuccess() {
        try {
            sut.undoAction();
            assertEquals(List.of("AB"), testData);
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void undoActionTooManyTimes() {
        try {
            sut.undoAction();
            sut.undoAction();
            assertThrows(CannotUndoException.class, () -> sut.undoAction());
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void undoActionAndRedoAction() {
        try {
            sut.undoAction();
            sut.redoAction();
            assertEquals(List.of("AB", "CD"), testData);
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void undoActionMoreThanThreeTimesFails() { // Because we set the capacity to be 3
        testData.add("EF");
        sut.pushAction(new TestAction(testData, "EF"));
        testData.add("GH");
        sut.pushAction(new TestAction(testData, "GH"));
        testData.add("IJ");
        sut.pushAction(new TestAction(testData, "IJ"));
        try {
            sut.undoAction();
            sut.undoAction();
            sut.undoAction();
            assertThrows(CannotUndoException.class, () -> sut.undoAction());
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void redoActionWithoutPreviousUndoFails() {
        assertThrows(CannotRedoException.class, () -> sut.redoAction());
    }

    @Test
    void noUndoSkipping() {
        try {
            testData.add("ZZ");
            sut.pushAction(new TestAction(testData, "ZZ", true, false));
            assertThrows(CannotUndoException.class, () -> sut.undoAction());
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void noRedoSkipping() {
        try {
            sut.undoAction();
            sut.undoAction();
            testData.add("ZZ");
            sut.pushAction(new TestAction(testData, "ZZ", false, true));
            testData.add("AA");
            sut.pushAction(new TestAction(testData, "AA"));
            sut.undoAction();
            sut.undoAction();
            assertThrows(CannotRedoException.class, () -> sut.redoAction());
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }

    @Test
    void blockingUndoDoesNotBlockRedo() {
        try {
            testData.add("ZZ");
            sut.pushAction(new TestAction(testData, "ZZ", true, false));
            testData.add("AA");
            sut.pushAction(new TestAction(testData, "AA"));
            sut.undoAction();
            assertThrows(CannotUndoException.class, () -> sut.undoAction());
            assertDoesNotThrow(() -> sut.redoAction());
        } catch (Exception e) {
            fail("Should be able to undo/redo");
        }
    }
}
