package client.utils.collection;

import commons.Collection;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionUtilsTest {

    private final CollectionUtils sut = new CollectionUtils();
    private static final String MOCK_SERVER_ADDRESS = "https://192.168.0.1:8080/";
    private int currentIndex;
    private Collection col1, col2;
    private List<Collection> collections = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        col1 = new Collection("notes-1", MOCK_SERVER_ADDRESS, "My first Collection");
        col2 = new Collection("notes-2", MOCK_SERVER_ADDRESS, "CO Lecture Notes");
        collections = new ArrayList<>();
        collections.add(col1);
        collections.add(col2);
        currentIndex = 1;
    }

    @Test
    public void addCollectionWithGivenAliasChangesIndexTest() {
        int newIndex = sut.createNewCollection(collections, "Dutch weather notes");
        assertEquals(2, newIndex); // Assert whether the newly created collection is selected
    }

    @Test
    public void addCollectionWithGivenAliasUpdatesListTest() {
        sut.createNewCollection(collections, "Dutch weather notes");
        assertEquals(List.of(col1, col2, new Collection("", "Dutch weather notes", "")),
                collections);
    }

    @Test
    public void addCollectionWithDefaultAliasTest() {
        sut.createNewCollection(collections);
        assertEquals(List.of(col1, col2, new Collection("",
                CollectionUtils.NEW_COLLECTION_DEFAULT_ALIAS,
                "")), collections);
    }

    @Test
    public void someDefaultAliasesAlreadyTakenTest() {
        Collection newCol1 = new Collection("notes-10",
                CollectionUtils.NEW_COLLECTION_DEFAULT_ALIAS,
                MOCK_SERVER_ADDRESS);
        Collection newCol2 = new Collection("notes-11",
                CollectionUtils.NEW_COLLECTION_DEFAULT_ALIAS + " (2)",
                MOCK_SERVER_ADDRESS);
        collections.add(newCol1);
        collections.add(newCol2);
        sut.createNewCollection(collections);
        assertEquals(List.of(col1, col2, newCol1, newCol2, new Collection("",
                CollectionUtils.NEW_COLLECTION_DEFAULT_ALIAS + " (3)",
                "")), collections);
    }

    @Test
    public void removeCollectionInvalidIndexTest() {
        int newIndex = sut.removeCollection(collections, currentIndex, -1);
        assertEquals(1, newIndex);
        assertEquals(List.of(col1, col2), collections);
    }

    @Test
    public void removeLastCollectionTest() {
        int newIndex = sut.removeCollection(collections, currentIndex, 1);
        assertEquals(0, newIndex);
        assertEquals(List.of(col1), collections);
    }

    @Test
    public void removeFirstCollectionTest() {
        int newIndex = sut.removeCollection(collections, currentIndex, 0);
        assertEquals(0, newIndex);
        assertEquals(List.of(col2), collections);
    }

    @Test
    public void removeCollectionAboveWithMoreCollectionsTest() {
        Collection newCol1 = new Collection("notes-10",
                CollectionUtils.NEW_COLLECTION_DEFAULT_ALIAS,
                MOCK_SERVER_ADDRESS);
        Collection newCol2 = new Collection("notes-11",
                CollectionUtils.NEW_COLLECTION_DEFAULT_ALIAS + " (2)",
                MOCK_SERVER_ADDRESS);
        collections.add(newCol1);
        collections.add(newCol2);
        int newIndex = sut.removeCollection(collections, currentIndex, 0);
        assertEquals(0, newIndex);
        assertEquals(List.of(col2, newCol1, newCol2), collections);
    }

    @Test
    public void collectionDoesAlreadyExistTest() {
        assertTrue(sut.collectionAlreadyExists(collections, "notes-1"));
    }

    @Test
    public void collectionDoesNotAlreadyExistTest() {
        assertFalse(sut.collectionAlreadyExists(collections, "my-new-notes"));
    }
}
