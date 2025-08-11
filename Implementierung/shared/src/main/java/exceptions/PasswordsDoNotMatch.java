package exceptions;

import java.rmi.RemoteException;

/**
 * Exception thrown when the provided passwords do not match during registration.
 */
public class PasswordsDoNotMatch extends RemoteException {
    public PasswordsDoNotMatch(String message){
        super(message);
    }
}
