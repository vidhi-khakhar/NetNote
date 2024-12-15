package client.scenes;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MarkdownCtrl {

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
     *  <br>1. #, then at least 1 space, then #, then 0 or more spaces.
     *  <br>2. only # on a line.
     *  <br>3. checks that there is an empty line between paragraphs.
     *  <br>4. checks if a space is left between - and the rest of the text.
     *  <br>5. checks if a space is left between # and the rest of the text.
     *  <br>6. checks if a space is left between the start of the numbered
     *  list and the rest of the text.
     *  <br>7. checks if the symbols * and _ are correctly written when trying to
     *  make something italic, bold, or both.
     *  <br>8. checks if a sequenced that is between the symbol _ is separated from the rest
     *  of the text by whitespace on both sides.
     *  <br>Doesn't check sublists yet (list within a list).
     * @param content the String it is checking
     * @return List of the problems
     */
    public List<String> getProblems(String content) {
        List<String> problems = new ArrayList<>();
        Scanner scanner = new Scanner(content);
        int lineNr = 0;
        boolean lastLineBlank = false;
        String lastLine = "";
        while (scanner.hasNextLine()) {
            lineNr++;
            String line = scanner.nextLine();

            checkParagraphSpacing(lineNr, line, lastLine, lastLineBlank, problems);
            checkIncorrectList(lineNr, line, problems);
            checkNumberedList(lineNr, line, problems);
            checkSymbolClosing(lineNr, line, '*', problems);
            checkSymbolClosing(lineNr, line, '_', problems);
            checkUnderlinesSpacing(lineNr, line, problems);
            checkHashtags(lineNr, line, problems);

            lastLineBlank = line.isBlank();
            lastLine = line;
        }
        scanner.close();
        return problems;
    }

    /**
     * Checks if there is no empty line between paragraphs.
     *
     * @param lineNr        The current line number.
     * @param line          The current line content.
     * @param lastLine      The content of the previous line.
     * @param lastLineBlank Whether the previous line was blank.
     * @param problems      The list to add problems to.
     */
    private void checkParagraphSpacing(int lineNr, String line, String lastLine, boolean lastLineBlank, List<String> problems) {
        if (lineNr != 1 && spaceBetweenParagraphs(line, lastLine, lastLineBlank)) {
            MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.NO_LINE);
            problems.add(problem.toString());
        }
    }

    /**
     * Checks if a list using '-' is written correctly.
     * @param lineNr   The current line number.
     * @param line     The current line content.
     * @param problems The list to add problems to.
     */
    private void checkIncorrectList(int lineNr, String line, List<String> problems) {
        if (!line.isEmpty() && line.charAt(0) == '-' && !listCheck(line)) {
            MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.INCORRECT_LIST);
            problems.add(problem.toString());
        }
    }

    /**
     * Checks if a ist using '1.' is written correctly.
     * @param lineNr   The current line number.
     * @param line     The current line content.
     * @param problems The list to add problems to.
     */
    private void checkNumberedList(int lineNr, String line, List<String> problems) {
        if ((!line.isEmpty() && numberedListStarterCondition(line)) && !numberedListCheck(line)) {
            MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.INCORRECT_NUM_LIST);
            problems.add(problem.toString());
        }
    }

    /**
     * Checks if a symbol is closed correctly.
     * @param lineNr   The current line number.
     * @param line     The current line content.
     * @param symbol   The symbol to check
     * @param problems The list to add problems to.
     */
    private void checkSymbolClosing(int lineNr, String line, char symbol, List<String> problems) {
        if (!symbolClosedCorrectly(line, symbol)) {
            MarkdownProblemType problemType = symbol == '*' ?
                    MarkdownProblemType.INCORRECT_ASTERISKS : MarkdownProblemType.INCORRECT_UNDERLINES;
            MarkdownProblem problem = new MarkdownProblem(lineNr, problemType);
            problems.add(problem.toString());
        }
    }

    /**
     * Checks if a sequence between underscores '_' is spaced correctly.
     *
     * @param lineNr   The current line number.
     * @param line     The current line content.
     * @param problems The list to add problems to.
     */
    private void checkUnderlinesSpacing(int lineNr, String line, List<String> problems) {
        if (!underlinesSpacedCorrectly(line)) {
            MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.NON_SEPARATED_UNDERLINES);
            problems.add(problem.toString());
        }
    }

    /**
     * Checks if a line starting with hashtags '#' is formatted correctly.
     * It checks for improperly spaced hashtags or lines with only hashtags.
     *
     * @param lineNr   The current line number.
     * @param line     The current line content.
     * @param problems The list to add problems to.
     */
    private void checkHashtags(int lineNr, String line, List<String> problems) {
        if (hashtagStarterCondition(line)) {
            if (hashtagsOnly(line)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.HASHTAGS_ONLY);
                problems.add(problem.toString());
            } else if (!spaceAfterHashTag(line)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.HASHTAG_NO_SPACE);
                problems.add(problem.toString());
            }
            Scanner lineScanner = new Scanner(line);
            if (hashtagSpacesHashtag(lineScanner)) {
                MarkdownProblem problem = new MarkdownProblem(lineNr, MarkdownProblemType.HASHTAGS_SPACE_HASHTAGS);
                problems.add(problem.toString());
            }
            lineScanner.close();
        }
    }


    /**
     * Starter condition for lines that start with #
     * @param line line that is being checked
     * @return true or false
     */

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
            return !lineNum || !lastLineNum;
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
        if(split.length == 1)
            return true;
        if(line.indexOf('.') == line.length() - 1)
            return true;
        return line.indexOf(' ') == line.indexOf('.') + 1;
    }

    /**
     * Starter condition for a line that starts with for example: 1.
     * @param line line that is being checked
     * @return true or false
     */

    private boolean numberedListStarterCondition(String line){
        if(!line.contains("."))
            return false;
        String[] split = line.split("\\.");
        return NumberUtils.isCreatable(split[0]);
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
        return line.charAt(0) == '-' && line.charAt(1) == ' ';
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
            return line.charAt(i) == ' ';
        }
        return StringUtils.containsOnly(line, "#");
    }

    /**
     * Checks if a line has correctly closed sequenced. For example:
     *  hello **world** is correct, but hello **world* isn't.
     * @param line line that is being checked
     * @param symbol chosen symbol
     * @return true if it's closed correctly in every situation, false otherwise
     */

    private boolean symbolClosedCorrectly(String line, char symbol){
        int oneAst = 0; // italic
        int twoAst = 0; // bold
        int threeAst = 0; // bold and italic
        for(int i = 0; i < line.length(); i++){
            if(line.charAt(i) == '\\'){
                i++;
                continue;
            }
            int nrAst = 0;
            if(line.charAt(i) == symbol) {
                while (line.charAt(i) == symbol) {
                    nrAst++;
                    i++;
                    if(i == line.length())
                        break;
                }
                i--;
                if(nrAst == 1)
                    oneAst++;
                else
                    if(nrAst % 2 == 0)
                        twoAst++;
                    else
                        threeAst++;
            }
        }
        return oneAst % 2 == 0 && twoAst % 2 == 0 && threeAst % 2 == 0;
    }

    /**
     * This method checks if a sequence that is between underlines
     * is separated by whitespace on its left and right.
     * @param line line that is being checked
     * @return true if its correctly spaced, false otherwise
     */

    private boolean underlinesSpacedCorrectly(String line){
        while(true){
            int first = findNextUnderline(line, 0, true);
            int second = findNextUnderline(line, line.length() - 1, false);

            if(first == second)
                return true;
            if((first + 1 != line.length() && first != 0) && line.charAt(first - 1) != ' ')
                return false;
            if((second + 1 != line.length() && second != 0) && line.charAt(second + 1) != ' ')
                return false;
            line = line.substring(first + 1, second);
        }
    }

    /**
     * THis method finds the next valid underline.
     * @param line     The line being checked.
     * @param start    The starting index for the search.
     * @param forward  Whether to search forward (true) or backward (false).
     * @return The position of the next valid underline, or -1 if none is found.
     */
    private int findNextUnderline(String line, int start, boolean forward) {
        boolean ok = true;
        int pos = -1;
        while (ok) {
            if (forward)
                pos = line.indexOf('_', start);
            else
                pos = line.lastIndexOf('_', start);
            if (pos == -1 || pos == 0 || line.charAt(pos - 1) != '\\')
                ok = false;
            else {
                if (forward)
                    start = pos + 1;
                else
                    start = pos - 1;
            }
        }
        return pos;
    }
}
