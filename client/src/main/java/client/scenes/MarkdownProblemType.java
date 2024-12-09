package client.scenes;

public enum MarkdownProblemType {

    NO_LINE("Make sure to keep empty lines between paragraphs."),
    HASHTAGS_ONLY("Incomplete heading, make sure to add text after the #'s"),
    HASHTAG_NO_SPACE("Leave a space after the #"),
    HASHTAGS_SPACE_HASHTAGS("Incomplete input, does not display anything, " +
            "either delete the #'s or add text after the last #"),
    INCORRECT_NUM_LIST("Leave a space between the start  of the list and the rest of the text"),
    INCORRECT_LIST("Leave a space between the hyphen and the rest of the text."),
    INCORRECT_ASTERISKS("Asterisks have not been closed properly. " +
            "If you want to use regular asterisks, use the escape sequence \\*"),
    INCORRECT_UNDERLINES("Underlines have not been closed properly. " +
            "If you want to use regular underlines, use the escape sequence \\_"),
    NON_SEPARATED_UNDERLINES("A sequence between underlines must separated from " +
            "the rest of the text by a white space.");

    private final String description;

    MarkdownProblemType(String description) {
        this.description = description;
    }

    /**
     * Getter for the description of the markdown problem type
     * @return The description of the markdown problem type
     */
    public String getDescription() {
        return description;
    }
}
