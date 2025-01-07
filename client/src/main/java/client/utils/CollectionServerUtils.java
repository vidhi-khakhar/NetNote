package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionServerUtils {

    private String serverAddress;
    private final ObjectMapper mapper;

    /**
     * Default constructor for CollectionServerUtils.
     * Sets the default server address to the address of the current device.
     */
    public CollectionServerUtils(ObjectMapper mapper) {
        this.serverAddress = getLocalAddress();
        this.mapper = mapper;
    }

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
     * This method sends a GET request to the "api/collections/" endpoint to fetch all notes.
     *
     * @return a list of all collections if the server responds with an HTTP status code 200
     * and an empty list if the request fails
     */
    public List<Collection> getAllCollections() {
        List<Collection> collections = new ArrayList<>();
        try {
            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                collections = response.readEntity(new GenericType<List<Collection>>() {
                });
            }
            else {
                System.out.println("Failed to retrieve collections." +
                        " Status: " + response.getStatus());
            }
        } catch (Exception e) {
            System.out.println("Error occurred while fetching collections: " + e.getMessage());
            e.printStackTrace();
        }
        return collections;
    }

    /**
     * Sends a request to create a new collection on the server.
     * Sends a PUT request with the Collection object as the request body.
     *
     * @param collection The collection to created and stored on the server.
     */
    public void createCollection(Collection collection) {
        try {
            String jsonData = mapper.writeValueAsString(collection);

            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/")
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(jsonData, MediaType.APPLICATION_JSON));
            if (response.getStatus() == 201) {
                System.out.println("collection created");
            } else {
                System.out.println("Failed to create collection. Status:" + response.getStatus());
                System.out.println("Response message:" + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println("Error occurred while creating collection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the alias of an existing collection.
     * Sends a POST request to the "api/collections/{uniqueName}" endpoint
     * with the new alias.
     *
     * @param uniqueName The unique name of the collection.
     * @param newAlias The new alias.
     */
    public void updateCollectionAlias(String uniqueName, String newAlias) {
        try {
            var updatedCollection = new Collection();
            updatedCollection.setAlias(newAlias);

            String jsonData = mapper.writeValueAsString(updatedCollection);

            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/" + uniqueName)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(jsonData, MediaType.APPLICATION_JSON));
            if (response.getStatus() == 200) {
                System.out.println("Collection alias updated successfully.");
            } else {
                System.out.println("Failed to update collection alias." +
                        " Status: " + response.getStatus());
                System.out.println("Response message: " + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println("Error occurred while updating collection alias: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a collection by its unique name.
     * Sends a DELETE request to the "api/collections/{uniqueName} endpoint.
     * @param uniqueName The uniqueName of the collection to delete.
     */
    public void deleteCollection(String uniqueName) {
        try {
            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/" + uniqueName)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();
            if (response.getStatus() == 200) {
                System.out.println("Collection deleted successfully.");
            } else {
                System.out.println("Failed to delete collection. Status: " + response.getStatus());
                System.out.println("Response message: " + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println("Error occurred while deleting collection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Adds a note to a specified collection.
     * Sends a POST request to the "api/collections/{uniqueName}/notes/{noteTitle}" endpoint.
     *
     * @param uniqueName The unique name of the collection.
     * @param noteTitle The title of the note to added.
     */
    public void addNoteToCollection(String uniqueName, String noteTitle) {
        try {
            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/" + uniqueName + "/notes/" + noteTitle)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
            if (response.getStatus() == 200) {
                System.out.println("Note added to collection successfully.");
            } else {
                System.out.println("Failed to add note to collection." +
                        " Status: " + response.getStatus());
                System.out.println("Response message: " + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println("Error occurred while adding note to collection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Moves a note from one collection to another.
     * Sends a POST request to the
     * "api/collections/{uniqueName}/notes/{noteTitle}/move/{targetUniqueName}" endpoint.
     *
     * @param uniqueName The unique name of the source collection.
     * @param noteTitle The title of the note to move.
     * @param targetUniqueName The unique name of the target collection.
     */
    public void moveNoteToAnotherCollection
    (String uniqueName, String noteTitle, String targetUniqueName) {
        try {
            var response = ClientBuilder.newClient()
                    .target(serverAddress + "api/collections/"
                            + uniqueName + "/notes/"
                            + noteTitle + "/move/"
                            + targetUniqueName)
                    .request(MediaType.APPLICATION_JSON)
                    .post(null);
            if (response.getStatus() == 200) {
                System.out.println("Note moved to another collection successfully.");
            } else {
                System.out.println("Failed to move note." +
                        " Status: " + response.getStatus());
                System.out.println("Response message: " + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println("Error occurred while moving note to another collection: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}
