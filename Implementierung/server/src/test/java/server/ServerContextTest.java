package server;

import api.Raumverwaltung;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerContextTest {

    private ServerContext context;
    private Registry mockRegistry;
    private Raumverwaltung raumService;



    @BeforeEach
    void setUp() {

        context = new ServerContext();
        mockRegistry = mock(Registry.class);
        raumService = mock(Raumverwaltung.class);

    }

    @Test
    void testStartRegistry() {
        context.setRegistry(mockRegistry);
        assertEquals(mockRegistry, context.getRegistry());
    }

    @Test
    void testBotRoomCleanup() {

        assertDoesNotThrow(() -> context.startBotRoomCleanup());
    }
}
