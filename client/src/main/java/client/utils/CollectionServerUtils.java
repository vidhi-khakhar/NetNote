package client.utils;

import jakarta.ws.rs.client.ClientBuilder;

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
    /**
     * Method to check if an address corresponds to an available server.
     * The server must send a 200 OK response containing JSON when a
     * GET request is made to /api/notes for it to be considered available.
     *
     * @param address The address of the server to try to connect to.
     * @return {@code true} if the server is reachable and valid and
     * {@code false} otherwise
     */
    public boolean isServerAvailable(String address) {
        try {
            var response = ClientBuilder.newClient()
                    .target(address)
                    .request()
                    .get();
            return response.getStatus() == 200;
        } catch (Exception e) {
            return false;
        }
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
