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
    private EmbeddedFile file1;
    private EmbeddedFile file2;

    @BeforeEach
    public void setUp() {
        mockRepository = mock(EmbeddedFileRepository.class);
        mockServerUtils = mock(ServerUtils.class);
        sut = new EmbeddedFileController(mockServerUtils, mockRepository);

        n1 = new Note("Note1", "Note 1 content");
        file1 = new EmbeddedFile("File1.txt", "text/plain", new byte[]{1, 2, 3}, n1);
        file2 = new EmbeddedFile("File2.txt", "text/plain", new byte[]{4, 5, 6}, null);

        n1.addEmbeddedFile(file1);
    }

    @Test
    public void getAllFiles() {
        when(mockRepository.findAll()).thenReturn(List.of(file1, file2));

        List<EmbeddedFile> result = sut.getAllFiles();
        assertEquals(List.of(file1, file2), result);
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void getFilesFromNoteWithFiles() {
        when(mockServerUtils.getNoteByTitle("Note1")).thenReturn(Optional.of(n1));

        List<EmbeddedFile> result = sut.getFilesFromNote("Note1");
        assertEquals(List.of(file1), result);
    }

    @Test
    public void getFilesFromNoteWithNoFiles() {
        Note emptyNote = new Note("EmptyNote", "No content");

        when(mockServerUtils.getNoteByTitle("EmptyNote")).thenReturn(Optional.of(emptyNote));

        List<EmbeddedFile> result = sut.getFilesFromNote("EmptyNote");
        assertNull(result);
    }

    @Test
    public void storeFileSuccess() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("File3");
        when(mockFile.getContentType()).thenReturn("text/plain");
        when(mockFile.getBytes()).thenReturn(new byte[]{7, 8, 9});
        when(mockServerUtils.getNoteByTitle("Note1")).thenReturn(Optional.of(n1));
        when(mockServerUtils.checkIfFileExists("File3", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.storeFile("Note1", mockFile);
        assertEquals(201, response.getStatusCodeValue());
        verify(mockRepository, times(1)).save(any(EmbeddedFile.class));
    }

    @Test
    public void storeFileAlreadyExists() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("File1");
        when(mockServerUtils.checkIfFileExists("File1", "Note1")).thenReturn(Optional.of(file1));

        ResponseEntity<String> response = sut.storeFile("Note1", mockFile);
        assertEquals(409, response.getStatusCodeValue());
    }

    @Test
    public void storeFileNoteNotFound() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);

        when(mockFile.getOriginalFilename()).thenReturn("File3");
        when(mockServerUtils.getNoteByTitle("NonExistentNote")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.storeFile("NonExistentNote", mockFile);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void updateFileTitleSuccess() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.of(file1));

        ResponseEntity<String> response = sut.updateFileTitle("Note1", "File1.txt", "NewTitle");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("NewTitle.plain", file1.getName());
        verify(mockRepository, times(1)).save(file1);
    }

    @Test
    public void updateFileTitleNotFound() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.updateFileTitle("Note1", "File1.txt", "NewTitle");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getFileSuccess() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.of(file1));

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
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.of(file1));

        ResponseEntity<String> response = sut.deleteFile("Note1", "File1.txt");
        assertEquals(200, response.getStatusCodeValue());
        verify(mockRepository, times(1)).delete(file1);
    }

    @Test
    public void deleteFileNotFound() {
        when(mockServerUtils.checkIfFileExists("File1.txt", "Note1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = sut.deleteFile("Note1", "File1.txt");
        assertEquals(404, response.getStatusCodeValue());
    }
}


