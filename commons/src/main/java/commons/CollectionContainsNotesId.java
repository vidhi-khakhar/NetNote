package commons;

import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 * Composite primary key class for CollectionContainsNotes.
 * This class is used as an embedded ID in the CollectionContainsNotes entity
 * and should be immutable once set.
 */
@Embeddable
public class CollectionContainsNotesId implements Serializable {

    /**
     * collectionId is part of the composite key.
     */
    private Long collectionId;
    /**
     * noteId is part of the composite key.
     */
    private Long noteId;

    /**
     * Default constructor is requested by JPA.
     */
    @SuppressWarnings("unused")
    public CollectionContainsNotesId() {}

    /**
     * Constructor to create a composite primary key
     * with a collection ID and a note ID.
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
     * @param anotherCollectionContainsNotesId another anotherCollectionContainsNotesId
     * @return whether two anotherCollectionContainsNotesIds are equal
     */
    @Override
    public boolean equals(Object anotherCollectionContainsNotesId) {
        return EqualsBuilder.reflectionEquals(this,anotherCollectionContainsNotesId);
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
