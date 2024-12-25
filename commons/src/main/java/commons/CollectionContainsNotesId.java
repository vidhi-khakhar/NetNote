package commons;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for CollectionContainsNotes.
 * This class is used as an embedded ID in the CollectionContainsNotes entity
 * and should be immutable once set.
 */
@Embeddable
public class CollectionContainsNotesId implements Serializable {

    private Long collectionId;
    private Long noteId;

    // Default constructor
    public CollectionContainsNotesId() {}

    /**
     * Constructor to create a composite primary key with a collection ID and a note ID.
     *
     * @param collectionId The ID of the Collection.
     * @param noteId The ID of the Note.
     */
    public CollectionContainsNotesId(Long collectionId, Long noteId) {
        this.collectionId = collectionId;
        this.noteId = noteId;
    }

    /**
     * Gets the ID of the associated Collection.
     *
     * @return The ID of the Collection.
     */
    public Long getCollectionId() {
        return collectionId;
    }

    /**
     * Gets the ID of the associated Note.
     *
     * @return The ID of the Note.
     */
    public Long getNoteId() {
        return noteId;
    }

    /**
     * Determines equality of two CollectionContainsNotesId objects based on their field values.
     *
     * @param o The object to compare to.
     * @return True if both objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollectionContainsNotesId)) return false;
        CollectionContainsNotesId that = (CollectionContainsNotesId) o;
        return Objects.equals(collectionId, that.collectionId) && Objects.equals(noteId, that.noteId);
    }

    /**
     * Generates a hash code based on the field values of the composite key.
     *
     * @return A hash code representing the composite key.
     */
    @Override
    public int hashCode() {
        return Objects.hash(collectionId, noteId);
    }
}
