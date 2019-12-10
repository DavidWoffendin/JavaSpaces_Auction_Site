package com.zackehh.auction;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class IWsUserTest {

    @Test
    public void testIWsUserInitializations() throws Exception {
        String id = "Test";

        IWsUser user1 = new IWsUser(id);
        IWsUser user2 = new IWsUser(id + 1);

        assertEquals(user1.getId(), id);
        assertFalse(user1.equals(user2));
    }

    @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "EqualsWithItself", "ObjectEqualsNull"})
    @Test
    public void testIWsUserEquals() throws Exception {
        IWsUser user1 = new IWsUser("Test");

        assertTrue(user1.equals(new IWsUser("Test")));
        assertTrue(user1.equals(user1));

        assertFalse(user1.equals(new IWsUser("Tester")));
        assertFalse(user1.equals(1));
        assertFalse(user1.equals(null));
    }

}
