package client.utils.markdown;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownUtilsTest {

    private MarkdownUtils sut;

    @BeforeEach
    void setUp() {
        sut = new MarkdownUtils();
    }

    @Test
    void markdownTest(){
        String test = "cox";
        String render = sut.markdownToHtml(test);
        assertEquals("<p>cox</p>\n", render);
    }

    @Test
    void markdownHeadingTest() {
        String test = "# poc";
        String render = sut.markdownToHtml(test);
        assertEquals("<h1>poc</h1>\n", render);
    }

    @Test
    void markdownParagraphTest() {
        String test = "Hello\n\nWorld";
        String render = sut.markdownToHtml(test);
        assertEquals("<p>Hello</p>\n<p>World</p>\n", render);
    }

    @Test
    void correctTextTest() {
        String test = "Hello World!";
        List<String> problems = sut.getProblems(test);
        assertTrue(problems.isEmpty());
    }

    @Test
    void incorrectlySpacedUnderlinesTest() {
        String test = "Hello_World!_";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.NON_SEPARATED_UNDERLINES).toString(), problems.get(0));
    }

    @Test
    void wronglyWrittenUnderlineTest(){
        String test = "Hello_World!";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_UNDERLINES).toString(), problems.get(0));
    }

    @Test
    void missingEmptyLineTest(){
        String test = "Hello\nWorld!";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(2, MarkdownProblemType.NO_LINE).toString(), problems.get(0));
    }

    @Test
    void asterisksNotClosedProperlyTest(){
        String test = "Hello*World!**";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_ASTERISKS).toString(), problems.get(0));
    }

    @Test
    void wronglyWrittenNumberedListTest(){
        String test = "1.Hello World!";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_NUM_LIST).toString(), problems.get(0));
    }

    @Test
    void wronglyWrittenListTest(){
        String test = "-Hello World!";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.INCORRECT_LIST).toString(), problems.get(0));
    }

    @Test
    void hashtagsOnlyTest(){
        String test = "#";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.HASHTAGS_ONLY).toString(), problems.get(0));
    }

    @Test
    void noSpaceAfterHashtagTest(){
        String test = "#Hello World!";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.HASHTAG_NO_SPACE).toString(), problems.get(0));
    }

    @Test
    void hashtagSpacesHashtagTest(){
        String test = "#   #";
        List<String> problems = sut.getProblems(test);
        assertEquals(new MarkdownProblem(1, MarkdownProblemType.HASHTAGS_SPACE_HASHTAGS).toString(), problems.get(0));
    }
}