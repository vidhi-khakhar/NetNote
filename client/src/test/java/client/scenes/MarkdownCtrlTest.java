package client.scenes;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownCtrlTest {

    @Test
    void markdownTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "cox";
        String render = mc.markdownToHtml(test);
        assertEquals("<p>cox</p>\n", render);
    }

    @Test
    void markdownHeadingTest() {
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "# poc";
        String render = mc.markdownToHtml(test);
        assertEquals("<h1>poc</h1>\n", render);
    }

    @Test
    void markdownParagraphTest() {
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello\n\nWorld";
        String render = mc.markdownToHtml(test);
        assertEquals("<p>Hello</p>\n<p>World</p>\n", render);
    }

    @Test
    void markdownProblemsTest() {
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello World!";
        List<String> problems = mc.getProblems(test);
        assertTrue(problems.isEmpty());
    }

    @Test
    void markdownProblemsTest2() {
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello_World!_";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.NON_SEPARATED_UNDERLINES).toString(), problems.get(0));
    }
}