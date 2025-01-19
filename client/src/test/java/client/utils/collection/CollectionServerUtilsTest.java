package client.utils.collection;

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
    private Collection testCollection;

    @BeforeEach
    void setup() {
        collectionServerUtils = new CollectionServerUtils(new ObjectMapper());

        testCollection = new Collection("test-collection", "Test Collection Alias", "http://localhost:8080/");
        collectionServerUtils.createCollection(testCollection, "http://localhost:8080/");
    }

    @Test
    void fetchAllCollectionsTest() {
        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");

        boolean isTestCollectionPresent = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                                        collection.getAlias().equals(testCollection.getAlias()));
        assertTrue(isTestCollectionPresent, "Test collection should be present in the list of collections.");
    }

    @Test
    void CreateCollectionTest() {
        Collection newCollection = new Collection("new-collection", "New Collection Alias", "http://localhost:8080/");
        collectionServerUtils.createCollection(newCollection, "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean isNewCollectionPresent = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(newCollection.getCollectionUniqueName()));
        assertTrue(isNewCollectionPresent, "New collection should be present in the list of collections.");
    }

    @Test
    void UpdateCollectionAliasTest() {
        String newAlias = "Updated Alias";
        collectionServerUtils.updateCollectionAlias(testCollection.getCollectionUniqueName(), newAlias, "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean isAliasUpdated = collections.stream()
                .anyMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()) &&
                                        collection.getAlias().equals(newAlias));
        assertTrue(isAliasUpdated, "Test collection alias should be updated.");
    }

    @Test
    void DeleteCollectionTest() {
        collectionServerUtils.deleteCollection(testCollection.getCollectionUniqueName(), "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean isCollectionDeleted = collections.stream()
                .noneMatch(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()));
        assertTrue(isCollectionDeleted, "Test collection should be deleted.");
    }

    @Test
    void HandleDuplicateCollectionCreationTest() {
        Collection duplicateCollection = new Collection("test-collection", "Duplicate Alias", "http://localhost:8080/");
        collectionServerUtils.createCollection(duplicateCollection, "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        long count = collections.stream()
                .filter(collection -> collection.getCollectionUniqueName().equals(testCollection.getCollectionUniqueName()))
                .count();
        assertEquals(1, count, "Duplicate collections should not be allowed.");
    }
    @Test
    void RemoveNoteFromCollectionTest() {
        Note removableNote = new Note("Note1", "Text1");

        collectionServerUtils.addNoteToCollection(testCollection.getCollectionUniqueName(), removableNote.getTitle(), "http://localhost:8080/");
        collectionServerUtils.removeNoteFromCollection(testCollection.getCollectionUniqueName(), removableNote.getTitle(), "http://localhost:8080/");

        List<Note> notes = collectionServerUtils.getAllNotesInCollection(testCollection.getCollectionUniqueName(), "http://localhost:8080/");

        boolean isNoteRemoved = notes.stream()
                .noneMatch(note -> note.getTitle().equals(removableNote.getTitle()));

        assertTrue(isNoteRemoved, "Removable note should be removed from the collection.");
    }
    @Test
    void canAddMultipleCollectionsWithDifferentNamesTest() {
        Collection collection1 = new Collection("collection1", "Alias1", "http://localhost:8080/");
        Collection collection2 = new Collection("collection2", "Alias2", "http://localhost:8080/");
        Collection collection3 = new Collection("collection3", "Alias3", "http://localhost:8080/");

        collectionServerUtils.createCollection(collection1, "http://localhost:8080/");
        collectionServerUtils.createCollection(collection2, "http://localhost:8080/");
        collectionServerUtils.createCollection(collection3, "http://localhost:8080/");

        List<Collection> collections = collectionServerUtils.getAllCollections("http://localhost:8080/");
        boolean allCollectionsAdded = collections.stream()
                                              .anyMatch(c -> c.getCollectionUniqueName().equals("collection1")) &&
                                      collections.stream().anyMatch(c -> c.getCollectionUniqueName().equals("collection2")) &&
                                      collections.stream().anyMatch(c -> c.getCollectionUniqueName().equals("collection3"));

        assertTrue(allCollectionsAdded, "All collections should be added successfully.");
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
        collectionServerUtils.deleteCollection(testCollection.getCollectionUniqueName(), "http://localhost:8080/");
    }
}

