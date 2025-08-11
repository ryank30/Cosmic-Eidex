package app;

import java.net.*;
import java.util.Collections;

/**
 * This class implements a network discovery that allows client locate available servers.
 */
public class ServerDiscoveryClient {
    /**
     * The UDP port number used for server discovery communication.
     */
    private static final int DISCOVERY_PORT = 8888;
    /**
     * It a broadcast across the network to identify discovery requests. servers listening will respond with their IP addresses.
     */
    private static final String DISCOVERY_REQUEST = "DISCOVER_SERVER";

    /**
     * Discovers and returns the IP address of an available server on the network.
     * @return the IP address of the discovered  server a String.
     */
    public static String discoverServerIP() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] sendData = DISCOVERY_REQUEST.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length,
                    InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT);
            socket.send(sendPacket);

            // interface specific broadcast addresses
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ni.isLoopback() || !ni.isUp()) continue;
                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        sendPacket = new DatagramPacket(
                                sendData, sendData.length,
                                broadcast, DISCOVERY_PORT);
                        socket.send(sendPacket);
                    }
                }
            }
             // Prepare to receive server response
            byte[] buf = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
            // Set timeout to prevent indefinite blocking
            socket.setSoTimeout(3000);
            socket.receive(receivePacket);
            String serverIP = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Discovered server at IP: " + serverIP);
            return serverIP;

        } catch (Exception e) {
            //Handle all exceptions (SocketException, IOException, SocketTimeoutException, etc.)
            System.err.println("UDP discovery failed: " + e.getMessage());
            return null;
        }
    }
}