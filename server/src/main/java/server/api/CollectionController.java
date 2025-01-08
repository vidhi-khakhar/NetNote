package server.api;

import commons.CollectionContainsNotes;
import commons.CollectionContainsNotesId;
import commons.Note;
import commons.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionContainsNotesRepository;
import server.database.CollectionRepository;
import server.database.NoteRepository;
import server.utils.ServerUtils;

import java.util.*;

@RestController
@RequestMapping("/api/collections/")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final CollectionContainsNotesRepository collectionContainsNotesRepository;
    private final ServerUtils serverUtils;
    private final NoteRepository noteRepository;

    /**
     * Constructor for the collection controller.
     * Used to inject the database repositories as dependencies.
     *
     * @param collectionRepository              The collection repository.
     * @param collectionContainsNotesRepository The repository managing relationships between
     *                                          collections and notes.
     * @param noteRepository                    The note repository.
     * @param serverUtils                       An instance of the server utilities.
     */
    @Autowired
    public CollectionController(CollectionRepository collectionRepository,
                                CollectionContainsNotesRepository
                                        collectionContainsNotesRepository,
                                ServerUtils serverUtils, NoteRepository noteRepository) {
        this.collectionRepository = collectionRepository;
        this.collectionContainsNotesRepository = collectionContainsNotesRepository;
        this.serverUtils = serverUtils;
        this.noteRepository = noteRepository;
    }

    /**
     * Creates a new collection in the collection repository.
     * Enforces alias length restrictions and ensures unique names.
     *
     * @param collection The collection to be stored in the database.
     * @return status
     */
    @PutMapping("/")
    public ResponseEntity<String> createCollection(@RequestBody Collection collection) {
        if (collection.getAlias().length() < 3 || collection.getAlias().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Alias must be between 3 and 50 characters.");
        }
        // validate the server field (ensure it's not empty)
        if (collection.getServer() == null || collection.getServer().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Server field cannot be empty.");
        }

        if (collectionRepository.existsById(collection.getCollectionUniqueName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A collection with this unique name already exists.");
        }

        collectionRepository.save(collection);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Collection created successfully.");
    }

    /**
     * Returns a list of all collections stored on the current server.
     *
     * @return A list of all collections.
     */
    @GetMapping("/")
    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    /**
     * Returns a specific collection by its unique name.
     *
     * @param uniqueName The unique name of the collection to retrieve.
     * @return The collection with the specified unique name,
     * or a 404 response if not found.
     */
    @GetMapping("/{uniqueName}")
    public ResponseEntity<Collection> getCollectionByUniqueName
    (@PathVariable("uniqueName") String uniqueName) {
        Optional<Collection> collectionOptional = collectionRepository.findById(uniqueName);

        return collectionOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Updates the alias of an existing collection.
     *
     * @param uniqueName        The unique name of the collection to update.
     * @param updatedCollection The updated collection data.
     * @return status
     */
    @PostMapping("/{uniqueName}")
    public ResponseEntity<String> updateCollectionAlias
    (@PathVariable("uniqueName") String uniqueName, @RequestBody Collection updatedCollection) {
        // find the collection by unique name
        Optional<Collection> collectionOptional = collectionRepository.findById(uniqueName);
        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Collection not found.");
        }

        Collection collection = collectionOptional.get();

        // update the alias if it's provided
        if (updatedCollection.getAlias() != null) {
            // update the alias
            collection.setAlias(updatedCollection.getAlias());
        }

        collectionRepository.save(collection);
        return ResponseEntity.ok("Collection alias updated successfully.");
    }

    /**
     * Deletes the collection with the specified unique name and its relationships with notes.
     *
     * @param uniqueName The unique name of the collection to delete.
     * @return A response indicating the deletion status.
     */
    @DeleteMapping("/{uniqueName}")
    public ResponseEntity<String> deleteCollection(@PathVariable("uniqueName") String uniqueName) {
        Optional<Collection> collectionOptional = collectionRepository.findById(uniqueName);

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Collection not found.");
        }

        Collection collection = collectionOptional.get();

        List<CollectionContainsNotes> relationships = collectionContainsNotesRepository.findAll();
        for (CollectionContainsNotes relationship : relationships) {
            if (relationship.getCollectionId().equals(collection.getCollectionUniqueName())) {
                collectionContainsNotesRepository.delete(relationship);
            }
        }

        collectionRepository.deleteById(collection.getCollectionUniqueName());

        return ResponseEntity.ok("Collection deleted successfully.");
    }

    /**
     * Gets all the notes from the collection with the given name
     *
     * @param uniqueName The name of the collection from which to fetch all the notes
     * @return A list of all the notes in that collection, if the collection exists.
     */
    @GetMapping("/{uniqueName}/notes")
    public ResponseEntity<List<Note>> getAllNotesInCollection(
            @PathVariable("uniqueName") String uniqueName) {

        Optional<Collection> collectionOptional = collectionRepository.findById(uniqueName);

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        List<Note> notes = collectionContainsNotesRepository
                .findAll()
                .stream()
                .filter(entry -> entry.getCollectionId().equals(uniqueName))
                .map(CollectionContainsNotes::getNoteId)
                .map(noteId -> noteRepository.findById(noteId).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(notes);
    }

    /**
     * Adds a note to a collection.
     *
     * @param uniqueName The unique name of the collection.
     * @param noteTitle  The title of the note to add.
     * @return status
     */
    @PostMapping("/{uniqueName}/notes/{noteTitle}")
    public ResponseEntity<String> addNoteToCollection(@PathVariable("uniqueName") String uniqueName,
                                                      @PathVariable("noteTitle") String noteTitle) {

        Optional<Collection> collectionOptional = collectionRepository.findById(uniqueName);

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Collection with the specified unique name does not exist.");
        }

        Optional<Note> noteOptional = serverUtils.getNoteByTitle(noteTitle);

        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note with the specified title does not exist.");
        }

        String collectionUniqueName = collectionOptional
                .get().getCollectionUniqueName();
        Long noteId = noteOptional.get().getNoteId();

        // create a new relationship between collection
        // and note using CollectionContainsNotesId
        CollectionContainsNotes relation =
                new CollectionContainsNotes(collectionUniqueName, noteId);

        // save the relationship to the database
        collectionContainsNotesRepository.save(relation);

        return ResponseEntity.ok("Note added to collection successfully.");
    }

    /**
     * Moves a note from one collection
     * to another.
     *
     * @param uniqueName       The unique name of the current collection.
     * @param noteTitle        The title of the note to move.
     * @param targetUniqueName The unique name of the target collection.
     * @return A response entity containing a status message.
     */
    @PostMapping("/{uniqueName}/notes/{noteTitle}/move/{targetUniqueName}")
    public ResponseEntity<String> moveNoteToAnotherCollection(
            @PathVariable("uniqueName") String uniqueName,
            @PathVariable("noteTitle") String noteTitle,
            @PathVariable("targetUniqueName") String targetUniqueName) {

        // find the current collection and target collection by unique name
        Optional<Collection> currentCollection = collectionRepository.findById(uniqueName);
        Optional<Collection> targetCollection = collectionRepository.findById(targetUniqueName);

        if (currentCollection.isEmpty() || targetCollection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("One or both collections do not exist.");
        }

        Optional<Note> noteOptional = serverUtils.getNoteByTitle(noteTitle);

        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note with the specified title does not exist.");
        }

        CollectionContainsNotesId relId =
                new CollectionContainsNotesId(uniqueName, noteOptional.get().getNoteId());

        if (!collectionContainsNotesRepository.existsById(relId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Note is not part of this collection.");
        }
        collectionContainsNotesRepository.deleteById(relId);

        // add the note to the target collection
        CollectionContainsNotes newRelation = new CollectionContainsNotes(
                targetCollection.get().getCollectionUniqueName(),
                noteOptional.get().getNoteId()
        );

        collectionContainsNotesRepository.save(newRelation);
        return ResponseEntity.ok("Note moved successfully.");
    }

    /**
     * After successfully calling this method, the note with the given title
     * will no longer be part of the collection with the given name.
     *
     * @param uniqueName The name of the collection to remove the note from.
     * @param noteTitle  The name of the note to be removed.
     * @return A message detailing whether the removal was successful or not.
     */
    @DeleteMapping("/{uniqueName}/notes/{noteTitle}")
    public ResponseEntity<String> removeNoteFromCollection(
            @PathVariable("uniqueName") String uniqueName,
            @PathVariable("noteTitle") String noteTitle) {

        Optional<Collection> collectionOptional = collectionRepository.findById(uniqueName);
        Optional<Note> noteOptional = serverUtils.getNoteByTitle(noteTitle);

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The collection with the given title does not exist.");
        }
        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The note with the given title does not exist.");
        }

        CollectionContainsNotesId relId =
                new CollectionContainsNotesId(uniqueName, noteOptional.get().getNoteId());

        if (!collectionContainsNotesRepository.existsById(relId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Note is not part of this collection.");
        }

        collectionContainsNotesRepository.deleteById(relId);
        return ResponseEntity.ok("The note is no longer part of this collection");
    }
}
