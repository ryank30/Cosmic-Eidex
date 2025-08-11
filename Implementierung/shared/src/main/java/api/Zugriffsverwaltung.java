package api;

import account.Account;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * Remote interface for managing user access such as registration, login,
 * logout, account editing, and deletion.
 */
public interface Zugriffsverwaltung extends Remote {

    /**
     * Registers a new user account.
     *
     * @param username the desired username
     * @param password the password to set
     * @param repeat_password the repeated password for confirmation
     * @throws RemoteException if a remote communication error occurs
     */
    void register(String username, String password, String repeat_password) throws IOException;

    /**
     * Logs a user into their account.
     *
     * @param username the username to log in
     * @param password the corresponding password
     * @throws RemoteException if a remote communication error occurs
     */
    void login(String username, String password) throws RemoteException;

    /**
     * Logs a user out of the system.
     *
     * @param username the username to log out
     * @throws RemoteException if a remote communication error occurs
     */
    void logout(String username) throws RemoteException;

    /**
     * Edits account details such as username or password.
     *
     * @param current the current account
     * @param new_username the new username to set
     * @param new_password the new password to set
     * @throws Exception if the operation fails (e.g. password mismatch or unavailable username)
     */
    void edit_account(Account current, String new_username, String new_password) throws Exception;

    /**
     * Deletes the specified user account.
     *
     * @param username the username of the account to delete
     * @param password the password to verify before deletion
     * @throws RemoteException if a remote communication error occurs
     */
    void delete_account(String username, String password) throws RemoteException;
}
