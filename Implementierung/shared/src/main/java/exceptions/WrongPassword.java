package exceptions;

import java.rmi.RemoteException;

/**
 * Exception thrown when a user enters an incorrect password during login or authentication.
 */
public class WrongPassword extends RemoteException {
    public WrongPassword(String message){
        super(message);
    }
}
