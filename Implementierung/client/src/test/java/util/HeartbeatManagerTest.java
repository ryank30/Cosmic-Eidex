package util;

import api.Raumverwaltung;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.rmi.RemoteException;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

public class HeartbeatManagerTest {

    @Mock
    private Raumverwaltung fakeRoom;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHeartbeat() throws Exception {
        HeartbeatManager.start(fakeRoom, username);
        Thread.sleep(6500); //enough for 2 beats

        verify(fakeRoom, atLeast(2)).receiveHeartbeat(username);
        HeartbeatManager.stop();
    }

    @Test
    void testHeartAttack() throws Exception {
        HeartbeatManager.start(fakeRoom, username);
        Thread.sleep(1500); //2 heartbeats
        HeartbeatManager.stop();
        Thread.sleep(4000); //no more heartbeats, maybe call an ambulance
        verify(fakeRoom, atMost(2)).receiveHeartbeat(username);
    }

    @Test
    void testHeartException() throws Exception {
        Raumverwaltung fakeRoom = mock(Raumverwaltung.class);
        String username = "testUser";

        AtomicBoolean exceptionThrown = new AtomicBoolean(false);

        doAnswer(invocation -> {
            exceptionThrown.set(true);
            throw new RemoteException("Too much cholesterol");
        }).when(fakeRoom).receiveHeartbeat(username);

        HeartbeatManager.start(fakeRoom, username);

        Thread.sleep(3500);
        HeartbeatManager.stop();

        assertTrue(exceptionThrown.get());
    }

}