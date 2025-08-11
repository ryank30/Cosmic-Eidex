package util;

import api.Raumverwaltung;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility class for managing app.client heartbeats to the server.
 * Periodically notifies the server that a app.client is still active.
 */
public class HeartbeatManager {
    private static Timer heartbeatTimer;

    /**
     * Starts the heartbeat process that sends periodic signals to the server.
     *
     * @param raum the remote Raumverwaltung instance
     * @param username the username to identify the app.client
     */
    public static void start(Raumverwaltung raum, String username) {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
        }

        heartbeatTimer = new Timer(true);
        heartbeatTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    raum.receiveHeartbeat(username);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 3000);
    }

    /**
     * Stops the heartbeat timer.
     * No further heartbeat signals will be sent.
     */
    public static void stop() {
        if (heartbeatTimer != null) {
            heartbeatTimer.cancel();
            heartbeatTimer = null;
        }
    }
}
