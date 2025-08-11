package server.implTest;
import chat.ChatNachricht;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerContext;
import server.impl.ChatserviceImpl;
import server.impl.SpielverwaltungImpl;

import java.rmi.RemoteException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ChatserviceImplTest {
    ChatserviceImpl testChat;
    ServerContext context;

    @BeforeEach
    void setup() throws RemoteException{
        context = new ServerContext();
        testChat = new ChatserviceImpl(context);
    }
    @Test
    void testPost_message() {
        testChat.post_message("room", "msg", "sender");
        assertEquals(1, testChat.get_messages("room").size());
    }
    @Test
    void testGet_messages() {
        testChat.post_message("room", "msg", "sender");
        List<ChatNachricht> result = testChat.get_messages("room");
        assertEquals(1, result.size());

        List<ChatNachricht> empty = testChat.get_messages("empty");
        assertEquals(0, empty.size());
    }
    @Test
    void testUpdate_chat() throws Exception {
        String result = testChat.update_chat();
        assertEquals("", result);
    }


    @Test
    void testSendChatMessage() throws RemoteException {
        testChat.sendChatMessage("user", "message");
        assertEquals(1, testChat.getChatHistory().size());
    }

    @Test
    void testGetChatHistory() throws RemoteException {
        testChat.sendChatMessage("user", "msg");
        List<ChatNachricht> result = testChat.getChatHistory();
        assertNotNull(result);
    }
    


}
