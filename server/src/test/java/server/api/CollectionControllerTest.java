package server.api;

import commons.Collection;
import org.junit.jupiter.api.*;
import server.utils.ServerUtils;

public class CollectionControllerTest {

    private TestCollectionRepository collectionRepository;
    private TestCollectionContainsNotesRepository containsNotesRepository;
    private TestNoteRepository noteRepository;
    private CollectionController sut;
    private Collection collection1, collection2;

    @BeforeEach
    public void setUp() {
        collectionRepository = new TestCollectionRepository();
        containsNotesRepository = new TestCollectionContainsNotesRepository();
        noteRepository = new TestNoteRepository();

        collection1 = new Collection("Collection1", "Alias1", "Server1");
        collection2 = new Collection("Collection2", "Alias2", "Server2");

        collectionRepository.save(collection1);
        collectionRepository.save(collection2);

        ServerUtils serverUtils = new ServerUtils(noteRepository, null);
        sut = new CollectionController(collectionRepository, containsNotesRepository, serverUtils, noteRepository);
    }


}
