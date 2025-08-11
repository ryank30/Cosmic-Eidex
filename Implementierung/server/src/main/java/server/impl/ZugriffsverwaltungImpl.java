package server.impl;

import server.AccountStorage;
import server.ServerContext;
import api.Zugriffsverwaltung;
import account.Account;
import exceptions.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

public class ZugriffsverwaltungImpl extends UnicastRemoteObject implements Zugriffsverwaltung {

    private final ServerContext context;

    public ZugriffsverwaltungImpl(ServerContext context) throws RemoteException {
        this.context = context;
    }

    /**
     * Registers a new user account with the provided credentials.
     *
     * @param username        the desired username
     * @param password        the desired password
     * @param repeat_password repeated password for confirmation
     * @throws RemoteException          if RMI communication fails
     * @throws UsernameNotAvailable     if the username is already taken
     * @throws PasswordsDoNotMatch      if the passwords do not match
     * @throws IllegalArgumentException if any input field is empty or null
     */
    @Override
    public synchronized void register(String username, String password, String repeat_password)
            throws IOException {
        if (username.isEmpty() || password == null || password.isEmpty() || repeat_password.isEmpty()) {
            throw new IllegalArgumentException("Username or Password can not be empty!");
        }
        for (Account account : this.context.accounts) {
            if (account.get_username().equals(username)) {
                throw new UsernameNotAvailable("Username not available:" + username);
            }
        }
        if (!password.equals(repeat_password)) {
                throw new PasswordsDoNotMatch("Passwords do not match!");
        }
        Account new_Account = new Account(username, password);
        this.context.accounts.add(new_Account);
        AccountStorage.saveAccounts(this.context.accounts);
    }

    /**
     * Logs in a user by checking their credentials.
     *
     * @param username the username
     * @param password the password
     * @throws RemoteException if RMI communication fails
     * @throws WrongPassword if the password is incorrect
     * @throws UsernameNotFound if the username does not exist
     */
    @Override
    public synchronized void login(String username, String password)
            throws RemoteException, WrongPassword, UsernameNotFound {
        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username or Password can not be empty!");
        }
        for (Account account : this.context.accounts) {
            if (account.get_username().equals(username)) {
                if (account.get_password().equals(password)) {
                    this.context.logged_in.add(account);
                    return;
                } else {
                    throw new WrongPassword("Entered wrong password!");
                }
            }
        }
        throw new UsernameNotFound("Username not found!");
    }

    /**
     * Logs out the user with the given username.
     *
     * @param username the username to log out
     * @throws RemoteException if RMI communication fails
     */
    @Override
    public synchronized void logout(String username) throws RemoteException {
        for (Account account : this.context.accounts) {
            if (account.get_username().equals(username)) {
                this.context.logged_in.remove(account);
            }
        }
    }

    /**
     * Edits the account by updating username and/or password.
     *
     * @param current the current account
     * @param new_username the new username
     * @param new_password the new password
     * @throws Exception for RMI or internal errors
     * @throws UseDifferentPassword if the new password is the same as the old one
     */
    @Override
    public synchronized void edit_account(Account current, String new_username, String new_password)
            throws Exception, UseDifferentPassword {

        int acc_index = -1;
        int counter = 0;

        for (Account account : this.context.accounts) {
            if (!new_username.equals(current.get_username())) {
                if (account.get_username().equals(new_username)) {
                    throw new UsernameNotAvailable("Username not available:" + new_username);
                }
            }

            if (account.get_username().equals(current.get_username())){
                acc_index = counter;
            }
            counter++;
        }
        current.set_username(new_username);
        if (new_password.equals(current.get_password())) {
            throw new UseDifferentPassword("Use a different password.");
        } else {
            current.set_password(new_password);
        }

        if (acc_index != -1){
            this.context.accounts.set(acc_index, current);
        }
    }

    /**
     * Deletes the user account if credentials match.
     *
     * @param username the username of the account to delete
     * @param password the password for verification
     * @throws RemoteException if RMI communication fails
     * @throws WrongPassword if the password is incorrect
     */
    @Override
    public synchronized void delete_account(String username, String password) throws RemoteException, WrongPassword {
        Iterator<Account> iterator = this.context.accounts.iterator();
        while (iterator.hasNext()) {
            Account account = iterator.next();
            System.out.println(account.get_username());
            if (account.get_username().equals(username)) {
                if (!account.get_password().equals(password)) {
                    throw new WrongPassword("Wrong Password!");
                } else {
                    iterator.remove();
                    this.context.logged_in.remove(account);
                    return;
                }
            }
        }
        System.out.println("account doesn't exist");
    }
}