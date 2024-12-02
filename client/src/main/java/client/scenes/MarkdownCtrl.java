package client.scenes;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MarkdownCtrl{

    /**
     * Method that parses a markdown string to html.
     * @param content String that is being parsed
     * @return html formatted string
     */

    public String markdownToHtml(String content) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(content);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * Method that creates a list of the problems in the
     * text editor. So far it can detect the following problems:
     *  <br>1. #, then at least 1 space, then #, then 0 or more spaces
     *  <br>2. only # on a line,
     *  <br>3. checks that there is an empty line between paragraphs,
     *  <br>4. checks if a space is left between - and the rest of the text,
     *  <br>5. checks if a space is left between # and the rest of the text,
     *  <br>Doesn't check sublists yet(list within a list).
     * @param content the String it is checking
     * @return List of the problems
     *
     */

    public List<String> getProblems(String content){
        List<String> problems = new ArrayList<>();
        Scanner scanner = new Scanner(content);
        int lineNr = 0;
        boolean lastLineBlank = false;
        String lastLine = "";
        while (scanner.hasNextLine()) {
            lineNr++;
            String line = scanner.nextLine();

            if(lineNr != 1 && spaceBetweenParagraphs(line, lastLine, lastLineBlank)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr,
                        MarkdownProblemType.NO_LINE);
                problems.add(problem.toString());
            }

            if(!line.isEmpty() && (line.charAt(0) == '-' && !listCheck(line))) {
                MarkdownProblem problem = new MarkdownProblem(lineNr,
                        MarkdownProblemType.INCORRECT_LIST);
                problems.add(problem.toString());
            }

            if(hashtagStarterCondition(line) && hashtagsOnly(line)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr,
                        MarkdownProblemType.HASHTAGS_ONLY);
                problems.add(problem.toString());
            }

            Scanner lineScanner = new Scanner(line);
            if(hashtagStarterCondition(line) && hashtagSpacesHashtag(lineScanner)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr,
                        MarkdownProblemType.HASHTAGS_SPACE_HASHTAGS);
                problems.add(problem.toString());
            }
            lineScanner.close();

            if(hashtagStarterCondition(line) && !spaceAfterHashTag(line)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr,
                        MarkdownProblemType.HASHTAG_NO_SPACE);
                problems.add(problem.toString());

            }
            lastLineBlank = line.isBlank();
            lastLine = line;
        }
        return problems;
    }

    private boolean hashtagStarterCondition(String line){
        return !line.isEmpty() && line.charAt(0) == '#';
    }

    /**
     * Method that checks if there is an empty line between paragraphs
     * @param line the current line
     * @param lastLine the line before
     * @param lastLineBlank boolean value that represents if the last line was blank
     * @return true if there isn't space between paragraphs, false otherwise
     */

    private boolean spaceBetweenParagraphs(String line, String lastLine, boolean lastLineBlank) {
        if((!lastLineBlank && !line.isBlank())){
            boolean lineReg = listCheck(line);
            boolean lastLineReg = listCheck(lastLine);
            boolean lineNum = numberedListCheck(line);
            boolean lastLineNum = numberedListCheck(lastLine);
            if(lineReg && lastLineReg)
                return false;
            if(lineNum && lastLineNum)
                return false;
            return true;
        }
        return false;
    }

    /**
     * Check if a line is written correctly as a numbered list
     * @param line line that is being checked
     * @return true if it is written correctly, false otherwise
     */

    private boolean numberedListCheck(String line){
        if(line.length() < 2)
            return false;
        if(!line.contains("."))
            return false;
        String[] split = line.split("\\.");
        if(!NumberUtils.isCreatable(split[0]))
            return false;
        if(line.indexOf('.') == line.length() - 1)
            return true;
        if(line.indexOf(' ') == line.indexOf('.') + 1)
            return true;
        return false;
    }

    /**
     * Method that checks that a line doesn't contain #, then spaces,
     *  then #, and then more spaces
     * @param lineScanner scanner used to parse the line
     * @return true if the method only consists of #, false otherwise
     */

    private boolean hashtagSpacesHashtag(Scanner lineScanner){
        lineScanner.useDelimiter("\\s");
        lineScanner.next();
        if (lineScanner.hasNext()) {
            String checker = lineScanner.nextLine();
            checker = checker.strip();
            return hashtagsOnly(checker);
        }
        return false;
    }

    /**
     * Checks if a line only has #
     * @param line the line that is being checked
     * @return true or false
     */

    private boolean hashtagsOnly(String line){
        line = line.strip();
        boolean condition = true;
        for(int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != '#') {
                condition = false;
                break;
            }
        }
        return condition;
    }

    /**
     * Checks if a list is written correctly
     * @param line the line that is being checked
     * @return true if its correct, false otherwise
     */

    private boolean listCheck(String line){
        if(line.length() == 1)
            if(line.charAt(0) == '-')
                return true;
        if(line.charAt(0) == '-' && line.charAt(1) == ' ')
            return true;
        return false;
    }

    /**
     * Checks if there is space between the # and the rest of the text
     * @param line line that is being checked
     * @return true if there is space, false otherwise
     */

    private boolean spaceAfterHashTag(String line){
        if(line.length() == 1)
            if(line.charAt(0) == '#')
                return true;
        for(int i = 0; i < line.length(); i++){
            if(line.charAt(i) == '#')
                continue;
            if(line.charAt(i) == ' ')
                return true;
            else
                return false;
        }
        if(StringUtils.containsOnly(line, "#"))
            return true;
        return false;
    }
}
