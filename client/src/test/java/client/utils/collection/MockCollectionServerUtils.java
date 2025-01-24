package client.utils.collection;

import client.utils.note.MockNoteServerUtils;
import commons.Collection;
import commons.Note;

import java.util.*;

public class MockCollectionServerUtils extends CollectionServerUtils {

    public static Map<String, List<Collection>> collections = new HashMap<>();
    public static Map<String, Map<String, List<String>>> relations = new HashMap<>();

    public MockCollectionServerUtils() {
        super(null);
    }

    @Override
    public List<Collection> getAllCollections(String serverAddress) {
        return collections.get(serverAddress);
    }

    @Override
    public void createCollection(Collection collection, String serverAddress) {
        if (!collections.containsKey(serverAddress)) {
            collections.put(serverAddress, new ArrayList<>());
        }
        collections.get(serverAddress).add(collection);
    }

    @Override
    public void updateCollectionAlias(String uniqueName, String newAlias, String serverAddress) {
        // TODO
    }

    @Override
    public void deleteCollection(String uniqueName, String serverAddress) {
        // TODO
    }

    @Override
    public List<Note> getAllNotesInCollection(String uniqueName, String serverAddress) {
        if (!relations.containsKey(serverAddress)) {
            relations.put(serverAddress, new HashMap<>());
            return new ArrayList<>();
        }
        if (!relations.get(serverAddress).containsKey(uniqueName)) {
            relations.get(serverAddress).put(uniqueName, new ArrayList<>());
            return new ArrayList<>();
        }
        List<String> names = relations.get(serverAddress).get(uniqueName);
        return names.stream().map(n -> MockNoteServerUtils.notes.get(serverAddress).stream()
                .filter(note -> note.getTitle().equals(n)).findFirst().get()).toList();
    }

    @Override
    public void addNoteToCollection(String uniqueName, String noteTitle, String serverAddress) {
        if (!relations.containsKey(serverAddress)) {
            relations.put(serverAddress, new HashMap<>());
        }
        if (!relations.get(serverAddress).containsKey(uniqueName)) {
            relations.get(serverAddress).put(uniqueName, new ArrayList<>());
        }
        relations.get(serverAddress).get(uniqueName).add(noteTitle);
    }

    @Override
    public void moveNoteToAnotherCollection(String uniqueName, Note note, String targetUniqueName, String serverAddress, String targetServerAddress) {
        // TODO
    }

    @Override
    public void removeNoteFromCollection(String collectionName,
                                         String noteTitle,
                                         String serverAddress) {
        // TODO
    }
}
