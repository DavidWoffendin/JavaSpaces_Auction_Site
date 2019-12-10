package com.zackehh.util;

import com.zackehh.auction.IWsBid;
import com.zackehh.auction.IWsLot;
import com.zackehh.auction.IWsUser;
import net.jini.space.JavaSpace;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.swing.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;

import static org.testng.Assert.*;

public class InterfaceUtilsTest {

    private JavaSpace space;

    private IWsLot testLot1;
    private IWsLot testLot2;
    private IWsBid testBid1;
    private IWsBid testBid2;
    private IWsBid testBid3;
    private IWsUser testUser;

    @BeforeClass
    public void setup() throws Exception {
        testUser = UserUtils.setCurrentUser("Test");
        testLot1 = new IWsLot(1, testUser, null, "Test Item 1", 0.01, "Test Description", false, false);
        testLot2 = new IWsLot(2, testUser, null, "Test Item 2", 0.01, "Test Description", false, false);

        testBid1 = new IWsBid(1, testUser, 1, 1.00, false);
        testBid2 = new IWsBid(2, testUser, 1, 2.00, false);
        testBid3 = new IWsBid(3, testUser, 1, 3.00, false);

        testLot1.history = new ArrayList<Integer>();

        testLot1.history.add(1);
        testLot1.history.add(2);
        testLot1.history.add(3);

        space = SpaceUtils.getSpace();

        space.write(testLot1, null, Constants.LOT_LEASE_TIMEOUT);
        space.write(testLot2, null, Constants.LOT_LEASE_TIMEOUT);

        space.write(testBid1, null, Constants.BID_LEASE_TIMEOUT);
        space.write(testBid2, null, Constants.BID_LEASE_TIMEOUT);
        space.write(testBid3, null, Constants.BID_LEASE_TIMEOUT);
    }

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testUnsupportedConstructor() throws Exception {
        Constructor constructor = InterfaceUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void testGetTextAsPositiveNumber() throws Exception {
        // Integer
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("1")).intValue(), 1);

        // Long
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("2147483648")).longValue(), Integer.MAX_VALUE + 1L);

        // Double
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("1.23")).doubleValue(), 1.23);

        // Null
        assertNull(InterfaceUtils.getTextAsNumber(new JTextField("")));
        assertNull(InterfaceUtils.getTextAsNumber(new JTextField("test")));
        assertNull(InterfaceUtils.getTextAsNumber(new JTextField("some-string")));

        // Special case truncation
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("1.2")).doubleValue(), 1.2);
    }

    @Test
    public void testGetTextAsNegativeNumber() throws Exception {
        // Integer
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("-1")).intValue(), -1);

        // Long
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("-2147483648")).longValue(), 0 - (Integer.MAX_VALUE + 1L));

        // Double
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("-1.23")).doubleValue(), -1.23);

        // Null
        assertNull(InterfaceUtils.getTextAsNumber(new JTextField("-")));
        assertNull(InterfaceUtils.getTextAsNumber(new JTextField("-test")));
        assertNull(InterfaceUtils.getTextAsNumber(new JTextField("-some-string")));

        // Special case truncation
        assertEquals(InterfaceUtils.getTextAsNumber(new JTextField("-1.2")).doubleValue(), -1.2);
    }

    @Test
    public void testGetDoubleAsPositiveCurrency() throws Exception {
        // Null
        assertNull(InterfaceUtils.getDoubleAsCurrency(null));

        // Integer
        assertEquals(InterfaceUtils.getDoubleAsCurrency((double) 1), "£1.00");

        // Double
        assertEquals(InterfaceUtils.getDoubleAsCurrency(1.2), "£1.20");

        // Overly long double
        assertEquals(InterfaceUtils.getDoubleAsCurrency(1.2345), "£1.23");
        assertEquals(InterfaceUtils.getDoubleAsCurrency(1.2678), "£1.27");
    }

    @Test
    public void testGetDoubleAsNegativeCurrency() throws Exception {
        // Null
        assertNull(InterfaceUtils.getDoubleAsCurrency(null));

        // Integer
        assertEquals(InterfaceUtils.getDoubleAsCurrency((double) -1), "-£1.00");

        // Double
        assertEquals(InterfaceUtils.getDoubleAsCurrency(-1.2), "-£1.20");

        // Overly long double
        assertEquals(InterfaceUtils.getDoubleAsCurrency(-1.2345), "-£1.23");
        assertEquals(InterfaceUtils.getDoubleAsCurrency(-1.2678), "-£1.27");
    }

    @Test
    public void testGetBidHistoryFromBiddingUser() throws Exception {
        ArrayList<IWsBid> bidArrayList = InterfaceUtils.getBidHistory(testLot1);

        assertEquals(bidArrayList.get(2), testBid1);
        assertEquals(bidArrayList.get(1), testBid2);
        assertEquals(bidArrayList.get(0), testBid3);
    }

    @Test
    public void testGetBidHistoryFromNewUser() throws Exception {
        UserUtils.setCurrentUser("New Test");

        ArrayList<IWsBid> bidArrayList = InterfaceUtils.getBidHistory(testLot1);

        assertEquals(bidArrayList.get(2).getUser().getId(), "Anonymous Buyer");
        assertEquals(bidArrayList.get(1).getUser().getId(), "Anonymous Buyer");
        assertEquals(bidArrayList.get(0).getUser().getId(), "Anonymous Buyer");
    }

    @Test
    public void testGetBidHistoryWithNoBids() throws Exception {
        assertEquals(InterfaceUtils.getBidHistory(testLot2).size(), 0);
    }

    @Test
    public void testGetVectorBidMatrix() throws Exception {
        Vector<Vector<String>> vectorMatrix = InterfaceUtils.getVectorBidMatrix(testLot1);

        assertEquals(vectorMatrix.get(0).get(0), "Anonymous Buyer");
        assertEquals(vectorMatrix.get(0).get(1), "£3.00");
        assertEquals(vectorMatrix.get(1).get(0), "Anonymous Buyer");
        assertEquals(vectorMatrix.get(1).get(1), "£2.00");
        assertEquals(vectorMatrix.get(2).get(0), "Anonymous Buyer");
        assertEquals(vectorMatrix.get(2).get(1), "£1.00");
    }

    @Test
    public void testToCamelCase() throws Exception {
        // Spaces
        assertEquals(InterfaceUtils.toCamelCase("test as string", " "), "testAsString");
        assertEquals(InterfaceUtils.toCamelCase("Test As String", " "), "testAsString");
        assertEquals(InterfaceUtils.toCamelCase("TEST AS STRING", " "), "testAsString");

        // Dots
        assertEquals(InterfaceUtils.toCamelCase("test.as.string", "\\."), "testAsString");
        assertEquals(InterfaceUtils.toCamelCase("Test.As.String", "\\."), "testAsString");
        assertEquals(InterfaceUtils.toCamelCase("TEST.AS.STRING", "\\."), "testAsString");

        // Negative
        assertEquals(InterfaceUtils.toCamelCase("", " "), "");
        assertEquals(InterfaceUtils.toCamelCase("", "\\."), "");
        assertEquals(InterfaceUtils.toCamelCase("testAsString", " "), "testAsString");
        assertEquals(InterfaceUtils.toCamelCase("testAsString", "\\."), "testAsString");
    }

    @AfterClass
    public void teardown() throws Exception {
        space.take(new IWsLot(), null, Constants.SPACE_TIMEOUT);
        space.take(new IWsLot(), null, Constants.SPACE_TIMEOUT);
        space.take(new IWsBid(), null, Constants.SPACE_TIMEOUT);
        space.take(new IWsBid(), null, Constants.SPACE_TIMEOUT);
        space.take(new IWsBid(), null, Constants.SPACE_TIMEOUT);
    }
}
