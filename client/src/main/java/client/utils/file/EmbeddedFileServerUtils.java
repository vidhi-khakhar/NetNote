package client.utils.file;


import commons.EmbeddedFile;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.glassfish.jersey.client.ClientConfig;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A client utility class for communicating with the server for embedded files.
 */
public class EmbeddedFileServerUtils {

    /**
     * Method to check if an address corresponds to an available server.
     * The server must send a 200 OK response containing JSON when a
     * GET request is made to /api/files/ for it to be considered available.
     *
     * @param address The address of the server to try to connect to.
     * @return {@code true} if the server is reachable and valid and
     * {@code false} otherwise
     */
    public boolean isServerAvailable(String address) {
        try {
            if (ClientBuilder.newClient(new ClientConfig())
                    .target(address + "api/files/")
                    .request(MediaType.APPLICATION_JSON)
                    .get()
                    .getStatus() != 200) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * This should retrieve the files based upon the note name.
     * @param serverAddress - The server address because some files
     *                      may be stored on different servers.
     * @param noteName - the note name which is used to correlate files to the correct note
     * @return - a list of all files if the server responds with an HTTP status code 200
     * and an empty list if the request files.
     */
    public List<EmbeddedFile> getFilesFromNote(String serverAddress, String noteName) {
        List<EmbeddedFile> files = new ArrayList<>();
        noteName = URLEncoder.encode(noteName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress + "api/files/notes/" + noteName)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                files = response.readEntity(new GenericType<List<EmbeddedFile>>() {});
            } else {
                System.out.println("Failed to retrieve files for note." +
                        "Status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }


    /**
     * Sends a POST request to store a file on the server.
     * The method uses the REST API endpoint at "api/files/" to store the provided note.
     *
     * @param serverAddress - The server address because some files m
     *                      ay be stored on different servers.
     * @param noteName - used to match embedded files with the correct note based on key: noteName
     * @param file - the embedded file.
     * @return sm
     */
    public boolean storeEmbeddedFile(String serverAddress, String noteName, EmbeddedFile file) {
        noteName = URLEncoder.encode(noteName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress + "api/files/" + noteName)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(file, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 201) {
                System.out.println("Embedded file stored successfully.");
                return true;
            } else {
                System.out.println("Failed to store embedded file." +
                        "Status: " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * Sends a PATCH request to update the title of an existing embedded file on the server.
     * @param serverAddress - The server address because some files
     *                      may be stored on different servers.
     * @param noteName  - The name of the note
     * @param oldTitle    - The current title of the embedded file to be updated.
     * @param newTitle    - The new title to be assigned to the embedded file.
     */
    public void renameEmbeddedFile(String serverAddress,String noteName,
                                   String oldTitle, String newTitle) {
        oldTitle = URLEncoder.encode(oldTitle, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress)
                    .path("api/files/" + noteName + "/content/" + oldTitle)
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-HTTP-Method-Override", "PATCH")
                    .post(Entity.entity(newTitle, MediaType.APPLICATION_JSON));

            // Handle the response
            if (response.getStatus() == 200) {
                System.out.println("Embedded file successfully renamed.");
            } else {
                System.out.println("Failed to update embedded file title." +
                        "Status: " + response.getStatus());
                System.out.println("Response message: " + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.err.println("An error occurred while renaming file: " + e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * Sends an HTTP DELETE request to the server to remove
     * the embedded file with the given title.
     * @param serverAddress - The server address because some files
     *                        may be stored on different servers.
     * @param noteName - Name of the note selected
     * @param fileName - Name of the file to be deleted.
     */
    public void deleteEmbeddedFile(String serverAddress, String noteName, String fileName) {
        noteName = URLEncoder.encode(noteName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress + "api/files/" + noteName + "/content/" + fileName)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            if (response.getStatus() == 200) {
                System.out.println("Embedded file deleted successfully.");
            } else {
                System.out.println("Failed to delete embedded file." +
                        "Status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

