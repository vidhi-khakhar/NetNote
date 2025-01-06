package client.utils;

import java.util.List;

public class CollectionServerUtils {

    private String serverAddress;

    public CollectionServerUtils() {this.serverAddress = getLocalAddress();}

    public String getServerAddress() {return serverAddress;}

    public void setServerAddress(String serverAddress) {this.serverAddress = serverAddress;}

    public static String getLocalAddress() {
        return null;
    }

    public boolean isServerAvailable(String address) {
        return false;
    }

    public List<commons.Collection> getAllCollections() {
        return null;
    }

    public boolean createCollection(commons.Collection collection) {
        return false;
    }

    public boolean updateCollectionAlias(String uniqueName, String newAlias) {
        return false;
    }

    public boolean deleteCollection(String uniqueName) {
        return false;
    }

    public boolean addNoteToCollection(String uniqueName, String noteTitle) {
        return false;
    }

    public boolean moveNoteToAnotherCollection(String uniqueName, String noteTitle, String targetUniqueName) {
        return false;
    }
}
