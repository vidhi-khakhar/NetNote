package client.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerUtilsTest {

    private ServerUtils sut = new ServerUtils();

    @Test
    void cannotConnectToServerWithInvalidAddressTest() {
        String serverAddress = "test://test.test:test/";
        assertFalse(sut.isServerAvailable(serverAddress));
    }

    @Test
    void cannotConnectToServerWithIncompatibleAPITest() {
        String serverAddress = "https://www.google.com/";
        assertFalse(sut.isServerAvailable(serverAddress));
    }
}
