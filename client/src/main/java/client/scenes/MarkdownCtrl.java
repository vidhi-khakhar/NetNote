package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class MarkdownCtrl implements Initializable {

    @FXML
    private TextArea myTextArea;

    @FXML
    private WebView myWebView;

    @FXML
    private TextArea problemsTextArea;

    @FXML
    private CheckBox problemsCheckBox;

    /**
     * Stores the text currently in the editor
     */

    private String content = "";

    /**
     * Made this attribute because for some reason
     * the program would do the getProblems method twice.
     */

    private boolean problemChecked = false;

    /**
     * Initializes the WebView element.
     * It updates the WebView panel when the TextArea is updated.
     * @param url not used
     * @param resourceBundle not used
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myTextArea.setWrapText(true);
        myTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            content = newValue;
            myWebView.getEngine().loadContent(markdownToHtml());
        });
    }

    /**
     * Method that parses a markdown string to html.
     * @return html formatted string
     */

    public String markdownToHtml() {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(myTextArea.getText());
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    /**
     * Method that creates a temporary Html file
     * from the parsed markdown string.
     * Saves if you click anywhere in the app
     * (TextArea, Pane, WebView)
     * @throws IOException should the file not be found
     */

    public void saveHtml() throws IOException {
        File file = new File("markdown.html");
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(markdownToHtml());
        fileWriter.close();
        file.deleteOnExit();
    }

    /**
     * This method display the TextArea that contains the List
     * of Problems in the text editor, if there are any. When the
     * CheckBox is ticked off, the TextArea dissapears.
     */

    public void showProblems(){
        if(problemsCheckBox.isSelected()){
            problemsTextArea.setVisible(true);
            List<String> problems = getProblems();
            for(int i = 0; i < problems.size(); i++){
                problemsTextArea.appendText(problems.get(i));
                if(i < problems.size() - 1)
                    problemsTextArea.appendText("\n");
            }
        }
        else {
            problemsTextArea.setVisible(false);
            problemsTextArea.setText("");
        }
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
     * @return List of the problems
     *
     */

    public List<String> getProblems(){
        if(problemChecked) {
            problemChecked = false;
            return new ArrayList<>();
        }
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

            if(hashtagsOnly(line)) {
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
        problemChecked = true;
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
        if(line.charAt(0) == '#' && line.charAt(1) == ' ')
            return true;
        if(StringUtils.containsOnly(line, "#"))
            return true;
        return false;
    }
}
