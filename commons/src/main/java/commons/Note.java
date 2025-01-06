package commons;

import jakarta.persistence.*;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmbeddedFile> embeddedFiles;


    /**
     * Default constructor is requested by JPA.
     */
    @SuppressWarnings("unused")
    protected Note() { }

    /**
     * Recommended constructor for the Note.
     * @param title title of the note
     * @param text raw text of the note
     */
    public Note(String title, String text) {
        this.title = title;
        this.text = text;
        embeddedFiles = new ArrayList<>();
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

    public void addEmbeddedFile(EmbeddedFile embeddedFile) {
        embeddedFile.setNote(this);
        embeddedFiles.add(embeddedFile);
    }

    public void removeEmbeddedFile(EmbeddedFile embeddedFile) {
        embeddedFiles.remove(embeddedFile);
        embeddedFile.setNote(null);
    }

    /**
     * @param anotherNote another note
     * @return whether two notes are equal
     */
    @Override
    public boolean equals(Object anotherNote) {
        return EqualsBuilder.reflectionEquals(this,anotherNote);
    }

    /**
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return String representation
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
