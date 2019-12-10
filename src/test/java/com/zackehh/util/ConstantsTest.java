package com.zackehh.util;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstantsTest {

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testUnsupportedConstructor() throws Exception {
        Constructor constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}
