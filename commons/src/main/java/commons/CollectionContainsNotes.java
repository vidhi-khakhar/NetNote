package commons;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Moderating table between the Collection and Note entities.
 * This table represents a many-to-many relationship where each entry
 * associates one Collection with one Note. The primary key is a composite
 * key represented by the {@link CollectionContainsNotesId} class.
 *
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
    public CollectionContainsNotes() {}

    /**
     * Constructor to create a CollectionContainsNotes entity with the specified
     * collectionId and noteId. This constructor sets the composite primary key
     * for this entity.
     *
     * @param collectionId The ID of the Collection.
     * @param noteId The ID of the Note.
     */
    public CollectionContainsNotes(Long collectionId, Long noteId) {
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
    public Long getCollectionId() {
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
     * Checks the equality of this entity with another object. Two instances
     * are considered equal if their composite primary keys are equal.
     *
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionContainsNotes that = (CollectionContainsNotes) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Computes the hash code for this entity
     * based on its composite primary key.
     *
     * @return The hash code of this entity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
