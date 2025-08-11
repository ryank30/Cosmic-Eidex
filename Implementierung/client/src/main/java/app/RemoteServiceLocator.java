package app;
import api.Chatservice;
import api.Raumverwaltung;
import api.Spielverwaltung;
import api.Zugriffsverwaltung;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Service locator for accessing remote RMI services.
 * This class provides a way to connect ti and retrieve remote services from RMI registry.
 */
public class RemoteServiceLocator {
    private static Registry registry;
    /**
     * the default RMI port number.
     */
    private static final int RMI_PORT = 1099;

    /**
     * Makes a connection to the RMI registry on the specified server.
     * @param serverIP This is the IP address of the server hosting the RMI registry.
     * @throws Exception if the connection to the RMI registry fails,also the network connectivity.
     */
    public static void connect(String serverIP) throws Exception {
        System.out.println("Connecting to RMI registry at " + serverIP + ":" + RMI_PORT);
        registry = LocateRegistry.getRegistry(serverIP, RMI_PORT);
    }

    /**
     * Retrieves the account management service from the RMI registry.
     * This service handles user authentication, authorization, and account management.
     * @return the zugriffverwaltung servive
     * @throws Exception if the service look up fails, or the Account server is not bound in the registry.
     */
    public static Zugriffsverwaltung getZugriff() throws Exception {
        return (Zugriffsverwaltung) registry.lookup("AccountServer");
    }

    /**
     * This service handles the room creation and management for the application.
     * @return the raumverwaltung management service.
     * @throws Exception if the service look up fails, or the roomserver is not found in registry.
     */
    public static Raumverwaltung getRaum() throws Exception {
        return (Raumverwaltung) registry.lookup("RoomServer");
    }

    /**
     * This service provides the chat functionality, including message sending, receiving and chat room management.
     * @return the chatservice instance.
     * @throws Exception if the service lookup fails or the chatserver is not found in the registry.
     */
    public static Chatservice getChat() throws Exception {
        return (Chatservice) registry.lookup("ChatServer");
    }

    /**
     * this service handles game creation and gameplay coordination.
     * @return the game management service instance.
     * @throws Exception if the service lookup fails, or the GameServer is not bound in the registry.
     */
    public static Spielverwaltung getSpiel() throws Exception {
        return (Spielverwaltung) registry.lookup("GameServer");
    }
}