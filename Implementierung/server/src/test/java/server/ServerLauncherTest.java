package server;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerLauncherTest {

    private static final int PORT = 8000;
    private static final String ENDPOINT = "http://localhost:" + PORT + "/ip";
    private static HttpServer server;
    private ServerLauncher launcher;
    @BeforeEach
    public void setup() throws Exception {
        launcher.startIPServer("192.168.100.200");
        Thread.sleep(200);
    }
    @AfterEach
    public void teardown() {
        if (launcher != null) {
            server.stop(0);
        }
    }

    @Test
    void testServerStartupDoesNotThrow() {
        assertDoesNotThrow(() -> ServerLauncher.main(new String[]{}));
    }
}
