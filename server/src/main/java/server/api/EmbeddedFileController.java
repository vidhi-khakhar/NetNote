package server.api;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.EmbeddedFileRepository;
import server.utils.ServerUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class EmbeddedFileController {

    private final EmbeddedFileRepository embeddedFileRepository;

    private final ServerUtils serverUtils;

    /**
     * Constructor for the EmbeddedFileController
     * @param serverUtils Service with helper methods
     * @param embeddedFileRepository The embedded file repository
     */

    public EmbeddedFileController(ServerUtils serverUtils,
                                  EmbeddedFileRepository embeddedFileRepository) {
        this.serverUtils = serverUtils;
        this.embeddedFileRepository = embeddedFileRepository;
    }

    /**
     * @return A list of all the files saved in the repository
     */

    @GetMapping("/")
    public List<EmbeddedFile> getAllFiles(){
        return embeddedFileRepository.findAll();
    }

    /**
     * Returns a list of all the files that are in a note.
     * @param noteName Name of the note
     * @return Files found in the note.
     */

    @GetMapping("/notes/{noteName}")
    public List<EmbeddedFile> getFilesFromNote(@PathVariable String noteName){
        Optional<Note> note = serverUtils.getNoteByTitle(noteName);
        if(note.isPresent())
            if(!note.get().getEmbeddedFiles().isEmpty()) {
                return note.get().getEmbeddedFiles();
            }
        return null;
    }

    /**
     * Stores a file in the embeddedFileRepository
     * @param noteName The name of the note that the file is linked to
     * @param file The file that is being saved
     * @return HTTP response with a string message based if the action went
     * through properly.
     * @throws IOException Thrown if the content of the file doesn't exist
     */

    @PutMapping("/{noteName}")
    public ResponseEntity<String> storeFile(@PathVariable String noteName,
                                            @RequestBody EmbeddedFile file){
        Optional<EmbeddedFile> fileAlreadyExists =
                serverUtils.checkIfFileExists(file.getName(), noteName);
        if(fileAlreadyExists.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A file with the same name already exists");

        Optional<Note> note = serverUtils.getNoteByTitle(noteName);
        if(note.isPresent()) {
            file.setNote(note.get());
            note.get().addEmbeddedFile(file);
            embeddedFileRepository.save(file);
            return ResponseEntity.status(HttpStatus.CREATED).body("File successfully stored");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
    }

    /**
     * Changes the title of an already saved file
     * @param fileName The file that is having its name changed
     * @param noteName The name of the note the file belongs to
     * @param title The new title
     * @return HTTP response with a string message based if the action
     * went through properly.
     */

    @PostMapping("/{noteName}/content/{fileName}")
    public ResponseEntity<String> updateFileTitle
    (@PathVariable String noteName, @PathVariable String fileName, @RequestBody String title) {
        if(title.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title cannot be empty");
        Optional<EmbeddedFile> file = serverUtils.checkIfFileExists(fileName, noteName);
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
     * @param noteName the name of the note the file belongs to
     * @param fileName the file in the repository
     * @return HTTP response with a byte array that will
     * display the contents of the file if everything goes well.
     */

    @GetMapping("/{noteName}/content/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String noteName,
                                          @PathVariable String fileName){
        Optional<EmbeddedFile> file = serverUtils.checkIfFileExists(fileName, noteName);
        if(file.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType
                    .parseMediaType(file.get().getType())).body(file.get().getData());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Deletes a file from the repository
     * @param noteName name of the note the file belongs to
     * @param fileName name of the file
     * @return HTTP response with a string message based if the action
     * went through properly.
     */

    @DeleteMapping("/{noteName}/content/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String noteName,
                                             @PathVariable String fileName){
        Optional<EmbeddedFile> file = serverUtils.checkIfFileExists(fileName, noteName);
        if(file.isPresent()) {
            EmbeddedFile embeddedFile = file.get();
            Note note = embeddedFile.getNote();
            note.removeEmbeddedFile(embeddedFile);
            embeddedFileRepository.delete(embeddedFile);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully deleted file");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }
}