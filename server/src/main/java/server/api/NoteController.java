package server.api;

import commons.Note;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;

import java.util.*;

@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private final NoteRepository notesTable;

    /**
     * Constructor for the note controller.
     * Used to inject the database note repository as a dependency.
     *
     * @param notesTable The note repository.
     */
    public NoteController(NoteRepository notesTable) {
        this.notesTable = notesTable;
    }

    /**
     * Saves the provided note in the note repository
     *
     * @param note The note to be stored in the database
     */
    @PostMapping("/")
    public void storeNote(@RequestBody Note note) {
        notesTable.save(note);
    }

    /**
     * Returns a list of all the notes stored in the note repository
     *
     * @return A list of all the notes stored in the note repository
     */
    @GetMapping("/")
    @ResponseBody
    public List<Note> getAllNotes() {
        return notesTable.findAll();
    }

    /**
     * Updates the title and text of a note
     *
     * @param id The id of the note to update (part of URL)
     * @param noteData A list of key-value pairs which should have
     *                 one value for the "text" key
     *                 and/or one value for the "title" key.<br>
     *
     *                 Example usage:<br>
     *                 <code>{ "title": "New title" }</code>
     *                 Only updates the title<br>
     *                 <code>{ "title": "New title", "text": "..." }</code>
     *                 Updates both the title and the text<br>
     * @return Returns an HTTP response with a string message based on whether
     * the request is valid or not, and on whether there is a note
     * with the specified id or not.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> updateNote(@PathVariable("id") Long id,
                                     @RequestBody Map<String, String> noteData) {
        Optional<Note> noteOptional = notesTable.findById(id);
        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The note with the specified id does not exist");
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

        notesTable.save(note);

        return ResponseEntity.ok("Note updated successfully.");
    }

    /**
     * Deletes the note with the specified id
     * from the note repository
     *
     * @param id The id of the note to be deleted (part of URL)
     * @return Returns an HTTP response with the deleted note entity
     * if the provided id was valid, otherwise returns a 404 not found response
     * with an empty body.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Note> deleteNote(@PathVariable("id") Long id) {
        Optional<Note> noteOptional = notesTable.findById(id);
        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        Note note = noteOptional.get();

        notesTable.deleteById(id);

        return ResponseEntity.ok(note);
    }
}
