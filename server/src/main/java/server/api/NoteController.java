package server.api;

import commons.Note;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;
import server.utils.ServerUtils;

import java.util.*;

@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private final NoteRepository db;
    private final ServerUtils serverUtils;

    /**
     * Constructor for the note controller.
     * Used to inject the database note repository as a dependency.
     *
     * @param db          The note repository.
     * @param serverUtils An instance of the {@link ServerUtils} class,
     *                    injected by Spring
     */
    public NoteController(NoteRepository db, ServerUtils serverUtils) {
        this.db = db;
        this.serverUtils = serverUtils;
    }

    /**
     * Saves the provided note in the note repository
     *
     * @param note The note to be stored in the database
     */
    @PostMapping("/")
    public void storeNote(@RequestBody Note note) {
        db.save(note);
    }

    /**
     * Returns a list of all the notes stored in the note repository
     *
     * @return A list of all the notes stored in the note repository
     */
    @GetMapping("/")
    @ResponseBody
    public List<Note> getAllNotes() {
        return db.findAll();
    }

    /**
     * Returns a response containing a note with the specified title stored on the
     * current server if such a note exists, and a 404 NOT FOUND response otherwise.
     *
     * @param title The title of the note.
     * @return A response containing the note or null if it doesn't exist.
     */
    @GetMapping("/{title}")
    @ResponseBody
    public ResponseEntity<Note> getNoteByTitle(@PathVariable String title) {
        return serverUtils.getNoteByTitle(title)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Updates the title and text of a note
     *
     * @param title    The title of the note to update (part of URL)
     * @param noteData A list of key-value pairs which should have
     *                 one value for the "text" key
     *                 and/or one value for the "title" key.<br>
     *                 <p>
     *                 Example usage:<br>
     *                 <code>{ "title": "New title" }</code>
     *                 Only updates the title<br>
     *                 <code>{ "title": "New title", "text": "..." }</code>
     *                 Updates both the title and the text<br>
     * @return Returns an HTTP response with a string message based on whether
     * the request is valid or not, and on whether there is a note
     * with the specified title or not.
     */
    @PostMapping("/{title}")
    public ResponseEntity<String> updateNote(@PathVariable("title") String title,
                                             @RequestBody Map<String, String> noteData) {
        Optional<Note> noteOptional = serverUtils.getNoteByTitle(title);
        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The note with the specified title does not exist");
        }
        Note note = noteOptional.get();

        boolean containsInvalidKey = noteData
                .keySet()
                .stream()
                .anyMatch(k -> !k.equals("text") && !k.equals("title"));

        if (containsInvalidKey) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("One or more fields in the request body are not recognised. " +
                            "Must be one of: \"title\" or \"text\".");
        }

        String newTitle = noteData.get("title");
        if (newTitle != null) {
            note.setTitle(newTitle);
        }

        String newText = noteData.get("text");
        if (newText != null) {
            note.setText(newText);
        }

        db.save(note);

        return ResponseEntity.ok("Note updated successfully.");
    }

    /**
     * Deletes the note with the specified title
     * from the note repository
     *
     * @param title The title of the note to delete (part of URL)
     * @return Returns an HTTP response with the deleted note entity
     * if the provided title was valid, otherwise returns a 404 not found response
     * with an empty body.
     */
    @DeleteMapping("/{title}")
    public ResponseEntity<Note> deleteNote(@PathVariable("title") String title) {
        Optional<Note> noteOptional = serverUtils.getNoteByTitle(title);
        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        Note note = noteOptional.get();

        db.delete(note);
        return ResponseEntity.ok(note);
    }
}
