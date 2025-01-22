package server.database;

import commons.CollectionContainsNotes;
import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Method to retrieve a note by its title
     * @param title The title of the note.
     * @return An Optional containing the note if found.
     */
    Optional<Note> findByTitle(String title);

    /**
     * Method to retrieve notes by some of their text.
     * @param text A portion of the text to search for in notes.
     * @return A list of notes that contain the given text.
     */
    List<Note> findByTextContaining(String text);
}
