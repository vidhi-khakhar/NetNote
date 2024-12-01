package client.scenes;

public class MarkdownProblem {

    private int lineNr;
    private MarkdownProblemType type;

    public MarkdownProblem(int lineNr, MarkdownProblemType type) {
        this.lineNr = lineNr;
        this.type = type;
    }

    public MarkdownProblemType getType() {
        return type;
    }

    public String toString() {
        return "Problem at line " + lineNr + ": " + type.getDescription();
    }
}
