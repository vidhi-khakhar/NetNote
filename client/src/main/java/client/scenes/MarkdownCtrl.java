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

    private String content = "";
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
     *  1. #, then at least 1 space, then #, then 0 or more spaces,
     *  2. only # on a line,
     *  3. checks that there is an empty line between paragraphs,
     *  4. checks if a space is left between - and the rest of the text,
     *  5. checks if a space is left between # and the rest of the text,
     *  Doesn't check sublists yet(list within a list).
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
            String format = "Problem at line " + lineNr;
            String line = scanner.nextLine();

            if(lineNr != 1 && !problems.contains("Make sure to keep empty lines between paragraphs."))
                if(spaceBetweenParagraphs(line, lastLine, lastLineBlank))
                    problems.add("Make sure to keep empty lines between paragraphs.");

            if(!line.isEmpty() && (line.charAt(0) == '-' && !listCheck(line)))
                problems.add(format +
                        ": Leave a space between the hyphen and the rest of the text.");

            if(!line.isEmpty() && line.charAt(0) == '#') {
                Scanner lineScanner = new Scanner(line);
                if(hashtagSpacesHashtag(lineScanner))
                    problems.add(format + ": Incomplete input, " +
                            "does not display anything, " +
                            "either delete the #'s or add text after the last #");

                if(hashtagsOnly(line))
                    problems.add(format +
                            ": Incomplete heading, make sure to add text after the #'s");

                if(!spaceAfterHashTag(line))
                    problems.add(format + ": Leave space between the" +
                            " # and the rest of the text");
            }
            lastLineBlank = line.isBlank();
            lastLine = line;
        }
        problemChecked = true;
        return problems;
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
        if(line.length() == 2){
            char digit = line.charAt(0);
            char dot = line.charAt(1);
            if(Character.isDigit(digit) && dot == '.')
                return true;
        }
        if(line.length() > 2) {
            char digit = line.charAt(0);
            char dot = line.charAt(1);
            char space = line.charAt(2);
            if (Character.isDigit(digit) && dot == '.' && space == ' ')
                return true;
        }
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
