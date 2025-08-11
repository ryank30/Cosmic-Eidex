package api;

import chat.ChatNachricht;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for chat-related functionalities.
 */
public interface Chatservice extends Remote {

    /**
     * Posts a new message to a specified chat room.
     *
     * @param room the name of the chat room
     * @param message the content of the message
     * @param sender the sender's username
     * @throws Exception if the message could not be posted
     */
    void post_message(String room, String message, String sender) throws Exception;

    /**
     * Retrieves a list of messages from a specified chat room.
     *
     * @param room the name of the chat room
     * @return a list of chat messages from the room
     * @throws RemoteException if a remote communication error occurs
     */
    List<ChatNachricht> get_messages(String room) throws RemoteException;

    /**
     * Updates or synchronizes the chat state.
     *
     * @return a string indicating the update status
     * @throws Exception if the update operation fails
     */
    String update_chat() throws Exception;
}
