package chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatNachrichtTest {

    private ChatNachricht message;

    @BeforeEach
    public void setUp() {
        message = new ChatNachricht("ilias", "vae victis");
    }

    @Test
    public void testConstructor() {
        assertEquals("ilias", message.sender, "sender should be correct");
        assertEquals("vae victis", message.content, "content should be correct");
    }

    @Test
    public void testToStringMethod() {
        String result = message.toString();
        String expected ="ilias: vae victis";

        assertEquals(expected, result, "toString should return 'sender: content'.");
    }
    @Test
    void testNullSender() {
        ChatNachricht msg = new ChatNachricht(null, "Message");
        assertNull(msg.getSender());
        assertEquals("null: Message", msg.toString());
    }

    @Test
    void testNullContent() {
        ChatNachricht msg = new ChatNachricht("ilias", null);
        assertEquals("ilias", msg.getSender());
        assertNull(msg.getContent());
        assertEquals("ilias: null", msg.toString());
    }


}