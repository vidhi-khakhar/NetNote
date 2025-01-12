import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedFileTest {

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
        EmbeddedFile ef = new EmbeddedFile();
        ef.setData(null);
        assertNull(ef.getData());
    }

    @Test
    void emptyNameTest() {
        EmbeddedFile ef = new EmbeddedFile();
        ef.setName("");
        assertEquals("", ef.getName());
    }

    @Test
    void emptyTypeTest() {
        EmbeddedFile ef = new EmbeddedFile();
        ef.setType("");
        assertEquals("", ef.getType());
    }

    @Test
    void noteNotSetTest() {
        EmbeddedFile ef = new EmbeddedFile();
        assertNull(ef.getNote());
    }

    @Test
    void largeFileTest() throws IOException {
        EmbeddedFile ef = new EmbeddedFile();
        String path = "src/test/java/largeTestFile.jpg"; // Assume large file
        File file = new File(path);
        if (file.exists()) {
            byte[] content = Files.readAllBytes(file.toPath());
            ef.setData(content);
            assertEquals(content.length, ef.getData().length);
        } else {
            System.out.println("File does not exist.");
        }
    }

    @Test
    void invalidFileTypeTest() throws IOException {
        EmbeddedFile ef = new EmbeddedFile();
        String path = "src/test/java/test.txt";
        File file = new File(path);
        if (file.exists()) {
            ef.setType(Files.probeContentType(file.toPath()));
            assertEquals("text/plain", ef.getType());
        } else {
            System.out.println("File does not exist.");
        }
    }

    @Test
    void equalsTestWithNull() {
        EmbeddedFile ef = new EmbeddedFile();
        assertNotEquals(null, ef);
    }

    @Test
    void equalsTestWithDifferentClass() {
        EmbeddedFile ef = new EmbeddedFile();
        assertNotEquals("String", ef);
    }

    @Test
    void hashCodeTest() {
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
        EmbeddedFile ef = new EmbeddedFile();
        ef.setName(null);
        assertNull(ef.getName());
    }

    @Test
    void setTypeNullTest() {
        EmbeddedFile ef = new EmbeddedFile();
        ef.setType(null);
        assertNull(ef.getType());
    }

    @Test
    void setNoteNullTest() {
        EmbeddedFile ef = new EmbeddedFile();
        ef.setNote(null);
        assertNull(ef.getNote());
    }

    @Test
    void emptyDataTest() {
        EmbeddedFile ef = new EmbeddedFile();
        ef.setData(new byte[0]);
        assertEquals(0, ef.getData().length);
    }

    @Test
    void getIdNotSetTest() {
        EmbeddedFile ef = new EmbeddedFile();
        assertNull(ef.getId());
    }

    @Test
    void embeddedFileConstructorTest() throws IOException {
        String path = "src/test/java/test.jpg";
        File file = new File(path);
        byte[] content = Files.readAllBytes(file.toPath());
        Note note = new Note("title", "content");
        EmbeddedFile ef = new EmbeddedFile(file.getName(), Files.probeContentType(file.toPath()), content, note);
        assertEquals(file.getName(), ef.getName());
        assertEquals(Files.probeContentType(file.toPath()), ef.getType());
        assertArrayEquals(content, ef.getData());
        assertEquals(note, ef.getNote());
    }

    @Test
    void embeddedFileConstructorNullNoteTest() throws IOException {
        String path = "src/test/java/test.jpg";
        File file = new File(path);
        byte[] content = Files.readAllBytes(file.toPath());
        EmbeddedFile ef = new EmbeddedFile(file.getName(), Files.probeContentType(file.toPath()), content, null);
        assertNull(ef.getNote());
    }
}


