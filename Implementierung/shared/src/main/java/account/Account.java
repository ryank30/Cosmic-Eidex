package account;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Represents a user account with a username and password.
 */
public class Account implements Serializable {
    private String username;
    private String password;
    private int wins;

    /**
     * Constructs an account with the given username and password.
     *
     * @param username the account's username
     * @param password the account's password
     */
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.wins = 0;
    }

    /**
     * Sets the account's username.
     *
     * @param username the username to set
     */
    public void set_username(String username) {
        this.username = username;
    }

    /**
     * Returns the account's username.
     *
     * @return the current username
     */
    public String get_username() {
        return username;
    }

    /**
     * Sets the account's password.
     *
     * @param password the password to set
     */
    public void set_password(String password) {
        this.password = password;
    }

    /**
     * Returns the account's password.
     *
     * @return the current password
     */
    public String get_password() {
        return this.password;
    }

    public void set_wins(int wins) {
        this.wins = wins;
    }
    public int get_wins() {
        return this.wins;
    }
    public void add_wins() {
        this.wins += 1;
    }
}
