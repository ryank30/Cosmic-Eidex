package exceptions;

import java.rmi.RemoteException;

/**
 * Exception thrown when the chosen username is already taken during registration.
 */
public class UsernameNotAvailable extends RemoteException {
    public UsernameNotAvailable(String message){
        super(message);
    }
}
