package server;

import java.net.*;

/**
 * A UDP-based server discovery service that allows clients to automatically
 * discover the IP address of a running server on the network.
 */
public class ServerDiscoveryService implements Runnable {
    /** The UDP port number used for server discovery communications. */
    private static final int DISCOVERY_PORT = 8888;
/** The expected message content that triggers a discovery response.*/
    private static final String DISCOVERY_REQUEST = "DISCOVER_SERVER";


    /**
     * Runs the UDP discovery service in a continuous loop.
     * @throws SecurityException if a security manager exists and doesn't allow the socket to be created or network operations.
     */
    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            byte[] buf = new byte[256];
            System.out.println("UDP Discovery Server started on port " + DISCOVERY_PORT);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());
                if (DISCOVERY_REQUEST.equals(msg)) {
                    String serverIP = InetAddress.getLocalHost().getHostAddress();
                    byte[] responseBytes = serverIP.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseBytes,
                            responseBytes.length,
                            packet.getAddress(),
                            packet.getPort());
                    socket.send(responsePacket);
                    System.out.println("Responded to discovery request from " + packet.getAddress());
                }
            }
        } catch (Exception e) {
            System.err.println("UDP Discovery Server error: " + e);
            e.printStackTrace();
        }
    }
}