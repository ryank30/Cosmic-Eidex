package account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("pooderpanda", "password123");
    }



    @Test
    void testSetGetUsername() {
        account.set_username("gan4dalf");
        assertEquals("gan4dalf", account.get_username(), "username should be correct");
    }

    @Test
    void testSetGetPassword() {
        account.set_password("password1");
        assertEquals("password1", account.get_password(), "password should be correct");
    }
    @Test
    void testSetGetWins() {
        account.set_wins(20);
        assertEquals(20, account.get_wins(), "wins should be correct");
    }
}