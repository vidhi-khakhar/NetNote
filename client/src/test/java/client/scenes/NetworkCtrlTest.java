package client.scenes;

import client.utils.collection.MockCollectionServerUtils;
import client.utils.file.MockEmbeddedFileServerUtils;
import client.utils.network.NetworkMenuSelectionType;
import client.utils.note.MockNoteServerUtils;
import commons.Collection;
import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkCtrlTest {

    private static final MockNoteServerUtils mockNoteServerUtils = new MockNoteServerUtils();
    private static final MockCollectionServerUtils mockCollectionServerUtils = new MockCollectionServerUtils();
    private static final MockEmbeddedFileServerUtils mockFileServerUtils = new MockEmbeddedFileServerUtils();
    private static String a1, a2;
    private static Note n1, n2, n3;
    private static Collection c1, c2, c3;
    private static EmbeddedFile f1, f2, f3;
    private NetworkCtrl sut = new NetworkCtrl();

    @BeforeAll
    public static void setUp() {
        a1 = "http://192.168.0.123/";
        a2 = "http://192.168.0.124/";

        setUpNotes();
        setUpCollections();
        setUpFiles();
    }

    public static void setUpNotes() {
        n1 = new Note("Note 1", "Some text");
        n2 = new Note("Note 2", "Some more text");
        n3 = new Note("Note 3", "Some more text once more");

        mockNoteServerUtils.setServerAddress(a1);
        mockNoteServerUtils.storeNote(n1);
        mockNoteServerUtils.setServerAddress(a2);
        mockNoteServerUtils.storeNote(n2);
        mockNoteServerUtils.storeNote(n3);
    }

    public static void setUpCollections() {
        c1 = new Collection("col-1", "Collection 1", a1);
        c2 = new Collection("col-2", "Collection 2", a2);
        c3 = new Collection("my-notes", "Collection 1", a2);

        mockCollectionServerUtils.createCollection(c1, a1);
        mockCollectionServerUtils.createCollection(c2, a2);
        mockCollectionServerUtils.createCollection(c3, a2);

        mockCollectionServerUtils.addNoteToCollection("col-1", "Note 1", a1);
        mockCollectionServerUtils.addNoteToCollection("col-2", "Note 2", a2);
        mockCollectionServerUtils.addNoteToCollection("my-notes", "Note 3", a2);
    }

    public static void setUpFiles() {
        f1 = new EmbeddedFile("image.jpg", "image/jpg", new byte[0], n1);
        f2 = new EmbeddedFile("image.png", "image/png", new byte[0], n1);
        f3 = new EmbeddedFile("lecture_notes.md", "text/markdown", new byte[0], n2);

        mockFileServerUtils.storeEmbeddedFile(a1, "Note 1", f1);
        mockFileServerUtils.storeEmbeddedFile(a1, "Note 1", f2);
        mockFileServerUtils.storeEmbeddedFile(a2, "Note 2", f3);
    }

    @Test
    public void getNewResultsWithTypeNotesTest() {
        assertEquals(List.of("Note 2", "Note 3"),
                sut.getNewResults(a2,
                        NetworkMenuSelectionType.NOTES,
                        mockNoteServerUtils,
                        mockCollectionServerUtils,
                        mockFileServerUtils
                        ));
    }

    @Test
    public void getNewResultsWithTypeCollectionsTest() {
        System.out.println((mockCollectionServerUtils.getAllCollections(a1)));
        assertEquals(List.of("col-1"),
                sut.getNewResults(a1,
                        NetworkMenuSelectionType.COLLECTIONS,
                        mockNoteServerUtils,
                        mockCollectionServerUtils,
                        mockFileServerUtils
                ));
    }

    @Test
    public void getNewResultsWithTypeFilesTest() {
        assertEquals(List.of("image.jpg", "image.png"),
                sut.getNewResults(a1,
                        NetworkMenuSelectionType.FILES,
                        mockNoteServerUtils,
                        mockCollectionServerUtils,
                        mockFileServerUtils
                ));
    }

    @Test
    public void getNewNoteResultsTest() {
        assertEquals(List.of("Note 1"),
                sut.getNewNoteResults(a1, mockNoteServerUtils));
    }

    @Test
    public void getNewCollectionResultsTest() {
        assertEquals(List.of("col-2", "my-notes"),
                sut.getNewCollectionResults(a2, mockCollectionServerUtils));
    }

    @Test
    public void getNewFileResultsTest() {
        assertEquals(List.of("lecture_notes.md"),
                sut.getNewFileResults(a2, mockNoteServerUtils, mockFileServerUtils));
    }
}
