package server.api;

import commons.Collection;
import commons.CollectionContainsNotes;
import commons.Note;
import org.h2.tools.Server;
import org.junit.jupiter.api.*;
import server.utils.ServerUtils;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionControllerTest {

    private TestCollectionRepository collectionDb;
    private TestCollectionContainsNotesRepository containsNotesDb;
    private TestNoteRepository noteDb;
    private CollectionController sut;
    private Collection collection1, collection2;
    private ServerUtils serverUtils;
    private Note n1, n2, n3;

    @BeforeEach
    public void setUp() {
        collectionDb = new TestCollectionRepository();
        containsNotesDb = new TestCollectionContainsNotesRepository();
        noteDb = new TestNoteRepository();

        collection1 = new Collection("Collection1", "Alias1", "Server1");
        collection2 = new Collection("Collection2", "Alias2", "Server2");

        n1 = new Note("Title1", "Text1");
        n2 = new Note("Title2", "Text2");
        n3 = new Note("Title3", "Text3");

        collectionDb.save(collection1);
        collectionDb.save(collection2);

        noteDb.save(n1);
        noteDb.save(n2);
        noteDb.save(n3);

        n1.setNoteId(0L);
        n2.setNoteId(1L);
        n3.setNoteId(2L);

        containsNotesDb.save(new CollectionContainsNotes("Collection1", 0L));
        containsNotesDb.save(new CollectionContainsNotes("Collection1", 1L));
        containsNotesDb.save(new CollectionContainsNotes("Collection2", 2L));

        serverUtils = new ServerUtils(noteDb, null);
        sut = new CollectionController(collectionDb, containsNotesDb, serverUtils, noteDb);
    }

    @Test
    public void createCollection() {
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
        assertEquals(2, response.size());
        assertTrue(response.contains(collection1));
        assertTrue(response.contains(collection2));
    }

    @Test
    public void getCollectionByUniqueName() {
        var response = sut.getCollectionByUniqueName("Collection1");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(collection1, response.getBody());
    }

    @Test
    public void getCollectionByUniqueNameInvalid() {
        var response = sut.getCollectionByUniqueName("invalid");
        assertEquals(404, response.getStatusCode().value());
        assertEquals(null, response.getBody());
    }

    @Test
    public void updateCollectionAlias() {
        Collection collection = new Collection("Collection", "Alias", null);
        var response = sut.updateCollectionAlias("Collection1", collection);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Alias", collectionDb.findById("Collection1").get().getAlias());
    }

    @Test
    public void updateCollectionAliasInvalid() {
        Collection collection = new Collection("Collection", "Alias", null);
        var response = sut.updateCollectionAlias("Collection", collection);
        assertEquals(404, response.getStatusCode().value());
        assertTrue(collectionDb.findById("Collection").isEmpty());
    }

    @Test
    public void deleteCollection() {
        var response = sut.deleteCollection("Collection1");
        assertEquals(200, response.getStatusCode().value());
        assertFalse(collectionDb.existsById("Collection1"));
    }

    @Test
    public void deleteCollectionInvalid() {
        var response = sut.deleteCollection("InvalidCollection");
        assertEquals(404, response.getStatusCode().value());
        assertEquals(2, collectionDb.findAll().size());
    }

    @Test
    public void getAllNotesInCollection() {
        var response = sut.getAllNotesInCollection("Collection1");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(note -> note.getTitle().equals("Title1")));
        assertTrue(response.getBody().stream().anyMatch(note -> note.getTitle().equals("Title2")));
    }

    @Test
    public void getAllNotesInCollectionInvalid() {
        var response = sut.getAllNotesInCollection("ThisCollectionDoesNotExist");
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    public void addNoteToCollection() {
        var response = sut.addNoteToCollection("Collection1", "Title3");
        assertEquals(200, response.getStatusCode().value());
        assertTrue(containsNotesDb.findAll().stream()
                .anyMatch(x -> x.getCollectionId().equals("Collection1") &&
                        serverUtils.getNoteByTitle("Title3").isPresent()));
    }

    @Test
    public void addNoteToCollectionInvalid() {
        var response = sut.addNoteToCollection("ThisCollectionDoesNotExist", "Title3");
        assertEquals(404, response.getStatusCode().value());
        assertTrue(containsNotesDb.findAll().stream()
                .noneMatch(x -> serverUtils.getNoteByTitle("Title3").isPresent() &&
                        x.getCollectionId().equals("ThisCollectionDoesNotExist")));
    }

    @Test
    public void removeNoteFromCollection() {
        n1.setNoteId(0L);
        containsNotesDb.save(new CollectionContainsNotes("Collection1", n1.getNoteId()));
        var response = sut.removeNoteFromCollection("Collection1", "Title1");
        assertEquals(200, response.getStatusCode().value());
        assertFalse(containsNotesDb.findAll().stream()
                .anyMatch(x -> x.getCollectionId().equals("Collection1") &&
                        x.getNoteId().equals(n1.getNoteId())));
    }

    @Test
    public void removeNoteFromCollectionInvalid() {
        var response = sut.removeNoteFromCollection("InvalidCollection", "Title1");
        assertEquals(404, response.getStatusCode().value());
    }



}
