/*
The tests were created with the aid of ChatGPT to speed up the process
 */

import commons.CollectionContainsNotesId;
import commons.CollectionContainsNotes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionContainsNotesTest {

    private CollectionContainsNotes entity1;
    private CollectionContainsNotes entity2;
    private CollectionContainsNotes entity3;

    @BeforeEach
    void setUp() {
        entity1 = new CollectionContainsNotes("collection1", 123L);
        entity2 = new CollectionContainsNotes("collection1", 123L);
        entity3 = new CollectionContainsNotes("collection2", 456L);
    }

    @Test
    void testDefaultConstructor() {
        CollectionContainsNotes entity = new CollectionContainsNotes();

        assertNull(entity.getId(), "Default constructor should initialize id to null");
    }

    @Test
    void testGetId_NotNull() {
        CollectionContainsNotesId id = entity1.getId();

        assertNotNull(id, "getId should return a non-null id");
    }

    @Test
    void testGetId_CorrectCollectionId() {
        assertEquals("collection1", entity1.getCollectionId(), "getId should return the correct collectionId");
    }

    @Test
    void testGetId_CorrectNoteId() {
        assertEquals(123L, entity1.getNoteId(), "getId should return the correct noteId");
    }

    @Test
    void testEquals_WithSameValues() {
        assertEquals(entity1, entity2, "Entities with the same values should be equal");
    }

    @Test
    void testEquals_WithDifferentValues() {
        assertNotEquals(entity1, entity3, "Entities with different values should not be equal");
    }

    @Test
    void testEquals_WithDifferentClass() {
        assertNotEquals(entity1, new Object(), "Entity should not be equal to objects of different classes");
    }

    @Test
    void testEquals_WithNull() {
        assertNotEquals(entity1, null, "Entity should not be equal to null");
    }

    @Test
    void testHashCode_WithSameValues() {
        assertEquals(entity1.hashCode(), entity2.hashCode(), "Hash codes of equal entities should match");
    }

    @Test
    void testHashCode_WithDifferentValues() {
        assertNotEquals(entity1.hashCode(), entity3.hashCode(), "Hash codes of unequal entities should not match");
    }

    @Test
    void testToString() {
        String stringRepresentation = entity1.toString();

        assertNotNull(stringRepresentation, "toString should return a non-null string");
        assertTrue(stringRepresentation.contains("collection1"), "toString should include the collectionId");
        assertTrue(stringRepresentation.contains("123"), "toString should include the noteId");
    }
}
