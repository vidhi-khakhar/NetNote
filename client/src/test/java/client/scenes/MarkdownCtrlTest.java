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
    void correctTextTest() {
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello World!";
        List<String> problems = mc.getProblems(test);
        assertTrue(problems.isEmpty());
    }

    @Test
    void incorrectlySpacedUnderlinesTest() {
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello_World!_";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.NON_SEPARATED_UNDERLINES).toString(), problems.get(0));
    }

    @Test
    void wronglyWrittenUnderlineTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello_World!";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_UNDERLINES).toString(), problems.get(0));
    }

    @Test
    void missingEmptyLineTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello\nWorld!";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(2, MarkdownProblemType.NO_LINE).toString(), problems.get(0));
    }

    @Test
    void asterisksNotClosedProperlyTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "Hello*World!**";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_ASTERISKS).toString(), problems.get(0));
    }

    @Test
    void wronglyWrittenNumberedListTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "1.Hello World!";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_NUM_LIST).toString(), problems.get(0));
    }

    @Test
    void wronglyWrittenListTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "-Hello World!";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_LIST).toString(), problems.get(0));
    }

    @Test
    void hashtagsOnlyTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "#";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.HASHTAGS_ONLY).toString(), problems.get(0));
    }

    @Test
    void noSpaceAfterHashtagTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "#Hello World!";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.HASHTAG_NO_SPACE).toString(), problems.get(0));
    }

    @Test
    void hashtagSpacesHashtagTest(){
        MarkdownCtrl mc = new MarkdownCtrl();
        String test = "#   #";
        List<String> problems = mc.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.HASHTAGS_SPACE_HASHTAGS).toString(), problems.get(0));
    }
}