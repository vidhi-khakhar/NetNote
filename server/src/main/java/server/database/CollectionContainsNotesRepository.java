package server.database;

import commons.CollectionContainsNotes;
import commons.CollectionContainsNotesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CollectionContainsNotesRepository
        extends JpaRepository<CollectionContainsNotes, CollectionContainsNotesId> {

    /**
     * Finds all relationships between collections and notes based on the given collection ID.
     * This method retrieves all `CollectionContainsNotes` entities where the `collectionId`
     * matches the specified collection ID.
     *
     * @param collectionId The unique identifier of the collection.
     * @return A list of `CollectionContainsNotes` entities that match the given collection ID.
     */
    @Query("SELECT c FROM CollectionContainsNotes c WHERE c.id.collectionId = :collectionId")
    List<CollectionContainsNotes> findByCollectionId(String collectionId);

    /**
     * Finds all relationships between collections and notes based on the given note ID.
     * This method retrieves all `CollectionContainsNotes` entities where the `noteId`
     * matches the specified note ID.
     *
     * @param noteId The unique identifier of the note.
     * @return A list of `CollectionContainsNotes` entities that match the given note ID.
     */
    @Query("SELECT c FROM CollectionContainsNotes c WHERE c.id.noteId = :noteId")
    List<CollectionContainsNotes> findByNoteId(Long noteId);
}
