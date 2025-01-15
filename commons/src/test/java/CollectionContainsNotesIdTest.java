/*
The tests were created with the aid of ChatGPT to speed up the process
 */

import commons.CollectionContainsNotesId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionContainsNotesIdTest {

    private CollectionContainsNotesId id1;
    private CollectionContainsNotesId id2;
    private CollectionContainsNotesId id3;

    @BeforeEach
    void setUp() {
        id1 = new CollectionContainsNotesId("collection1", 1L);
        id2 = new CollectionContainsNotesId("collection1", 1L);
        id3 = new CollectionContainsNotesId("collection2", 2L);
    }

    @Test
    void testEqualsAndHashCode_WithSameValues() {
        assertEquals(id1, id2, "Objects with the same values should be equal");
    }

    @Test
    void testEqualsAndHashCode_WithDifferentValues() {
        assertNotEquals(id1, id3, "Objects with different values should not be equal");
    }

    @Test
    void testHashCode_WithSameValues() {
        assertEquals(id1.hashCode(), id2.hashCode(), "Hash codes should match for equal objects");
    }

    @Test
    void testHashCode_WithDifferentValues() {
        assertNotEquals(id1.hashCode(), id3.hashCode(), "Hash codes should not match for unequal objects");
    }

    @Test
    void testGetCollectionId() {
        assertEquals("collection1", id1.getCollectionId(), "getCollectionId should return the correct value");
    }

    @Test
    void testGetNoteId() {
        assertEquals(1L, id1.getNoteId(), "getNoteId should return the correct value");
    }

    @Test
    void testToString() {
        String expectedToString = "CollectionContainsNotesId[collectionId=collection1, noteId=1]";
        assertTrue(id1.toString().contains("collection1"), "toString should contain collectionId value");
        assertTrue(id1.toString().contains("1"), "toString should contain noteId value");
    }

    @Test
    void testNotEqualsToOtherObjectType() {
        assertNotEquals(id1, new Object(), "CollectionContainsNotesId should not be equal to objects of other types");
    }

    @Test
    void testNotEqualsToNull() {
        assertNotEquals(id1, null, "CollectionContainsNotesId should not be equal to null");
    }

    @Test
    void testDefaultConstructor() {
        CollectionContainsNotesId id = new CollectionContainsNotesId();
        assertNull(id.getCollectionId(), "Default constructor should initialize collectionId to null");
        assertNull(id.getNoteId(), "Default constructor should initialize noteId to null");
    }
}
