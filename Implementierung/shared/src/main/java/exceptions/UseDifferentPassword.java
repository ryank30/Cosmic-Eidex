package exceptions;

import java.rmi.RemoteException;

/**
 * Exception thrown when the new password is the same as the old one.
 */
public class UseDifferentPassword extends RemoteException {
    public UseDifferentPassword(String message){
        super(message);
    }
}
