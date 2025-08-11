package chat;

import java.io.Serializable;

/**
 * Represents a chat message with a sender and message content.
 */
public class ChatNachricht implements Serializable {

    /** The name of the message sender */
    public String sender;

    /** The content of the message */
    public String content;

    /**
     * Constructs a chat message with a sender and content.
     *
     * @param sender the name of the message sender
     * @param content the text content of the message
     */
    public ChatNachricht(String sender, String content){
        this.sender = sender;
        this.content = content;
    }

    /** Getter for sender */
    public String getSender() {
        return sender;
    }

    /** Getter for content */
    public String getContent() {
        return content;
    }

    /**
     * Returns the string representation of the message.
     *
     * @return the message in the format "sender: content"
     */
    @Override
    public String toString() {
        return sender + ": " + content;
    }
}
