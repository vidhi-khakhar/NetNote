package server.api;

import commons.EmbeddedFile;
import commons.Note;
import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import server.database.EmbeddedFileRepository;
import server.utils.ServerUtils;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmbeddedFileControllerTest {

    private EmbeddedFileController sut; // System Under Test
    private EmbeddedFileRepository mockRepository;
    private ServerUtils mockServerUtils;
    private Note n1;

    @BeforeEach
    public void setUp() {
        mockRepository = mock(EmbeddedFileRepository.class);
        mockServerUtils = mock(ServerUtils.class);
        sut = new EmbeddedFileController(mockServerUtils, mockRepository);
        n1 = new Note("Note1", "Note 1 content");
    }

    @Test
    public void getAllFiles() {
        EmbeddedFile file1 = new EmbeddedFile("File1", "text/plain", new byte[]{1, 2, 3}, null);
        EmbeddedFile file2 = new EmbeddedFile("File2", "text/plain", new byte[]{4, 5, 6}, null);

        when(mockRepository.findAll()).thenReturn(List.of(file1, file2));

        List<EmbeddedFile> result = sut.getAllFiles();
        assertEquals(List.of(file1, file2), result);
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void getFilesFromNoteWithFiles() {
        Note note = new Note("Note1", "Content");
        EmbeddedFile file = new EmbeddedFile("File1", "text/plain", new byte[]{1, 2, 3}, note);
        note.addEmbeddedFile(file);

        when(mockServerUtils.getNoteByTitle("Note1")).thenReturn(Optional.of(note));

        List<EmbeddedFile> result = sut.getFilesFromNote("Note1");
        assertEquals(List.of(file), result);
    }

    @Test
    public void getFilesFromNoteWithNoFiles() {
        Note note = new Note("Note1", "Content");

        when(mockServerUtils.getNoteByTitle("Note1")).thenReturn(Optional.of(note));

        List<EmbeddedFile> result = sut.getFilesFromNote("Note1");
        assertNull(result);
    }

    @Test
    public void storeFileSuccess() throws IOException {
        Note note = new Note("Note1", "Content");
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("File1");
        when(mockFile.getContentType()).thenReturn("text/plain");
        when(mockFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(mockServerUtils.getNoteByTitle("Note1")).thenReturn(Optional.of(note));
        when(mockServerUtils.checkIfFileExists("File1", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.storeFile("Note1", mockFile);
        assertEquals(201, response.getStatusCodeValue());
        verify(mockRepository, times(1)).save(any(EmbeddedFile.class));
    }

    @Test
    public void storeFileAlreadyExists() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("File1");
        when(mockServerUtils.checkIfFileExists("File1", "Note1"))
                .thenReturn(Optional.of(new EmbeddedFile()));

        ResponseEntity<String> response = sut.storeFile("Note1", mockFile);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    public void storeFileNoteNotFound() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("File1");
        when(mockServerUtils.getNoteByTitle("Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.storeFile("Note1", mockFile);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void updateFileTitleSuccess() {
        EmbeddedFile file = new EmbeddedFile("File1.txt", "text/plain", new byte[]{}, n1);
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.of(file));

        ResponseEntity<String> response = sut.updateFileTitle("Note1", "File1.txt", "NewTitle");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("NewTitle.plain", file.getName()); // Match the current controller behavior
        verify(mockRepository, times(1)).save(file);
    }

    @Test
    public void updateFileTitleNotFound() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.updateFileTitle("Note1", "File1.txt", "NewTitle");
        assertEquals(404, response.getStatusCodeValue());
    }


    @Test
    public void getFileSuccess() {
        EmbeddedFile file = new EmbeddedFile("File1.txt", "text/plain", new byte[]{1, 2, 3}, null);
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.of(file));

        ResponseEntity<byte[]> response = sut.getFile("Note1", "File1.txt");
        assertEquals(200, response.getStatusCodeValue());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getBody());
    }

    @Test
    public void getFileNotFound() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = sut.getFile("Note1", "File1.txt");
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void deleteFileSuccess() {
        EmbeddedFile file = new EmbeddedFile("File1.txt", "text/plain", new byte[]{}, n1);
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.of(file));

        ResponseEntity<String> response = sut.deleteFile("Note1", "File1.txt");
        assertEquals(200, response.getStatusCodeValue());
        verify(mockRepository, times(1)).delete(file);
    }

    @Test
    public void deleteFileNotFound() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.deleteFile("Note1", "File1.txt");
        assertEquals(404, response.getStatusCodeValue());
    }
}
