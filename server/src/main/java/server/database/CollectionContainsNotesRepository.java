package server.database;

import commons.CollectionContainsNotes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionContainsNotesRepository
        extends JpaRepository<CollectionContainsNotes, Long> {
}
