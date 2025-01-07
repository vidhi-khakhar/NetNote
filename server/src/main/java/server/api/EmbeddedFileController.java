package server.api;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.EmbeddedFileRepository;
import server.database.NoteRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class EmbeddedFileController {

    private final NoteRepository noteRepository;
    private final EmbeddedFileRepository embeddedFileRepository;

    public EmbeddedFileController(NoteRepository noteRepository, EmbeddedFileRepository embeddedFileRepository) {
        this.noteRepository = noteRepository;
        this.embeddedFileRepository = embeddedFileRepository;
    }

    @GetMapping
    public List<EmbeddedFile> getAllFiles(){
        return embeddedFileRepository.findAll();
    }

    @PostMapping("/{noteName}")
    public ResponseEntity<String> storeFile(@PathVariable String noteName, @RequestBody MultipartFile file) throws IOException {
        EmbeddedFile embeddedFile = new EmbeddedFile();
        embeddedFile.setName(file.getOriginalFilename());
        embeddedFile.setType(file.getContentType());
        embeddedFile.setData(file.getBytes());

        Optional<EmbeddedFile> fileAlreadyExists = embeddedFileRepository.findAll()
                .stream()
                .filter(f -> f.getName().equals(embeddedFile.getName()))
                .findFirst();
        if(fileAlreadyExists.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A file with the same name already exists");

        Optional<Note> note = noteRepository.findAll()
                .stream()
                .filter(x -> x.getTitle().equals(noteName))
                .findFirst();
        if(note.isPresent()) {
            embeddedFile.setNote(note.get());
            note.get().addEmbeddedFile(embeddedFile);
            embeddedFileRepository.save(embeddedFile);
            return ResponseEntity.status(HttpStatus.CREATED).body("File successfully stored");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
    }

    @PostMapping("/updateTitle/{fileName}")
    public ResponseEntity<String> updateFileTitle(@PathVariable String fileName, @RequestBody String title) {
        if(title.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title cannot be empty");
        Optional<EmbeddedFile> file = embeddedFileRepository.findAll()
                .stream()
                .filter(embeddedFile -> embeddedFile.getName().equals(fileName))
                .findFirst();
        if (file.isPresent()) {
            String type = file.get().getType().substring(file.get().getType().indexOf("/") + 1);
            file.get().setName(title + "." + type);
            embeddedFileRepository.save(file.get());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully changed title to " + title + ".");
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }

    @GetMapping("/get/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName){
        Optional<EmbeddedFile> file = embeddedFileRepository.findAll()
                .stream()
                .filter(embeddedFile -> embeddedFile.getName().equals(fileName))
                .findFirst();
        if(file.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.parseMediaType(file.get().getType())).body(file.get().getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        Optional<EmbeddedFile> file = embeddedFileRepository.findAll()
                .stream()
                .filter(embeddedFile -> embeddedFile.getName().equals(fileName))
                .findFirst();
        if(file.isPresent()) {
            noteRepository.getById(file.get().getNote().getNoteId()).removeEmbeddedFile(file.get());
            embeddedFileRepository.deleteById(file.get().getId());
            return ResponseEntity.ok("Successfully deleted file");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }
}