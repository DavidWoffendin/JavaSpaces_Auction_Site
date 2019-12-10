package com.zackehh.util;

import com.zackehh.auction.IWsUser;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.testng.Assert.*;

public class UserUtilsTest {

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testUnsupportedConstructor() throws Exception {
        Constructor constructor = UserUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGetAndSetUser() throws Exception {
        IWsUser user1 = UserUtils.setCurrentUser("Test User");

        assertEquals(user1.getId(), "Test User");

        IWsUser user2 = UserUtils.getCurrentUser();

        assertEquals(user2.getId(), "Test User");
    }

}
