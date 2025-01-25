package client.utils.collection;

import client.utils.ServerUtils;
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
    private ServerUtils serverUtils;
    private Collection testCollection;
    private Collection duplicateCollection;
    private Note removableNote;
    public static String SERVER_ADDRESS = ""; //  there isn't a mock server to be used when testing
    private String invalidServerAddress = "http://invalid-address";
    private String nonExistentCollection = "non-existent-collection";
    private String newAlias = "New Alias";


    @BeforeEach
    void setup() {
        collectionServerUtils = new CollectionServerUtils(new ObjectMapper());
        serverUtils = new ServerUtils();

        assumeTrue(serverUtils.isServerAvailable(SERVER_ADDRESS), "Server is not available, skipping tests.");

        testCollection = new Collection("test-collection", "Test Collection Alias", SERVER_ADDRESS);
        collectionServerUtils.createCollection(testCollection, SERVER_ADDRESS);
        Collection duplicateCollection = new Collection("test-collection", "Duplicate Alias", SERVER_ADDRESS);
        collectionServerUtils.createCollection(duplicateCollection, SERVER_ADDRESS);
        Note removableNote = new Note("Note1", "Text1");
    }

    @Test
    void fetchAllCollectionsTest() {
        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        assertNotNull(collections, "Collections list should not be null.");

        boolean isTestCollectionPresent = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                                        collection.getAlias().equals(testCollection.getAlias()));

        assertTrue(isTestCollectionPresent, "Test collection should be present in the list of collections.");
    }

    @Test
    void updateCollectionAliasTest() {
        String newAlias = "Updated Alias";
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), newAlias, SERVER_ADDRESS);

        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean isAliasUpdated = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                                        collection.getAlias().equals(newAlias));
        assertTrue(isAliasUpdated, "Test collection alias should be updated.");
    }

    @Test
    void deleteCollectionTest() {
        collectionServerUtils.deleteCollection(testCollection.getCollectionUniqueName(), SERVER_ADDRESS);

        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean isCollectionDeleted = collections.stream()
                .noneMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));
        assertTrue(isCollectionDeleted, "Test collection should be deleted.");
    }

    @Test
    void aliasConsistencyAfterUpdatesTest() {
        String alias1 = "UpdatedAlias1";
        String alias2 = "UpdatedAlias2";
        String alias3 = "UpdatedAlias3";

        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), alias1, SERVER_ADDRESS);
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), alias2, SERVER_ADDRESS);
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), alias3, SERVER_ADDRESS);

        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean finalAliasCorrect = collections.stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                               c.getAlias().equals(alias3));

        assertTrue(finalAliasCorrect, "Alias should be updated to the final value.");
    }
    @Test
    void deleteAllCollectionsTest() {
        Collection collection1 = new Collection("deleted1", "deletedAlias1", SERVER_ADDRESS);
        Collection collection2 = new Collection("deleted2", "deletedAlias2", SERVER_ADDRESS);

        collectionServerUtils.createCollection(collection1, SERVER_ADDRESS);
        collectionServerUtils.createCollection(collection2, SERVER_ADDRESS);

        collectionServerUtils.deleteCollection(collection1.getCollectionUniqueName(), SERVER_ADDRESS);
        collectionServerUtils.deleteCollection(collection2.getCollectionUniqueName(), SERVER_ADDRESS);

        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean allDeleted = collections.stream()
                .noneMatch(c -> c.getCollectionUniqueName().equals("deleted1") || c.getCollectionUniqueName().equals("deleted2"));

        assertTrue(allDeleted, "All collections should be deleted successfully.");
    }
    @Test
    void checkIfCollectionExistsTest() {
        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean collectionExists = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));

        assertTrue(collectionExists, "The test collection should exist.");
    }

    @Test
    void createDuplicateCollectionTest() {
        Collection duplicate = new Collection("test-collection", "Duplicate Alias", SERVER_ADDRESS);
        collectionServerUtils.createCollection(duplicate, SERVER_ADDRESS);

        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        long count = collections.stream()
                .filter(c -> c.getCollectionUniqueName().equals("test-collection"))
                .count();

        assertEquals(1, count, "Duplicate collections with the same unique name should not exist.");
    }

    @Test
    void updateAliasForNonExistentCollectionTest() {

        collectionServerUtils.updateCollectionAlias(nonExistentCollection, newAlias, SERVER_ADDRESS);
        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);

        boolean aliasUpdated = collections.stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(nonExistentCollection) && c.getAlias().equals(newAlias));

        assertFalse(aliasUpdated, "Alias for a non-existent collection should not be updated.");
    }

    @Test
    void deleteNonExistentCollectionTest() {
        collectionServerUtils.deleteCollection(nonExistentCollection, SERVER_ADDRESS);

        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean collectionExists = collections.stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(nonExistentCollection));

        assertFalse(collectionExists, "Non-existent collection should not be present after delete attempt.");
    }

    @Test
    void invalidServerAddressTest() {
        List<Collection> collections = collectionServerUtils.getAllCollections(invalidServerAddress);

        assertTrue(collections.isEmpty(), "Fetching collections from an invalid server should return an empty list.");
    }

    @AfterEach
    void cleanup() {
        List<Collection> collections = collectionServerUtils.getAllCollections(SERVER_ADDRESS);
        boolean exists = collections.stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));
        if (exists) {
            collectionServerUtils.deleteCollection(testCollection.getCollectionUniqueName(), SERVER_ADDRESS);
        }
    }
}

