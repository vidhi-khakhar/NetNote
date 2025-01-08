package server.utils;

import commons.EmbeddedFile;
import commons.Note;
import org.springframework.stereotype.Service;
import server.database.EmbeddedFileRepository;
import server.database.NoteRepository;

import java.util.*;

@Service
public class ServerUtils {

    private final NoteRepository noteRepo;
    private final EmbeddedFileRepository embeddedFileRepo;

    /**
     * Constructor containing the dependencies of {@link ServerUtils}
     * @param noteRepo The table in the DB containing all the notes
     * @param embeddedFileRepo The table in the DB containing all the embedded files
     */
    public ServerUtils(NoteRepository noteRepo,
                       EmbeddedFileRepository embeddedFileRepo) {
        this.noteRepo = noteRepo;
        this.embeddedFileRepo = embeddedFileRepo;
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

    /**
     * Helper method that checks if a file exists by its name.
     * @param fileName name of the file
     * @param noteName name of the note it belongs to
     * @return An optional that either contains a file or not.
     */

    public Optional<EmbeddedFile> checkIfFileExists(String fileName, String noteName){
        return embeddedFileRepo.findAll()
                .stream()
                .filter(x -> x.getNote().getTitle().equals(noteName))
                .filter(x -> x.getName().equals(fileName))
                .findFirst();
    }
}
