package client.utils.markdown;

import javafx.util.Pair;

public class MarkdownShortcutUtils {

    /**
     * If no text is selected, the caret will be surrounded with the Markdown
     * symbols so that the following text typed will be bold / italic / underlined. <br>
     * If some text is selected, it will remove the formatting if it is present,
     * and add it if it is not (acts as a toggle).
     *
     * @param selectedText The selected text to change
     * @param pos The current caret position
     * @param prefix The Markdown formatting before the selected text
     * @param suffix The Markdown formatting after the selected text
     * @return A pair consisting of the new text in the selection and
     * the new caret position.
     */
    public Pair<String, Integer> toggleShortcut(String selectedText,
                                                int pos,
                                                String prefix,
                                                String suffix) {
        if (!selectedText.isEmpty()) {
            if (selectedText.startsWith(prefix) && selectedText.endsWith(suffix)) {
                selectedText = selectedText.substring(
                        prefix.length(),
                        selectedText.length() - suffix.length());
            } else {
                selectedText = prefix + selectedText + suffix;
            }
        } else {
            selectedText = prefix + suffix;
            pos += prefix.length();
        }
        return new Pair<>(selectedText, pos);
    }
}
