package client.utils;

import commons.Collection;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CollectionServerUtils {

    private String serverAddress;

    /**
     * Default constructor for NoteServerUtils used by Guice
     * Sets the default server address to the address of the current device.
     */
    public CollectionServerUtils() {this.serverAddress = getLocalAddress();}

    /**
     * Getter for the server address
     * @return Getter for the server address
     */
    public String getServerAddress() {return serverAddress;}

    /**
     * Setter for the server address
     * @param serverAddress The new server address
     */
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

    /**
     * This method sends a GET request to the "api/collections/" endpoint to fetch all notes.
     *
     * @return a list of all collections if the server responds with an HTTP status code 200
     * and an empty list if the request fails
     */
    public List<commons.Collection> getAllCollections() {
        List<commons.Collection> collections = new ArrayList<Collection>();
        try {
            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                collections = response.readEntity(new GenericType<List<Collection>>() {
                });
            }
            else System.out.println(response.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return collections;
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
