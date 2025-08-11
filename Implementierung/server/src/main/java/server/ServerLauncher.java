package server;

import api.Spielverwaltung;
import api.Raumverwaltung;
import api.Zugriffsverwaltung;
import api.Chatservice;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import server.impl.ChatserviceImpl;
import server.impl.RaumverwaltungImpl;
import server.impl.SpielverwaltungImpl;
import server.impl.ZugriffsverwaltungImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;

/**
 * Main server launcher that initializes all game server components.
 */
public class ServerLauncher {
    /**
     * Initializes RMI registry, binds all services, and starts discovery service.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            //start udp
            String localIP = getLocalIP();

            System.setProperty("java.rmi.server.hostname", localIP);
            System.out.println("RMI server hostname set to: " + localIP);
            new Thread(new ServerDiscoveryService()).start();

            //rmi sevices
            ServerContext context = new ServerContext();
            context.startHeartbeatCleanup();
            context.startBotRoomCleanup();
            context.setAccounts(AccountStorage.loadAccounts());
            Zugriffsverwaltung zugriff = new ZugriffsverwaltungImpl(context);
            Raumverwaltung raum = new RaumverwaltungImpl(context);
            Chatservice chat = new ChatserviceImpl(context);
            Spielverwaltung spiel = new SpielverwaltungImpl(context);
            context.setRaumService(raum);

            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            context.setRegistry(registry);

            registry.bind("AccountServer", zugriff);
            registry.bind("RoomServer", raum);
            registry.bind("ChatServer", chat);
            registry.bind("GameServer", spiel);

            System.out.println("Server ready");
        } catch (Exception e) {
            System.err.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    /**
     * Finds the first available IPv4 LAN address
     * @return local IPv4 address as string.
     * @throws SocketException if the network interface access fails.
     * @throws RuntimeException if no suitable IP found.
     */
    private static String getLocalIP() throws SocketException {
        for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (iface.isLoopback() || !iface.isUp()) continue;
            for (InetAddress addr : Collections.list(iface.getInetAddresses())) {
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return addr.getHostAddress();
                }
            }
        }
        throw new RuntimeException("No suitable LAN IP address found");
    }

    /**
     * Starts HTTP server serving the given IP address on port 8000.
     * Responds with plain text IP at /ip endpoint.
     * @param ip the IP address to serve.
     * @throws IOException if server creation fails.
     */
    static void startIPServer(String ip) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/ip", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = ip.getBytes();
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        });
        server.setExecutor(null); // default executor
        server.start();
        System.out.println("IP server running on http://0.0.0.0:8000/ip");
    }

}