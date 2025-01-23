package server.database;

import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Method to retrieve a note by its title
     * @param title The title of the note.
     * @return An Optional containing the note if found.
     */
    @Query("SELECT n FROM Note n WHERE n.title = :title")
    Optional<Note> findByTitle(@Param("title") String title);

}
