package server.api;

import commons.Note;
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
        System.out.println(note);
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
     * Deletes the note with the specified id
     * from the note repository
     *
     * @param id The id of the note to be deleted
     */
    @DeleteMapping("/{id}")
    public void deleteNote(@PathVariable("id") Long id) {
        notesTable.deleteById(id);
    }
}
