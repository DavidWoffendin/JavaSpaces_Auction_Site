package com.zackehh.auction;

import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.*;

public class IWsLotTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testIWsLotInitializations() throws Exception {
        Integer id = 1;
        IWsUser user = new IWsUser("Test");
        ArrayList<Integer> history = new ArrayList<Integer>();
        String name = "Test Lot";
        Double price = 5.00;
        String description = "This is a test lot.";
        Boolean ended = true;
        Boolean markedForRemoval = false;

        history.add(1);

        IWsLot nullLot = new IWsLot();

        assertNull(nullLot.getId());
        assertNull(nullLot.getUser());
        assertNull(nullLot.getItemName());
        assertNull(nullLot.getCurrentPrice());
        assertNull(nullLot.getItemDescription());

        assertFalse(nullLot.hasEnded());
        assertFalse(nullLot.isMarkedForRemoval());

        assertEquals(nullLot.getHistory(), new ArrayList<Integer>());
        assertEquals(nullLot.getLatestBid(), null);
        assertEquals(nullLot.asObjectArray(), new Object[]{ null, null, null, null, "Running" });

        IWsLot lot = new IWsLot(id, user, history, name, price, description, ended, markedForRemoval);

        assertEquals(lot.getId(), id);
        assertEquals(lot.getUser(), user);
        assertEquals(lot.getUserId(), user.getId());
        assertEquals(lot.getItemName(), name);
        assertEquals(lot.getCurrentPrice(), price);
        assertEquals(lot.getItemDescription(), description);
        assertEquals(lot.hasEnded(), ended);
        assertEquals(lot.isMarkedForRemoval(), markedForRemoval);

        assertEquals(lot.getHistory(), history);
        assertEquals(lot.getLatestBid().intValue(), 1);
        assertEquals(lot.asObjectArray(), new Object[]{ id, name, user.getId(), "£5.00", "Ended" });

        lot.ended = false;
        lot.markedForRemoval = true;

        assertEquals(lot.hasEnded().booleanValue(), !ended);
        assertEquals(lot.isMarkedForRemoval().booleanValue(), !markedForRemoval);
    }

    @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "EqualsWithItself", "ObjectEqualsNull"})
    @Test
    public void testIWsLotEquals() throws Exception {
        IWsLot lot1 = new IWsLot(1, new IWsUser("Test"), new ArrayList<Integer>(), "Test", (double) 5, "Testing", true, true);
        IWsLot lot2 = new IWsLot(1, new IWsUser("Test"), new ArrayList<Integer>(), "Test", (double) 5, "Testing", true, true);

        assertTrue(lot1.equals(lot2));
        assertTrue(lot1.equals(lot1));

        lot2.user = null;

        assertFalse(lot1.equals(lot2));
        assertFalse(lot1.equals(1));
        assertFalse(lot1.equals(null));
    }

    @Test
    public void testIWsLotGetLatestBid() throws Exception {
        IWsLot lot = new IWsLot();

        lot.getHistory().add(1);
        lot.getHistory().add(2);
        lot.getHistory().add(3);
        lot.getHistory().add(4);
        lot.getHistory().add(5);
        assertEquals(lot.getLatestBid().intValue(), 5);

        lot.getHistory().add(6);
        assertEquals(lot.getLatestBid().intValue(), 6);

        lot.getHistory().add(7);
        assertEquals(lot.getLatestBid().intValue(), 7);
    }

}
