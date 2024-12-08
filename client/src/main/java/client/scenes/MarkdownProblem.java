package client.scenes;

public class MarkdownProblem {

    private int lineNr;
    private MarkdownProblemType type;

    /**
     * Constructor for the markdown problem object
     * @param lineNr The line number where the markdown problem occurs
     * @param type The markdown problem type (enum)
     */
    public MarkdownProblem(int lineNr, MarkdownProblemType type) {
        this.lineNr = lineNr;
        this.type = type;
    }

    /**
     * Getter for the markdown problem type
     * @return The type of the markdown problem (enum)
     */
    public MarkdownProblemType getType() {
        return type;
    }

    /**
     * Returns a human-friendly string representation of the markdown problem
     * @return A human-friendly string representation of the markdown problem
     */
    public String toString() {
        return "Problem at line " + lineNr + ": " + type.getDescription();
    }
}
