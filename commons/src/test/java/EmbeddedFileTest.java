import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedFileTest {
    private EmbeddedFile ef;
    private String testImagePath;
    private String testTextPath;
    private File testImageFile;
    private File testTextFile;
    private byte[] testImageData;

    @BeforeEach
    void setUp() throws IOException {
        ef = new EmbeddedFile();
        testImagePath = "src/test/java/test.jpg";
        testTextPath = "src/test/java/test.txt";
        testImageFile = new File(testImagePath);
        testTextFile = new File(testTextPath);
        if (testImageFile.exists()) {
            testImageData = Files.readAllBytes(testImageFile.toPath());
        }
    }

    @Test
    void dataTest() throws IOException {
        EmbeddedFile ef = new EmbeddedFile();
        String path = "src/test/java/test.jpg";
        File file = new File(path);
        byte[] content = Files.readAllBytes(file.toPath());
        ef.setData(content);
        assertArrayEquals(ef.getData(), content);
    }

    @Test
    void nameTest() {
        EmbeddedFile ef = new EmbeddedFile();
        String path = "src/test/java/test.jpg";
        File file = new File(path);
        ef.setName(file.getName());
        assertEquals("test.jpg", ef.getName());
    }

    @Test
    void typeTest() throws IOException {
        EmbeddedFile ef = new EmbeddedFile();
        String path = "src/test/java/test.jpg";
        File file = new File(path);
        ef.setType(Files.probeContentType(file.toPath()));
        assertEquals("image/jpeg", ef.getType());
    }

    @Test
    void noteTest() {
        EmbeddedFile ef = new EmbeddedFile();
        Note note = new Note("title", "hello");
        Note note2 = new Note("title", "hello");
        ef.setNote(note);
        assertEquals(note2, ef.getNote());
    }

    @Test
    void equalsTestDifferentImages() throws IOException {
        EmbeddedFile ef1 = new EmbeddedFile();
        EmbeddedFile ef2 = new EmbeddedFile();
        String path1 = "src/test/java/test.jpg";
        String path2 = "src/test/java/download.jpg";
        File file1 = new File(path1);
        File file2 = new File(path2);
        ef1.setName(file1.getName());
        ef2.setName(file2.getName());
        byte[] content1 = Files.readAllBytes(file1.toPath());
        byte[] content2 = Files.readAllBytes(file2.toPath());
        ef1.setData(content1);
        ef2.setData(content2);
        ef1.setType(Files.probeContentType(file1.toPath()));
        ef2.setType(Files.probeContentType(file2.toPath()));
        assertNotEquals(ef1, ef2);
    }

    @Test
    void equalsTestSameImage() throws IOException {
        EmbeddedFile ef1 = new EmbeddedFile();
        EmbeddedFile ef2 = new EmbeddedFile();
        String path1 = "src/test/java/test.jpg";
        File file1 = new File(path1);
        ef1.setName(file1.getName());
        ef2.setName(file1.getName());
        byte[] content1 = Files.readAllBytes(file1.toPath());
        ef1.setData(content1);
        ef2.setData(content1);
        ef1.setType(Files.probeContentType(file1.toPath()));
        ef2.setType(Files.probeContentType(file1.toPath()));
        assertEquals(ef1, ef2);
    }

    @Test
    void hashCodeTestDifferentImages() throws IOException {
        String path1 = "src/test/java/test.jpg";
        String path2 = "src/test/java/download.jpg";
        File file1 = new File(path1);
        File file2 = new File(path2);
        byte[] content1 = Files.readAllBytes(file1.toPath());
        byte[] content2 = Files.readAllBytes(file2.toPath());
        EmbeddedFile ef = new EmbeddedFile("stuff",
                "image/jpeg", content1, new Note("title", "hello"));
        EmbeddedFile ef2 = new EmbeddedFile("stuff",
                "image/jpeg", content2, new Note("title", "hello"));
        assertNotEquals(ef.hashCode(), ef2.hashCode());
    }

    @Test
    void hashCodeTestSameImage() throws IOException {
        EmbeddedFile ef1 = new EmbeddedFile();
        EmbeddedFile ef2 = new EmbeddedFile();
        String path1 = "src/test/java/test.jpg";
        File file1 = new File(path1);
        byte[] content1 = Files.readAllBytes(file1.toPath());
        byte[] content2 = Files.readAllBytes(file1.toPath());
        ef1.setName(file1.getName());
        ef2.setName(file1.getName());
        ef1.setData(content1);
        ef2.setData(content2);
        ef1.setType(Files.probeContentType(file1.toPath()));
        ef2.setType(Files.probeContentType(file1.toPath()));
        assertEquals(ef1.hashCode(), ef2.hashCode());
    }

    @Test
    void getIdTest() {
        EmbeddedFile embeddedFile = new EmbeddedFile("name", "type", null, null);
        Long id = embeddedFile.getId();
        assertNull(id);
    }

    @Test
    void toStringTest() {
        EmbeddedFile embeddedFile = new EmbeddedFile("name", "type", null, null);
        String representation = embeddedFile.toString();
        System.out.println(representation);
        assertTrue(representation.contains("data=<null>") &&
                   representation.contains("name=name"));
    }


    @Test
    void nullDataTest() {
        ef.setData(null);
        assertNull(ef.getData());
    }

    @Test
    void emptyNameTest() {
        ef.setName("");
        assertEquals("", ef.getName());
    }

    @Test
    void emptyTypeTest() {
        ef.setType("");
        assertEquals("", ef.getType());
    }

    @Test
    void noteNotSetTest() {
        assertNull(ef.getNote());
    }

    @Test
    void equalsTestWithNull() {
        assertNotEquals(null, ef);
    }


    @Test
    void repeatedHashCodeHasSameValueTest() {
        EmbeddedFile ef = new EmbeddedFile("name", "type", null, null);
        int initialHashCode = ef.hashCode();
        int repeatedHashCode = ef.hashCode();
        assertEquals(initialHashCode, repeatedHashCode);
    }

    @Test
    void toStringNotNullTest() {
        EmbeddedFile ef = new EmbeddedFile("name", "type", null, null);
        String representation = ef.toString();
        System.out.println(representation);
        assertNotNull(representation);
    }

    @Test
    void setNameNullTest() {
        ef.setName(null);
        assertNull(ef.getName());
    }

    @Test
    void setTypeNullTest() {
        ef.setType(null);
        assertNull(ef.getType());
    }

    @Test
    void setNoteNullTest() {
        ef.setNote(null);
        assertNull(ef.getNote());
    }

    @Test
    void emptyDataTest() {
        ef.setData(new byte[0]);
        assertEquals(0, ef.getData().length);
    }

    @Test
    void getIdNotSetTest() {
        assertNull(ef.getId());
    }

    @Test
    void embeddedFileConstructorNameTest() {
        if (testImageFile.exists()) {
            try {
                ef = new EmbeddedFile(testImageFile.getName(), Files.probeContentType(testImageFile.toPath()), testImageData, new Note("title", "content"));
                assertEquals(testImageFile.getName(), ef.getName());
            } catch (IOException e) {
                fail("Failed during constructor name test: " + e.getMessage());
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    @Test
    void embeddedFileConstructorTypeTest() {
        if (testImageFile.exists()) {
            try {
                ef = new EmbeddedFile(testImageFile.getName(), Files.probeContentType(testImageFile.toPath()), testImageData, new Note("title", "content"));
                assertEquals(Files.probeContentType(testImageFile.toPath()), ef.getType());
            } catch (IOException e) {
                fail("Failed during constructor type test: " + e.getMessage());
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    @Test
    void embeddedFileConstructorDataTest() {
        if (testImageFile.exists()) {
            try {
                ef = new EmbeddedFile(testImageFile.getName(), Files.probeContentType(testImageFile.toPath()), testImageData, new Note("title", "content"));
                assertArrayEquals(testImageData, ef.getData());
            } catch (IOException e) {
                fail("Failed during constructor data test: " + e.getMessage());
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    @Test
    void embeddedFileConstructorNoteTest() {
        if (testImageFile.exists()) {
            try {
                Note note = new Note("title", "content");
                ef = new EmbeddedFile(testImageFile.getName(), Files.probeContentType(testImageFile.toPath()), testImageData, note);
                assertEquals(note, ef.getNote());
            } catch (IOException e) {
                fail("Failed during constructor note test: " + e.getMessage());
            }
        } else {
            System.out.println("File does not exist.");
        }
    }

    @Test
    void embeddedFileConstructorNullNoteTest()  {
        if(testImageFile.exists()) {
            try {
                ef = new EmbeddedFile(testImageFile.getName(), Files.probeContentType(testImageFile.toPath()), testImageData, null);
                assertNull(ef.getNote());
            } catch (IOException e) {
                fail("Failed during constructor test for null note: " + e.getMessage());
            }
        } else {
                System.out.println("File does not exist.");
            }
    }
}


