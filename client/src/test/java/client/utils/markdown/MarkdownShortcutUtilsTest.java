package client.utils.markdown;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownShortcutUtilsTest {

    private MarkdownShortcutUtils sut;

    @BeforeEach
    public void setUp() {
        sut = new MarkdownShortcutUtils();
    }

    @Test
    public void surroundingCaretChangesTextTest() {
        var data = sut.toggleShortcut("", 12, "<u>", "</u>");
        assertEquals("<u></u>", data.getKey());
    }

    @Test
    public void surroundingCaretChangesPositionTest() {
        var data = sut.toggleShortcut("", 12, "<u>", "</u>");
        assertEquals(15, data.getValue());
    }

    @Test
    public void removeUnderlineTest() {
        var data = sut.toggleShortcut("<u>Underlined</u>", 8, "<u>", "</u>");
        assertEquals("Underlined", data.getKey());
    }

    @Test
    public void addUnderlineTest() {
        var data = sut.toggleShortcut("Underline me", 8, "<u>", "</u>");
        assertEquals("<u>Underline me</u>", data.getKey());
    }
}