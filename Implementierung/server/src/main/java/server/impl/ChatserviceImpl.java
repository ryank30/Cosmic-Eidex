package server.impl;

import server.ServerContext;
import chat.ChatNachricht;
import api.Chatservice;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatserviceImpl extends UnicastRemoteObject implements Chatservice{

    private final ServerContext context;

    public ChatserviceImpl(ServerContext context) throws RemoteException {
        this.context = context;
    }

    /**
     * Posts a chat message to a specific room.
     *
     * @param room the name of the room
     * @param message the chat message content
     * @param sender the sender of the message
     */
    @Override
    public synchronized void post_message(String room, String message, String sender) {
        context.roomMessages.putIfAbsent(room, new ArrayList<>());
        context.roomMessages.get(room).add(new ChatNachricht(sender, message));
    }

    /**
     * Retrieves all messages from the specified room.
     *
     * @param room the room name
     * @return list of chat messages in that room
     */
    @Override
    public synchronized List<ChatNachricht> get_messages(String room) {
        return new ArrayList<>(context.roomMessages.getOrDefault(room, Collections.emptyList()));
    }

    /**
     * Updates the current lobby chat content.
     *
     * @return current lobby chat as a string
     * @throws Exception if internal error occurs
     */
    @Override
    public String update_chat() throws Exception {
        return this.context.lobby_chat;
    }

    /**
     * Sends a chat message from a user and appends it to the global chat history.
     *
     * @param sender  the username of the message sender
     * @param message the content of the message
     * @throws RemoteException if RMI communication fails
     */
    public synchronized void sendChatMessage(String sender, String message) throws RemoteException {
        ChatNachricht chat = new ChatNachricht(sender, message);
        context.chatHistory.add(chat);
    }

    /**
     * Retrieves the entire chat history of the lobby.
     *
     * @return a list of chat messages
     * @throws RemoteException if RMI communication fails
     */
    public synchronized List<ChatNachricht> getChatHistory() throws RemoteException {
        return new ArrayList<>(context.chatHistory); // return a copy
    }

}
