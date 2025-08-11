package server.implTest;

import account.Account;
import exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import server.ServerContext;
import server.impl.SpielverwaltungImpl;
import server.impl.ZugriffsverwaltungImpl;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZugriffsverwaltungImplTest {

    ServerContext context;
    ZugriffsverwaltungImpl testAccess;
    @BeforeEach
    void setup() throws RemoteException{
        context = new ServerContext();
        context.accounts = new ArrayList<>();
        context.logged_in = new ArrayList<>();
        testAccess = new ZugriffsverwaltungImpl(context);
    }



    @Test
    void testRegister() throws IOException {

        assertThrows(IllegalArgumentException.class, () ->
                testAccess.register("", "pass", "pass"));

        assertThrows(IllegalArgumentException.class, () ->
                testAccess.register("user", null, "pass"));

        assertThrows(IllegalArgumentException.class, () ->
                testAccess.register("user", "", ""));

        assertThrows(IllegalArgumentException.class, () ->
                testAccess.register("user", "pass", ""));

        testAccess.register("user1", "pass", "pass");

        assertThrows(UsernameNotAvailable.class, () ->
                testAccess.register("user1", "pass2", "pass2"));
    }

    @Test
    void testLogin() throws RemoteException, WrongPassword, UsernameNotFound {

        assertThrows(IllegalArgumentException.class, () ->
                testAccess.login("", "pass"));

        assertThrows(IllegalArgumentException.class, () ->
                testAccess.login("user", ""));

        context.accounts.add(new Account("user", "pass"));

        testAccess.login("user", "pass");

        assertThrows(WrongPassword.class, () ->
                testAccess.login("user", "wrong"));

        assertThrows(UsernameNotFound.class, () ->
                testAccess.login("notfound", "pass"));
    }

    @Test
    void testLogout() throws RemoteException {
        Account acc = new Account("user", "pass");
        context.accounts.add(acc);
        context.logged_in.add(acc);

        testAccess.logout("user");
        assertEquals(0, context.logged_in.size());
    }

    @Test
    void testEdit_account() throws Exception {
        Account acc = new Account("user", "pass");
        context.accounts.add(acc);

        context.accounts.add(new Account("existing", "pass2"));
        assertThrows(UsernameNotAvailable.class, () ->
                testAccess.edit_account(acc, "existing", "newpass"));

        assertThrows(UseDifferentPassword.class, () ->
                    testAccess.edit_account(acc, "newuser", "pass"));

        testAccess.edit_account(acc,"newuser", "newpass");
    }

    @Test
    void testDelete_account() throws RemoteException, WrongPassword {
        Account acc = new Account("user", "pass");
        context.accounts.add(acc);
        context.logged_in.add(acc);

        assertThrows(WrongPassword.class, () ->
                testAccess.delete_account("user", "wrong"));

        testAccess.delete_account("user", "pass");
        assertEquals(0, context.accounts.size());
    }

}
