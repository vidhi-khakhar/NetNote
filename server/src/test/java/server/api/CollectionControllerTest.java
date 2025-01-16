package server.api;

import commons.Collection;
import org.junit.jupiter.api.*;
import server.utils.ServerUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionControllerTest {

    private TestCollectionRepository collectionDb;
    private TestCollectionContainsNotesRepository containsNotesDb;
    private TestNoteRepository noteDb;
    private CollectionController sut;
    private Collection collection1, collection2;

    @BeforeEach
    public void setUp() {
        collectionDb = new TestCollectionRepository();
        containsNotesDb = new TestCollectionContainsNotesRepository();
        noteDb = new TestNoteRepository();

        collection1 = new Collection("Collection1", "Alias1", "Server1");
        collection2 = new Collection("Collection2", "Alias2", "Server2");

        collectionDb.save(collection1);
        collectionDb.save(collection2);

        ServerUtils serverUtils = new ServerUtils(noteDb, null);
        sut = new CollectionController(collectionDb, containsNotesDb, serverUtils, noteDb);
    }

    @Test
    public void createCollectionValid() {
        Collection collection = new Collection("Collection", "Alias", "Server");
        var response = sut.createCollection(collection);
        assertEquals(201, response.getStatusCode().value());
        assertTrue(collectionDb.existsById(collection.getCollectionUniqueName()));
        assertEquals(collection, collectionDb.findById("Collection").get());
    }

    @Test
    public void createCollectionInvalidAlias() {
        Collection collection = new Collection("Collection", "Al", "Server");
        var response = sut.createCollection(collection);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(collectionDb.findAll().stream().noneMatch(c -> c.getCollectionUniqueName().equals("Collection")));
    }

    @Test
    public void createCollectionDuplicate() {
        Collection collection = new Collection("Collection1", "Alias1", "Server1");
        var response = sut.createCollection(collection);
        assertEquals(409, response.getStatusCode().value());
        assertEquals(2, collectionDb.findAll().size());
        assertTrue(collectionDb.findById("Collection1").isPresent());
    }

    @Test
    public void createCollectionInvalidServer() {
        Collection collection = new Collection("Collection", "Alias", null);
        var response = sut.createCollection(collection);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(collectionDb.findAll().stream().noneMatch(c -> c.getCollectionUniqueName().equals("Collection")));
    }

    @Test
    public void getAllCollections() {
        var response = sut.getAllCollections();
        assertEquals(2, collectionDb.findAll().size());
        assertTrue(response.contains(collection1));
        assertTrue(response.contains(collection2));
    }


}
