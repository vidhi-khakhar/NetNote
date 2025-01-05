package server.api;

import commons.CollectionContainsNotes;
import commons.Note;
import commons.Collection;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionContainsNotesRepository;
import server.database.CollectionRepository;
import server.database.NoteRepository;


import java.util.*;


@RestController
@RequestMapping("/api/collections/")
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final NoteRepository noteRepository;
    private final CollectionContainsNotesRepository collectionContainsNotesRepository;

    /**
     * Constructor for the collection controller.
     * Used to inject the database repositories as dependencies.
     *
     * @param collectionRepository              The collection repository.
     * @param noteRepository                    The note repository.
     * @param collectionContainsNotesRepository The repository managing relationships between collections and notes.
     */
    public CollectionController(CollectionRepository collectionRepository,
                                NoteRepository noteRepository,
                                CollectionContainsNotesRepository collectionContainsNotesRepository) {
        this.collectionRepository = collectionRepository;
        this.noteRepository = noteRepository;
        this.collectionContainsNotesRepository = collectionContainsNotesRepository;
    }

    /**
     * Creates a new collection in the collection repository.
     * Enforces alias length restrictions and ensures unique names.
     *
     * @param collection The collection to be stored in the database.
     */
    @PostMapping("/")
    public ResponseEntity<String> createCollection(@RequestBody Collection collection) {
        if (collection.getAlias().length() < 3 || collection.getAlias().length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Alias must be between 3 and 50 characters.");
        }
        // validate the server field (ensure it's not empty)
        if (collection.getServer() == null || collection.getServer().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Server field cannot be empty.");
        }
        // check if the collection exists by searching through the existing collections using unique name
        List<Collection> allCollections = collectionRepository.findAll();
        boolean collectionExists = false;
        for (Collection existingCollection : allCollections) {
            if (existingCollection.getCollectionUniqueName().equals(collection.getCollectionUniqueName())) {
                collectionExists = true;
                break;
            }
        }

        if (collectionExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A collection with this unique name already exists.");
        }

        collectionRepository.save(collection);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Collection created successfully.");
    }

    /**
     * Returns a list of all collections stored in the collection repository.
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
     * @return The collection with the specified unique name, or a 404 response if not found.
     */
    @GetMapping("/{uniqueName}")
    public ResponseEntity<Collection> getCollectionByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        List<Collection> allCollections = collectionRepository.findAll();
        // find the collection by unique name
        Optional<Collection> collectionOptional = allCollections
                .stream()
                .filter(x -> x.getCollectionUniqueName().equals(uniqueName))
                .findFirst();

        return collectionOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Updates the alias of an existing collection.
     *
     * @param uniqueName        The unique name of the collection to update.
     * @param updatedCollection The updated collection data.
     */
    @PutMapping("/{uniqueName}")
    public ResponseEntity<String> updateCollection(@PathVariable("uniqueName") String uniqueName,
                                                   @RequestBody Collection updatedCollection) {
        // find the collection by unique name
        List<Collection> allCollections = collectionRepository.findAll();
        Optional<Collection> collectionOptional = allCollections
                .stream()
                .filter(x -> x.getCollectionUniqueName().equals(uniqueName))
                .findFirst();

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Collection not found.");
        }

        Collection collection = collectionOptional.get();

        // update the unique name if provided
        if (updatedCollection.getCollectionUniqueName() != null) {
            String newUniqueName = updatedCollection.getCollectionUniqueName();

            // check if the new unique name already exists
            Optional<Collection> existingCollection = allCollections.stream()
                    .filter(c -> c.getCollectionUniqueName().equals(newUniqueName))
                    .findFirst();

            if (existingCollection.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("A collection with this unique name already exists.");
            }

            // update the unique name
            collection.setCollectionUniqueName(newUniqueName);
        }

        collectionRepository.save(collection);
        return ResponseEntity.ok("Collection updated successfully.");
    }

    /**
     * Deletes the collection with the specified unique name and its relationships with notes.
     *
     * @param uniqueName The unique name of the collection to delete.
     * @return A response indicating the deletion status.
     */
    @DeleteMapping("/{uniqueName}")
    public ResponseEntity<String> deleteCollection(@PathVariable("uniqueName") String uniqueName) {
        // find the collection by unique name
        List<Collection> allCollections = collectionRepository.findAll();
        Optional<Collection> collectionOptional = allCollections
                .stream()
                .filter(c -> c.getCollectionUniqueName().equals(uniqueName))
                .findFirst();

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Collection not found.");
        }

        Collection collection = collectionOptional.get();

// remove relationships with notes
        List<CollectionContainsNotes> relationships = collectionContainsNotesRepository.findAll();
        for (CollectionContainsNotes relationship : relationships) {
            if (relationship.getCollectionId().equals(collection.getCollectionUniqueName())) {
                // remove relationship
                collectionContainsNotesRepository.delete(relationship);
            }
        }

        // delete the collection from the list
        Iterator<Collection> collectionIterator = allCollections.iterator();
        while (collectionIterator.hasNext()) {
            Collection c = collectionIterator.next();
            if (c.getCollectionUniqueName().equals(collection.getCollectionUniqueName())) {
                collectionIterator.remove();
                break;
            }
        }

// save the updated list of collections
        collectionRepository.saveAll(allCollections);

        return ResponseEntity.ok("Collection deleted successfully.");

    }

    /**
     * Adds a note to a collection.
     *
     * @param uniqueName The unique name of the collection.
     * @param noteTitle  The title of the note to add.
     */
    @PostMapping("/{uniqueName}/notes/{noteTitle}")
    public ResponseEntity<String> addNoteToCollection(@PathVariable("uniqueName") String uniqueName,
                                                      @PathVariable("noteTitle") String noteTitle) {

        // find the collection by unique name
        List<Collection> allCollections = collectionRepository.findAll();
        Optional<Collection> collectionOptional = allCollections
                .stream()
                .filter(x -> x.getCollectionUniqueName().equals(uniqueName))
                .findFirst();

        if (collectionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Collection with the specified unique name does not exist.");
        }

        // find the note by title
        List<Note> allNotes = noteRepository.findAll();
        Optional<Note> noteOptional = allNotes
                .stream()
                .filter(note -> note.getTitle().equals(noteTitle))
                .findFirst();

        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note with the specified title does not exist.");
        }

        // retrieve the collection unique name and note id
        String collectionUniqueName = collectionOptional.get().getCollectionUniqueName();
        Long noteId = noteOptional.get().getNoteId();

        // create a new relationship between collection and note using CollectionContainsNotesId
        CollectionContainsNotes relation = new CollectionContainsNotes(collectionUniqueName, noteId);

        // save the relationship to the database
        collectionContainsNotesRepository.save(relation);

        return ResponseEntity.ok("Note added to collection successfully.");

    }

    /**
     * Moves a note from one collection to another.
     *
     * @param uniqueName           The unique name of the current collection.
     * @param noteTitle            The title of the note to move.
     * @param targetCollectionName The unique name of the target collection.
     */
    @PostMapping("/{uniqueName}/move-note/{noteTitle}")
    public ResponseEntity<String> moveNoteToAnotherCollection(@PathVariable("uniqueName") String uniqueName,
                                                              @PathVariable("noteTitle") String noteTitle,
                                                              @RequestBody String targetCollectionName) {

// find the current collection and target collection by unique name
        List<Collection> allCollections = collectionRepository.findAll();
        Optional<Collection> currentCollection = allCollections
                .stream()
                .filter(c -> c.getCollectionUniqueName().equals(uniqueName))
                .findFirst();

        Optional<Collection> targetCollection = allCollections
                .stream()
                .filter(c -> c.getCollectionUniqueName().equals(targetCollectionName))
                .findFirst();

        if (currentCollection.isEmpty() || targetCollection.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("One or both collections do not exist.");
        }

        // find the note by title
        List<Note> allNotes = noteRepository.findAll();
        Optional<Note> noteOptional = allNotes
                .stream()
                .filter(note -> note.getTitle().equals(noteTitle))
                .findFirst();

        if (noteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note with the specified title does not exist.");
        }

        // Retrieve all CollectionContainsNotes relationships
        List<CollectionContainsNotes> allRelations = collectionContainsNotesRepository.findAll();

// find the relationship where the note exists in the current collection
        CollectionContainsNotes currentRelation = allRelations
                .stream()
                .filter(relation ->
                        relation.getCollectionId().equals(currentCollection.get().getCollectionUniqueName()) &&
                        relation.getNoteId().equals(noteOptional.get().getNoteId()))
                .findFirst()
                .orElse(null);

        if (currentRelation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note is not part of the current collection.");
        }


        // remove the note from the current collection
        collectionContainsNotesRepository.delete(currentRelation);

        // add the note to the target collection
        CollectionContainsNotes newRelation = new CollectionContainsNotes(
                targetCollection.get().getCollectionUniqueName(),
                noteOptional.get().getNoteId()
        );

        collectionContainsNotesRepository.save(newRelation);



        return ResponseEntity.ok("Note moved successfully.");
    }

}




