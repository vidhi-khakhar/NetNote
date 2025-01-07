package server.utils;

import commons.Note;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.*;

@Service
public class ServerUtils {

    private NoteRepository noteRepo;

    /**
     * Default constructor used for dependency injection
     */
    public ServerUtils() {
    }

    /**
     * Constructor containing the dependencies of {@link ServerUtils}
     * @param noteRepo The table in the DB containing all the notes
     */
    public ServerUtils(NoteRepository noteRepo) {
        this.noteRepo = noteRepo;
    }

    /**
     * Returns the note with the given title stored in the database,
     * wrapped inside an {@link Optional}
     * @param title The title of the note
     * @return The note with the given title wrapped in an {@link Optional}
     */
    public Optional<Note> getNoteByTitle(String title) {
        return noteRepo
                .findAll()
                .stream()
                .filter(n -> n.getTitle().equals(title))
                .findFirst();
    }
}
