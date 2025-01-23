package client.utils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class ServerUtils {

    /**
     * Getter for the local address of the current device in the network.
     *
     * @return The local address of the current device.
     */
    public String getLocalAddress() {
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
        try (
                Client client = ClientBuilder
                        .newBuilder()
                        .withConfig(new ClientConfig())
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build()
        ) {
            if (client
                    .target(address + "api/notes/")
                    .request(MediaType.APPLICATION_JSON)
                    .get()
                    .getStatus() != 200) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
