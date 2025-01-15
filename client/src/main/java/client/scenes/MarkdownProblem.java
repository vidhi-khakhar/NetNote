package client.scenes;

import client.MyModule;
import client.utils.LangUtils;
import com.google.inject.Guice;
import com.google.inject.Inject;

public class MarkdownProblem {

    private int lineNr;
    private MarkdownProblemType type;
    private final LangUtils langUtils;

    /**
     * Constructor for the markdown problem object
     *
     * @param lineNr    The line number where the markdown problem occurs
     * @param type      The markdown problem type (enum)
     */
    @Inject
    public MarkdownProblem(int lineNr, MarkdownProblemType type) {
        this.lineNr = lineNr;
        this.type = type;
        this.langUtils = Guice.createInjector(new MyModule()).getInstance(LangUtils.class);
    }

    /**
     * Getter for the markdown problem type
     *
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
        return langUtils.getProps().get("markdownProblemAtLine").get() +
                " " +
                lineNr +
                ": "
                +
                langUtils.getProps().get(type.getBundleLabel()).get();
    }
}
