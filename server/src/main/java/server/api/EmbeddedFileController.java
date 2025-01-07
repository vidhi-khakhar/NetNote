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

    /**
     * Constructor for the EmbeddedFileController
     * @param noteRepository The note repository
     * @param embeddedFileRepository The embedded file repository
     */

    public EmbeddedFileController(NoteRepository noteRepository,
                                  EmbeddedFileRepository embeddedFileRepository) {
        this.noteRepository = noteRepository;
        this.embeddedFileRepository = embeddedFileRepository;
    }

    /**
     * @return A list of all the files saved in the repository
     */

    @GetMapping
    public List<EmbeddedFile> getAllFiles(){
        return embeddedFileRepository.findAll();
    }

    /**
     * Stores a file in the embeddedFileRepository
     * @param noteName The name of the note that the file is linked to
     * @param file The file that is being saved
     * @return HTTP response with a string message based if the action went
     * through properly.
     * @throws IOException Thrown if the content of the file doesn't exist
     */

    @PostMapping("/{noteName}")
    public ResponseEntity<String> storeFile(@PathVariable String noteName,
                                            @RequestBody MultipartFile file) throws IOException {
        EmbeddedFile embeddedFile = new EmbeddedFile();
        embeddedFile.setName(file.getOriginalFilename());
        embeddedFile.setType(file.getContentType());
        embeddedFile.setData(file.getBytes());

        Optional<EmbeddedFile> fileAlreadyExists = embeddedFileRepository.findAll()
                .stream()
                .filter(f -> f.getName().equals(embeddedFile.getName()))
                .findFirst();
        if(fileAlreadyExists.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A file with the same name already exists");

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

    /**
     * Changes the title of an already saved file
     * @param fileName The file that is having its name changed
     * @param title The new title
     * @return HTTP response with a string message based if the action
     * went through properly.
     */

    @PostMapping("/updateTitle/{fileName}")
    public ResponseEntity<String> updateFileTitle
    (@PathVariable String fileName, @RequestBody String title) {
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
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Successfully changed title to " + title + ".");
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }

    /**
     * Displays the contents of a file.
     * @param fileName the file in the repository
     * @return HTTP response with a byte array that will
     * display the contents of the file if everything goes well.
     */

    @GetMapping("/get/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName){
        Optional<EmbeddedFile> file = embeddedFileRepository.findAll()
                .stream()
                .filter(embeddedFile -> embeddedFile.getName().equals(fileName))
                .findFirst();
        if(file.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType
                    .parseMediaType(file.get().getType())).body(file.get().getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Deletes a file from the repository
     * @param fileName name of the file
     * @return HTTP response with a string message based if the action
     * went through properly.
     */

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