package client.utils.markdown;

public enum MarkdownProblemType {

    NO_LINE("markdownNoLine"),
    HASHTAGS_ONLY("markdownHashtagsOnly"),
    HASHTAG_NO_SPACE("markdownHashtagNoSpace"),
    HASHTAGS_SPACE_HASHTAGS("markdownHashtagsSpaceHashtags"),
    INCORRECT_NUM_LIST("markdownIncorrectNumList"),
    INCORRECT_LIST("markdownIncorrectList"),
    INCORRECT_ASTERISKS("markdownIncorrectAsterisks"),
    INCORRECT_UNDERLINES("markdownIncorrectUnderlines"),
    NON_SEPARATED_UNDERLINES("markdownNonSeparatedUnderlines");

    private final String bundleLabel;

    MarkdownProblemType(String bundleLabel) {
        this.bundleLabel = bundleLabel;
    }

    /**
     * Getter for the name of the entry in the languages bundle for the given Markdown problem
     * @return The name of the entry in the languages bundle for the given Markdown problem
     */
    public String getBundleLabel() {
        return bundleLabel;
    }
}
