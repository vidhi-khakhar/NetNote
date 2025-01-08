package client.utils;

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
}
