package controllers;

import account.Account;

/**
 * A utility class for managing the currently logged-in account.
 */
public class AccountManager {

    /**
     * Private constructor to prevent instantiation.
     *
     * @throws IllegalStateException always thrown to prevent instantiation
     */
    private AccountManager() {
        throw new IllegalStateException("account Manager!");
    }

    private String username;
    private String password;

    static Account account;

    /**
     * Returns the currently logged-in account.
     *
     * @return the current account
     */
    public static Account getAccount() {
        return account;
    }

    /**
     * Sets the currently logged-in account.
     *
     * @param newAccount the account to set
     */
    public static void setAccount(Account newAccount) {
        account = newAccount;
    }
}
