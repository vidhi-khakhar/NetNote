package server.database;

import commons.CollectionContainsNotes;
import commons.CollectionContainsNotesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionContainsNotesRepository
        extends JpaRepository<CollectionContainsNotes, CollectionContainsNotesId> {
}
