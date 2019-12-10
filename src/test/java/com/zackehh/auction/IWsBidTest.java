package com.zackehh.auction;

import com.zackehh.util.UserUtils;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class IWsBidTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testIWsBidInitializations() throws Exception {
        Integer id = 1;
        IWsUser user = new IWsUser("Test");
        Integer itemId = 5;
        Double price = 5.00;
        Boolean visible = true;

        IWsBid nullBid = new IWsBid();

        assertNull(nullBid.getId());
        assertNull(nullBid.getUser());
        assertNull(nullBid.getItemId());
        assertNull(nullBid.getPrice());
        assertNull(nullBid.isPublic());

        IWsBid bid = new IWsBid(id, user, itemId, price, visible);

        assertEquals(bid.getId(), id);
        assertEquals(bid.getUser(), user);
        assertEquals(bid.getItemId(), itemId);
        assertEquals(bid.getPrice(), price);
        assertEquals(bid.isPublic(), visible);
    }

    @SuppressWarnings({"EqualsBetweenInconvertibleTypes", "EqualsWithItself", "ObjectEqualsNull"})
    @Test
    public void testIWsBidEquals() throws Exception {
        IWsBid bid1 = new IWsBid(1, new IWsUser("Test"), 5, (double) 5, true);
        IWsBid bid2 = new IWsBid(1, new IWsUser("Test"), 5, (double) 5, true);

        assertTrue(bid1.equals(bid2));
        assertTrue(bid1.equals(bid1));

        bid2.setUser(null);

        assertFalse(bid1.equals(bid2));
        assertFalse(bid1.equals(1));
        assertFalse(bid1.equals(null));
    }

    @Test
    public void testIWsBidIsAnonymous() throws Exception {
        IWsUser user = UserUtils.setCurrentUser("Tester");

        IWsLot lot = new IWsLot();

        lot.user = user;
        lot.id = 1;

        IWsBid bid = new IWsBid(1, user, 1, (double) 5, true);

        assertFalse(bid.isAnonymous(lot));

        bid.visible = false;

        assertFalse(bid.isAnonymous(lot));

        UserUtils.setCurrentUser("Tester Two");

        assertTrue(bid.isAnonymous(lot));

        bid.visible = true;

        assertFalse(bid.isAnonymous(lot));
    }

}
