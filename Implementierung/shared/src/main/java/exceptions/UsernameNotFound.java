package exceptions;

import java.rmi.RemoteException;

/**
 * Exception thrown when a username is not found during login.
 */
public class UsernameNotFound extends RemoteException {
    public UsernameNotFound(String message){
        super(message);
    }
}
