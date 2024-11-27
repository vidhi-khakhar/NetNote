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
package client.utils;

import java.util.concurrent.TimeUnit;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * A client utility class for making HTTP GET requests to a REST API.
 * The class is responsible for the requests from the client side to the server.
 */
public class APIrequestHandler {

    private static final String SERVER = "http://localhost:8080/";
    private static final int TIMEOUT = 5000;
    private final Client client;

    /**
     * Initializes a new instance of the APIrequestHandler class -
     * a new Client with the configured timeouts.
     */
    public APIrequestHandler() {
        client = ClientBuilder.newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * Makes a GET request to a specified API endpoint and returns the response.
     * @param endpoint - endpoint path
     * @return response as a String if the request is successful
     * and error message if the status code is not 200 or if an exception occurs.
     */
    public String makeApiRequest(String endpoint) {
        try {
            WebTarget target = client.target(SERVER + endpoint); // append endpoint to base URL
            Response response = target.request().get();  // make GET request to the API endpoint

            if (response.getStatus() == 200) {
                return response.readEntity(String.class);  // return the response as a string
            } else {
                return "Error: status code " + response.getStatus();
            }
        } catch (Exception e) {
            return "Error during API request: " + e.getMessage();
        }
    }

    /**
     * Closes the client instance to release any resources used by the client.
     */
    public void close() {
        client.close();
    }
}
