package client.utils.collection;

import client.utils.note.NoteServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;
import commons.Note;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;


public class CollectionServerUtilsTest {

    private CollectionServerUtils collectionServerUtils;
    private NoteServerUtils noteServerUtils;
    private Collection testCollection;
    private Collection duplicateCollection;
    private Note removableNote;

    @BeforeEach
    void setup() {
        collectionServerUtils = new CollectionServerUtils(new ObjectMapper());
        noteServerUtils = new NoteServerUtils();

        assumeTrue(noteServerUtils.isServerAvailable("http://localhost:8080/"), "Server is not available, skipping tests.");
        testCollection = new Collection("test-collection", "Test Collection Alias", "http://localhost:8080/");
        collectionServerUtils.createCollection(testCollection, "http://localhost:8080/");
        Collection duplicateCollection = new Collection("test-collection", "Duplicate Alias", "http://localhost:8080/");
        collectionServerUtils.createCollection(duplicateCollection, "http://localhost:8080/");
        Note removableNote = new Note("Note1", "Text1");
    }

    @Test
    void fetchAllCollectionsTest() {
        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        assertNotNull(collections, "Collections list should not be null.");

        boolean isTestCollectionPresent = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                                        collection.getAlias().equals(testCollection.getAlias()));

        assertTrue(isTestCollectionPresent, "Test collection should be present in the list of collections.");
    }

    @Test
    void updateCollectionAliasTest() {
        String newAlias = "Updated Alias";
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), newAlias, "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean isAliasUpdated = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                                        collection.getAlias().equals(newAlias));
        assertTrue(isAliasUpdated, "Test collection alias should be updated.");
    }

    @Test
    void deleteCollectionTest() {
        collectionServerUtils.deleteCollection(testCollection.getCollectionUniqueName(), "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean isCollectionDeleted = collections.stream()
                .noneMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));
        assertTrue(isCollectionDeleted, "Test collection should be deleted.");
    }

    @Test
    void aliasConsistencyAfterUpdatesTest() {
        String alias1 = "UpdatedAlias1";
        String alias2 = "UpdatedAlias2";
        String alias3 = "UpdatedAlias3";

        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), alias1, "http://localhost:8080/");
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), alias2, "http://localhost:8080/");
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), alias3, "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean finalAliasCorrect = collections.stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                               c.getAlias().equals(alias3));

        assertTrue(finalAliasCorrect, "Alias should be updated to the final value.");
    }
    @Test
    void deleteAllCollectionsTest() {
        Collection collection1 = new Collection("deleted1", "deletedAlias1", "http://localhost:8080/");
        Collection collection2 = new Collection("deleted2", "deletedAlias2", "http://localhost:8080/");

        collectionServerUtils.createCollection(collection1, "http://localhost:8080/");
        collectionServerUtils.createCollection(collection2, "http://localhost:8080/");

        collectionServerUtils.deleteCollection(collection1.getCollectionUniqueName(), "http://localhost:8080/");
        collectionServerUtils.deleteCollection(collection2.getCollectionUniqueName(), "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean allDeleted = collections.stream()
                .noneMatch(c -> c.getCollectionUniqueName().equals("deleted1") || c.getCollectionUniqueName().equals("deleted2"));

        assertTrue(allDeleted, "All collections should be deleted successfully.");
    }
    @Test
    void checkIfCollectionExistsTest() {
        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean collectionExists = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));

        assertTrue(collectionExists, "The test collection should exist.");
    }

    @AfterEach
    void cleanup() {
        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean exists = collections.stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));
        if (exists) {
            collectionServerUtils.deleteCollection(testCollection.getCollectionUniqueName(), "http://localhost:8080/");
        }
    }
}

