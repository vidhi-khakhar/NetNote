import commons.CollectionContainsNotesId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionContainsNotesIdTest {

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        CollectionContainsNotesId id1 = new CollectionContainsNotesId("collection1", 1L);
        CollectionContainsNotesId id2 = new CollectionContainsNotesId("collection1", 1L);
        CollectionContainsNotesId id3 = new CollectionContainsNotesId("collection2", 2L);

        // Act & Assert
        assertEquals(id1, id2, "Objects with the same values should be equal");
        assertEquals(id1.hashCode(), id2.hashCode(), "Hash codes should match for equal objects");
        assertNotEquals(id1, id3, "Objects with different values should not be equal");
        assertNotEquals(id1.hashCode(), id3.hashCode(), "Hash codes should not match for unequal objects");
    }

    @Test
    void testGetters() {
        // Arrange
        String expectedCollectionId = "collection1";
        Long expectedNoteId = 1L;
        CollectionContainsNotesId id = new CollectionContainsNotesId(expectedCollectionId, expectedNoteId);

        // Act & Assert
        assertEquals(expectedCollectionId, id.getCollectionId(), "getCollectionId should return the correct value");
        assertEquals(expectedNoteId, id.getNoteId(), "getNoteId should return the correct value");
    }

    @Test
    void testNotEqualsToOtherObjectTypes() {
        // Arrange
        CollectionContainsNotesId id = new CollectionContainsNotesId("collection1", 1L);

        // Act & Assert
        assertNotEquals(id, new Object(), "CollectionContainsNotesId should not be equal to objects of other types");
        assertNotEquals(id, null, "CollectionContainsNotesId should not be equal to null");
    }
}
