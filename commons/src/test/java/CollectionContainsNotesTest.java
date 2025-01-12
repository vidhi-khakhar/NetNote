
/*
The tests were created with the aid of ChatGPT to speed up the process
 */

import commons.CollectionContainsNotes;
import commons.CollectionContainsNotesId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionContainsNotesTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        CollectionContainsNotes entity = new CollectionContainsNotes();

        // Assert
        assertNull(entity.getId(), "Default constructor should initialize id to null");
    }

    @Test
    void testGetId() {
        // Arrange
        String collectionId = "collection1";
        Long noteId = 123L;
        CollectionContainsNotes entity = new CollectionContainsNotes(collectionId, noteId);

        // Act
        CollectionContainsNotesId id = entity.getId();

        // Assert
        assertNotNull(id, "getId should return a non-null id");
        assertEquals(collectionId, id.getCollectionId(), "getId should return the correct collectionId");
        assertEquals(noteId, id.getNoteId(), "getId should return the correct noteId");
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        CollectionContainsNotes entity1 = new CollectionContainsNotes("collection1", 123L);
        CollectionContainsNotes entity2 = new CollectionContainsNotes("collection1", 123L);
        CollectionContainsNotes entity3 = new CollectionContainsNotes("collection2", 456L);

        // Act & Assert
        assertEquals(entity1, entity2, "Entities with the same values should be equal");
        assertEquals(entity1.hashCode(), entity2.hashCode(), "Hash codes of equal entities should match");

        assertNotEquals(entity1, entity3, "Entities with different values should not be equal");
        assertNotEquals(entity1.hashCode(), entity3.hashCode(), "Hash codes of unequal entities should not match");
    }

    @Test
    void testNotEqualsToOtherObjects() {
        // Arrange
        CollectionContainsNotes entity = new CollectionContainsNotes("collection1", 123L);

        // Act & Assert
        assertNotEquals(entity, null, "Entity should not be equal to null");
        assertNotEquals(entity, new Object(), "Entity should not be equal to objects of other types");
    }
}
