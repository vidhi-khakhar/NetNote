package client.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class CollectionServerUtils {

    private String serverAddress;

    public CollectionServerUtils() {this.serverAddress = getLocalAddress();}

    public String getServerAddress() {return serverAddress;}

    public void setServerAddress(String serverAddress) {this.serverAddress = serverAddress;}

    /**
     * Getter for the local address of the current device in the network.
     * @return The local address of the current device.
     */
    public static String getLocalAddress(){
        try {
            return "http://" + InetAddress.getLocalHost().getHostAddress() + ":8080/";
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            return "";
        }
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
