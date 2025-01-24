/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils.note;

import commons.Note;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;

/**
 * A client utility class for making HTTP requests to a REST API.
 * The class is responsible for the requests from the client side to the server.
 */
public class NoteServerUtils {

    /**
     * This method sends a GET request to the "api/notes/" endpoint to fetch all notes.
     *
     * @param serverAddress The address of the server to send the request to
     * @return a list of all notes if the server responds with an HTTP status code 200
     * and an empty list if the request fails
     */
    public List<Note> getAllNotes(String serverAddress) {
        List<Note> notes = new ArrayList<>();
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress + "api/notes/")
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                notes = response.readEntity(new GenericType<List<Note>>() {
                });
            } else {
                System.out.println("Failed to retrieve notes. Status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    /**
     * Sends a POST request to store a note on the server.
     * The method uses the REST API endpoint at "api/notes/" to store the provided note.
     *
     * @param note - the Note object to be stored on the server.
     * @param serverAddress The address of the server to send the request to
     */
    public void storeNote(Note note, String serverAddress) {
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress).path("api/notes/")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(note, MediaType.APPLICATION_JSON));

            if (response.getStatus() == 200) {
                System.out.println("Note stored successfully.");
            } else {
                System.out.println("Failed to store note. Status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a PATCH requestUpdates to update the details of an existing note on the server.
     *
     * @param title    - The title of the note to be updated.
     * @param noteData - A map containing the fields to update and their new values.
     *                 The keys in the map are "title" or "text", and their values
     *                 are the corresponding updated values for the note.
     * @param serverAddress The address of the server to send the request to
     */
    public void updateNote(String title, Map<String, String> noteData, String serverAddress) {
        try {
            String jsonData = new ObjectMapper()
                    .writeValueAsString(noteData);  // Convert Map to JSON

            // Build the Jersey client
            Client client = ClientBuilder.newClient();

            title = URLEncoder.encode(title, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            // Perform the POST request with the X-HTTP-Method-Override header
            Response response = client.target(serverAddress)
                    .path("api/notes/" + title)
                    .request(MediaType.APPLICATION_JSON)
                    .header("X-HTTP-Method-Override", "PATCH")
                    .post(Entity.entity(jsonData, MediaType.APPLICATION_JSON));

            // Handle the response
            if (response.getStatus() == 200) {
                System.out.println("Note updated successfully.");
            } else {
                System.out.println("Failed to update note. Status: " + response.getStatus());
                System.out.println("Response message: " + response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.err.println("An error occurred while updating the note: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends an HTTP DELETE request to the server to remove
     * the note with the given title.
     *
     * @param title - The title of the note to be deleted.
     * @param serverAddress The address of the server to send the request to
     */
    public void deleteNote(String title, String serverAddress) {
        title = URLEncoder.encode(title, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        try {
            Response response = ClientBuilder.newClient()
                    .target(serverAddress + "api/notes/" + title)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();

            if (response.getStatus() == 200) {
                System.out.println("Note deleted successfully.");
            } else {
                System.out.println("Failed to delete note. Status: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
