package client.scenes;

public enum MarkdownProblemType {
    NO_LINE("Make sure to keep empty lines between paragraphs."),
    HASHTAGS_ONLY("Incomplete heading, make sure to add text after the #'s"),
    HASHTAG_NO_SPACE("Leave a space after the #"),
    HASHTAGS_SPACE_HASHTAGS("Incomplete input, does not display anything, " +
            "either delete the #'s or add text after the last #"),
    INCORRECT_NUM_LIST("Leave a space between the start  of the list and the rest of the text"),
    INCORRECT_LIST("Leave a space between the hyphen and the rest of the text.");

    private final String description;
    MarkdownProblemType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }




}
