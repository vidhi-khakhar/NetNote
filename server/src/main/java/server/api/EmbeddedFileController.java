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
        Optional<Note> note = checkIfNoteExists(noteName);
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
                                            @RequestBody MultipartFile file) throws IOException {
        EmbeddedFile embeddedFile = new EmbeddedFile();
        embeddedFile.setName(file.getOriginalFilename());
        embeddedFile.setType(file.getContentType());
        embeddedFile.setData(file.getBytes());

        Optional<EmbeddedFile> fileAlreadyExists =
                checkIfFileExists(embeddedFile.getName(), noteName);
        if(fileAlreadyExists.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A file with the same name already exists");

        Optional<Note> note = checkIfNoteExists(noteName);
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
     * @param noteName The name of the note the file belongs to
     * @param title The new title
     * @return HTTP response with a string message based if the action
     * went through properly.
     */

    @PostMapping("/{noteName}/{fileName}")
    public ResponseEntity<String> updateFileTitle
    (@PathVariable String noteName, @PathVariable String fileName, @RequestBody String title) {
        if(title.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title cannot be empty");
        Optional<EmbeddedFile> file = checkIfFileExists(fileName, noteName);
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

    @GetMapping("/{noteName}/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String noteName,
                                          @PathVariable String fileName){
        Optional<EmbeddedFile> file = checkIfFileExists(fileName, noteName);
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

    @DeleteMapping("/{noteName}/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String noteName,
                                             @PathVariable String fileName){
        Optional<EmbeddedFile> file = checkIfFileExists(fileName, noteName);
        if(file.isPresent()) {
            EmbeddedFile embeddedFile = file.get();
            Note note = embeddedFile.getNote();
            note.removeEmbeddedFile(embeddedFile);
            embeddedFileRepository.delete(embeddedFile);
            return ResponseEntity.ok("Successfully deleted file");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
    }

    /**
     * Helper method that checks if a file exists by its name.
     * @param fileName name of the file
     * @param noteName name of the note it belongs to
     * @return An optional that either contains a file or not.
     */

    public Optional<EmbeddedFile> checkIfFileExists(String fileName, String noteName){
        return embeddedFileRepository.findAll()
                .stream()
                .filter(x -> x.getNote().getTitle().equals(noteName))
                .filter(x -> x.getName().equals(fileName))
                .findFirst();
    }

    /**
     * Helper method that checks if a note exists by its name.
     * @param noteName name of the note
     * @return An optional that either contains a note or not.
     */

    public Optional<Note> checkIfNoteExists(String noteName){
        return noteRepository.findAll()
                .stream()
                .filter(x -> x.getTitle().equals(noteName))
                .findFirst();
    }

}