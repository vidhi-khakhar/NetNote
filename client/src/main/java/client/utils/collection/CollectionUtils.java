package client.utils.collection;

import client.scenes.EditCollectionsCtrl;
import commons.Collection;

import java.util.*;

/**
 * Utilities for the {@link EditCollectionsCtrl}
 */
public class CollectionUtils {

    public static final String NEW_COLLECTION_DEFAULT_ALIAS = "New Collection";

    /**
     * Creates a new collection with the alias "New Collection (n)",
     * where n is the smallest number so that the name is still unique.
     * Adds it to the provided list of collections.
     *
     * @param currentCollections The current list of collections
     * @return The index of the newly created collection
     */
    public int createNewCollection(List<Collection> currentCollections) {
        String newCollectionAlias;

        List<String> conflictingTitles = currentCollections.stream()
                .map(Collection::getAlias)
                .filter(a -> a.startsWith(NEW_COLLECTION_DEFAULT_ALIAS))
                .toList();

        if (!conflictingTitles.contains(NEW_COLLECTION_DEFAULT_ALIAS)) {
            newCollectionAlias = NEW_COLLECTION_DEFAULT_ALIAS;
        } else {
            int i = 2;
            while (conflictingTitles.contains(NEW_COLLECTION_DEFAULT_ALIAS + " (" + i + ")"))
                i++;
            newCollectionAlias = NEW_COLLECTION_DEFAULT_ALIAS + " (" + i + ")";
        }
        return createNewCollection(currentCollections, newCollectionAlias);
    }

    /**
     * Creates a new collection with the provided alias.
     * Adds it to the provided list of collections.
     *
     * @param currentCollections The current list of collections
     * @param alias              The alias of the newly created collection
     * @return The index of the newly created collection
     */
    public int createNewCollection(List<Collection> currentCollections, String alias) {
        currentCollections.add(new Collection("", alias, ""));
        return currentCollections.size() - 1;
    }

    /**
     * Removes a collection in a list of collections
     *
     * @param currentCollections The current list of collections
     * @param currentIndex       The index of the currently selected collection
     * @param targetIndex        The index of the collection to delete
     * @return The index of the selected collection after the deletion
     */
    public int removeCollection(
            List<Collection> currentCollections,
            int currentIndex,
            int targetIndex) {
        if (targetIndex > currentCollections.size() - 1 ||
                targetIndex < 0) return currentIndex;
        currentCollections.remove(targetIndex);
        if (currentIndex == currentCollections.size())
            return currentIndex - 1;
        if (targetIndex < currentIndex)
            return currentIndex - 1;
        return currentIndex;
    }

    /**
     * Checks if a collection with a unique name already exists in a list of collections
     *
     * @param collections The list of collections to check in
     * @param uniqueName  The unique name of the collection to search for in the list
     * @return True if the collection with that unique name is already in the list,
     * false otherwise.
     */
    public boolean collectionAlreadyExists(List<Collection> collections, String uniqueName) {
        return collections
                .stream()
                .anyMatch(c -> c.getCollectionUniqueName().equals(uniqueName));
    }
}
