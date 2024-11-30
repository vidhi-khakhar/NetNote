package client.scenes;

public class Problem {

    private int lineNr;
    private ProblemType type;

    public Problem(int lineNr, ProblemType type) {
        this.lineNr = lineNr;
        this.type = type;
    }

    public ProblemType getType() {
        return type;
    }

    public String toString() {
        return "Problem at line " + lineNr + ": " + type.getDescription();
    }
}
