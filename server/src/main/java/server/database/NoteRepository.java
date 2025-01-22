package server.database;

import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Method to retrieve a note by its title
     * @param title The title of the note.
     * @return An Optional containing the note if found.
     */
    Optional<Note> findByTitle(String title);

}
