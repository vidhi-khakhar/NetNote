package commons;

import jakarta.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Moderating table between the Collection and Note entities.
 * This table represents a many-to-many relationship where each entry
 * associates one Collection with one Note. The primary key is a composite
 * key represented by the {@link CollectionContainsNotesId} class.
 * <p>
 * This entity does not contain additional business logic and serves
 * solely to facilitate the many-to-many relationship between the
 * {@link Collection} and {@link Note} entities.
 */
@Entity
public class CollectionContainsNotes {

    /**
     * The composite primary key for this entity, consisting of the collectionId
     * and noteId.
     */
    @EmbeddedId
    private CollectionContainsNotesId id;

    /**
     * Default constructor for JPA. It is required for the entity class to
     * have a no-argument constructor.
     */
    public CollectionContainsNotes() {
    }

    /**
     * Constructor to create a CollectionContainsNotes entity with the specified
     * collectionId and noteId. This constructor sets the composite primary key
     * for this entity.
     *
     * @param collectionId The ID of the Collection.
     * @param noteId       The ID of the Note.
     */
    public CollectionContainsNotes(String collectionId, Long noteId) {
        this.id = new CollectionContainsNotesId(collectionId, noteId);
    }

    /**
     * Gets the composite primary key for this entity.
     *
     * @return The composite primary key ({@link CollectionContainsNotesId}).
     */
    public CollectionContainsNotesId getId() {
        return id;
    }

    /**
     * Gets the ID of the associated Collection from the composite primary key.
     *
     * @return The ID of the Collection.
     */
    public String getCollectionId() {
        return id.getCollectionId();
    }

    /**
     * Gets the ID of the associated Note from the composite primary key.
     *
     * @return The ID of the Note.
     */
    public Long getNoteId() {
        return id.getNoteId();
    }

    /**
     * Sets the ID of the associated Collection from the composite primary key.
     *
     * @return The ID of the Collection.
     */
    public String setCollectionId() {
        return id.getCollectionId();
    }

    /**
     * Sets the ID of the associated Note from the composite primary key.
     *
     * @return The ID of the Note.
     */
    public Long setNoteId() {
        return id.getNoteId();
    }


    /**
     * @param anotherCollectionContainsNotes another anotherCollectionContainsNotes
     * @return whether two anotherCollectionContainsNotes are equal
     */
    @Override
    public boolean equals(Object anotherCollectionContainsNotes) {
        return EqualsBuilder.reflectionEquals(this, anotherCollectionContainsNotes);
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
