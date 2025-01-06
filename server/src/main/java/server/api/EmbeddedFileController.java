package server.api;

import commons.EmbeddedFile;
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

    @PostMapping("/{noteId}")
    public void storeFile(@PathVariable Long noteId, @RequestBody MultipartFile file) throws IOException {
        EmbeddedFile embeddedFile = new EmbeddedFile();
        embeddedFile.setName(file.getOriginalFilename());
        embeddedFile.setType(file.getContentType());
        embeddedFile.setData(file.getBytes());
        noteRepository.findById(noteId).get().addEmbeddedFile(embeddedFile);
        embeddedFileRepository.save(embeddedFile);
    }

    @PostMapping("/updateTitle/{fileId}")
    public ResponseEntity<String> updateFileTitle(@PathVariable Long fileId, @RequestBody String title) {
        Optional<EmbeddedFile> embeddedFile = embeddedFileRepository.findById(fileId);
        if (embeddedFile.isPresent()) {
            String type = embeddedFile.get().getType().substring(embeddedFile.get().getType().indexOf("/") + 1);
            embeddedFile.get().setName(title + "." + type);
            embeddedFileRepository.save(embeddedFile.get());
            return ResponseEntity.status(HttpStatus.OK).body("Successfully changed title to " + title);
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }

    @GetMapping("/get/{fileId}")
    public ResponseEntity<byte[]> getImageFile(@PathVariable Long fileId){
        Optional<EmbeddedFile> embeddedFile = embeddedFileRepository.findById(fileId);
        if(embeddedFile.isPresent()){
            if(!embeddedFile.get()
                    .getType()
                    .substring(0, embeddedFile.get().getType().indexOf('/'))
                    .equals("image"))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.parseMediaType(embeddedFile.get().getType())).body(embeddedFile.get().getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId){
        embeddedFileRepository.deleteById(fileId);
        return ResponseEntity.ok("Successfully deleted file");
    }
}