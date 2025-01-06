package server.api;

import commons.EmbeddedFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.EmbeddedFileRepository;
import server.database.NoteRepository;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files/notes/{noteId}")
public class EmbeddedFileController {

    private final NoteRepository noteRepository;
    private final EmbeddedFileRepository embeddedFileRepository;

    public EmbeddedFileController(NoteRepository noteRepository, EmbeddedFileRepository embeddedFileRepository) {
        this.noteRepository = noteRepository;
        this.embeddedFileRepository = embeddedFileRepository;
    }

    @GetMapping
    public List<EmbeddedFile> getAllFiles() {
        return embeddedFileRepository.findAll();
    }

    @PostMapping
    public void createFile(@PathVariable Long noteId, @RequestBody MultipartFile file) throws IOException {
        EmbeddedFile embeddedFile = new EmbeddedFile();
        embeddedFile.setName(file.getOriginalFilename());
        embeddedFile.setType(file.getContentType());
        embeddedFile.setData(file.getBytes());
        embeddedFile.setNote(noteRepository.findById(noteId).get());
        embeddedFileRepository.save(embeddedFile);
    }
}