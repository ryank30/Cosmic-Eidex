package controllers;

import account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {
    @BeforeEach
    void setUp() {
        AccountManager.setAccount(null);
    }

    @Test
    void testGetAccount_WhenNotSet_ReturnsNull() {
        assertNull(AccountManager.getAccount());
    }

    @Test
    void testSetAndGetAccount() {
        Account testAccount = new Account("testUser", "testPass");
        AccountManager.setAccount(testAccount);

        assertEquals(testAccount, AccountManager.getAccount());
    }



}
