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
     * Creates a new collection with the name "New Collection (n)",
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
        currentCollections.add(new Collection("", newCollectionAlias, ""));
        return currentCollections.size() - 1;
    }

    /**
     * Removes the last collection in a list of collections
     * @param currentCollections The current list of collections
     * @param currentIndex The index of the currently selected collection
     * @return The index of the selected collection after the deletion
     */
    public int removeLastCollection(List<Collection> currentCollections, int currentIndex) {
        currentCollections.removeLast();
        if (currentIndex == currentCollections.size())
            currentIndex--;
        return currentIndex;
    }
}
