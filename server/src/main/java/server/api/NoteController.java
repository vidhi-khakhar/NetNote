package server.api;

import org.springframework.web.bind.annotation.*;
import server.database.NoteRepository;

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
}
