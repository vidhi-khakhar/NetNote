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
     * Generates a new alias of the form "New Collection (n)",
     * where n is the smallest number so that the alias is still unique.
     *
     * @param unavailableAliases The list of aliases that cannot be returned by this method.
     *                           The new alias must be unique.
     * @return The newly created unique alias.
     */
    public String generateAlias(List<String> unavailableAliases) {
        List<String> conflictingAliases = unavailableAliases.stream()
                .filter(a -> a.startsWith(NEW_COLLECTION_DEFAULT_ALIAS))
                .toList();
        if (!conflictingAliases.contains(NEW_COLLECTION_DEFAULT_ALIAS)) {
            return NEW_COLLECTION_DEFAULT_ALIAS;
        }
        int i = 2;
        while (conflictingAliases.contains(NEW_COLLECTION_DEFAULT_ALIAS + " (" + i + ")"))
            i++;
        return NEW_COLLECTION_DEFAULT_ALIAS + " (" + i + ")";
    }

    /**
     * Creates a new collection with the alias "New Collection (n)",
     * where n is the smallest number so that the name is still unique.
     * Adds it to the provided list of collections.
     *
     * @param currentCollections The current list of collections
     * @return The index of the newly created collection
     */
    public int createNewCollection(List<Collection> currentCollections) {
        if (currentCollections == null) return -1; // Prevent NullPointerException
        return createNewCollection(currentCollections,
                generateAlias(currentCollections
                        .stream()
                        .map(Collection::getAlias)
                        .toList()
        ));
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
        if (currentCollections == null) return -1; // Prevent NullPointerException
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
