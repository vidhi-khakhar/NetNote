package client.utils.note;

import commons.Note;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for searching notes by title and content.
 */
public class NoteSearchUtils {
    /**
     * Searches through a list of notes for matches in the title or content.
     *
     * @param notes The list of notes to search through.
     * @param query The search query (supports multiple words).
     * @return A list of notes matching the query, sorted by title.
     */
    public static List<Note> search(List<Note> notes, String query) {
        if (query == null || query.trim().isEmpty()) {
            // Return all notes sorted by title if no query is provided
            return notes.stream()
                    .sorted(Comparator.comparing(Note::getTitle))
                    .collect(Collectors.toList());
        }

        String[] queryWords = query.toLowerCase().split("\\s+");

        return notes.stream()
                .filter(note -> {
                    String title = note.getTitle().toLowerCase();
                    String content = note.getText().toLowerCase();
                    for (String word : queryWords) {
                        if (!title.contains(word) && !content.contains(word)) {
                            return false;
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Note::getTitle))
                .collect(Collectors.toList());
    }
}

