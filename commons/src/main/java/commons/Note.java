package commons;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entity Note.
 * Has 3 fields: noteId(unmodifiable), title, text
 */
@Entity // Marks this class as a JPA entity.
public class Note {

    /**
     * noteID identifies the note in the database.
     * It is the primary key.
     */
    @Id // Specifies the primary key.
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-generated primary key.
    private Long noteId;

    /**
     * title of the note shouldn't be a null.
     */
    @Column(nullable = false) // Makes the column non-nullable in the database.
    private String title;

    /**
     * plain text of the note.
     */
    private String text;

    /**
     * Default constructor is requested by JPA.
     */
    public Note() { }

    /**
     * Recommended constructor for the Note.
     * @param title title of the note
     * @param text raw text of the note
     */
    public Note(String title, String text) {
        this.title = title;
        this.text = text;
    }

    /**
     * Getter for NoteId.
     * @return noteId
     */
    public Long getNoteId() {
        return noteId; //READ-ONLY
    }

    /**
     * Getter for title.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for title.
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for text.
     * @return text.
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for text.
     * @param text new text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return String representation of Note (for debugging purposes).
     */
    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    /**
     * Equals method of a note compares only the Notes' noteId.
     * @param o another note
     * @return whether two notes have the same noteId
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(noteId, note.noteId);
    }

    /**
     * Hashcode uses only noteId.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(noteId);
    }
}
