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
    void getIdTest(){
        EmbeddedFile embeddedFile = new EmbeddedFile("name", "type", null, null);
        Long id = embeddedFile.getId();
        assertNull(id);
    }

    @Test
    void toStringTest(){
        EmbeddedFile embeddedFile = new EmbeddedFile("name", "type", null, null);
        String representation = embeddedFile.toString();
        System.out.println(representation);
        assertTrue(representation.contains("data=<null>") &&
                representation.contains("name=name"));
    }
}