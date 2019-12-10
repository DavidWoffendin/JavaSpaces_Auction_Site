package U1654949.User_Interface;

import U1654949.Space_Auction_Items.U1654949_Bid_Space;
import U1654949.Space_Auction_Items.U1654949_Lot_Remover;
import U1654949.Space_Auction_Items.U1654949_Lot_Space;
import U1654949.User_Interface.Interface_Helpers.GenericNotifier;
import U1654949.User_Interface.Defaults.Default_Table;
import U1654949.User_Interface.Interface_Helpers.PlaceBidListener;
import U1654949.Default_Variables;
import U1654949.User_Interface.Interface_Helpers.Common_Functions;
import U1654949.Space_Utils;
import U1654949.User;
import U1654949.User_Interface.Interface_Helpers.AcceptBidListener;
import U1654949.User_Interface.Interface_Helpers.RemoveLotListener;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * The lot page which displays a list of bids associated
 * with the chosen lot. This card allows the user to place
 * a bid on an item, as well as allowing the seller to
 * accept a bid or remove the current item from the auction.
 */
public class Lot_Card extends JPanel {

    /**
     * The common JavaSpace instance, stored privately.
     */
    private final JavaSpace space;

    /**
     * The table of bids which will hold the the bid
     * history of the current lot.
     */
    private final Default_Table bidTable;

    /**
     * The lot that this card is associated with. This
     * will not change throughout the lifecycle of the
     * card.
     */
    private final U1654949_Lot_Space lot;

    /**
     * The history as reflected by the bidTable object.
     * This is updating alongside the table to allow
     * easy access to elements and easy table revalidation.
     */
    private final Vector<Vector<String>> bidHistory;

    /**
     * The label allowing the seller to accept a bid.
     */
    private final JLabel acceptBidOrRemoveLot;

    /**
     * The label displaying the current price of the lot.
     */
    private final JLabel currentPrice;

    /**
     * The label associated with the current price.
     */
    private final JLabel currentPriceLabel;

    /**
     * The label allowing the user to place a bid.
     */
    private final JLabel placeBid;

    /**
     * The main cards parent.
     */
    private final JPanel cards;

    private final AcceptBidListener acceptBidListener;

    private final RemoveLotListener removeLotListener;

    /**
     * Initializes a new card based on the given lot, which
     * allows the user to place a bid on an item and allows
     * the seller to accept a bid or remove a lot from the
     * auction. This card only exists whilst the user remains
     * on the card, it is destroyed when the user returns to
     * the main AuctionCard.
     *
     * @param cards             the parent card layout
     * @param lotForCard        the lot this card is for
     */
    public Lot_Card(final JPanel cards, U1654949_Lot_Space lotForCard) {
        super();

        // Set required fields from params
        this.cards = cards;
        this.space = Space_Utils.getSpace();

        // Refresh the lot, in case state has changed
        U1654949_Lot_Space baseLot = lotForCard;
        try {
            U1654949_Lot_Space templateLot = new U1654949_Lot_Space(lotForCard.getId());
            baseLot = (U1654949_Lot_Space) space.read(templateLot, null, Default_Variables.SPACE_TIMEOUT);
        } catch(Exception e){
            e.printStackTrace(); // doesn't matter, UI will handle it
        }

        // Store the last known good version of the lot
        this.lot = baseLot;

        setLayout(new BorderLayout());

        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            NewBidListener bidListener = new NewBidListener();
            LotChangeListener lotListener = new LotChangeListener();

            // Generate the templates
            U1654949_Bid_Space bidTemplate = new U1654949_Bid_Space(null, null, lot.getId(), null);
            U1654949_Lot_Remover removerTemplate = new U1654949_Lot_Remover(lot.getId(), null, null);

            // Ensures all listeners are set to notify
            space.notify(bidTemplate, null, bidListener.getListener(), Lease.FOREVER, null);
            space.notify(removerTemplate, null, lotListener.getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a new main panel with a BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Set a back button to return to the main UI
        JLabel back = new JLabel("Back");
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                // Remove the current card
                cards.remove(Lot_Card.this);
            }
        });

        // Add the back button to the main frame
        panel.add(back, BorderLayout.WEST);

        // Create any needed labels
        placeBid = new JLabel("Place Bid");
        acceptBidOrRemoveLot = new JLabel("Accept Latest Bid");
        currentPrice = new JLabel();

        // Setup the removal and accept bid listeners
        acceptBidListener = new AcceptBidListener(lot, currentPrice);
        removeLotListener = new RemoveLotListener(lot);

        // Ensure a lot has not ended (this should always be true)
        if(!lot.isEnded()) {

            // If the user is the Seller of the lot
            if (User.getCurrentUser().equals(lot.getUser())) {

                // If there are no bids, the Seller can remove the lot
                if(lot.getLastBid() == null){
                    // Set the new text and add a removal listener
                    acceptBidOrRemoveLot.setText("Remove Lot");
                    acceptBidOrRemoveLot.addMouseListener(removeLotListener);
                } else {
                    // Add a listener to accept the last bid
                    acceptBidOrRemoveLot.addMouseListener(acceptBidListener);
                }

                // Add the label to the main frame
                panel.add(acceptBidOrRemoveLot, BorderLayout.EAST);

            } else {
                // Allow the user to place a bid if desired
                placeBid.addMouseListener(new PlaceBidListener(lot));
                panel.add(placeBid, BorderLayout.EAST);
            }
        }

        // Add the upper panel to the UI
        add(panel, BorderLayout.NORTH);

        // The fields we're displaying information for
        String[] labels = {
            "ID",
            "User ID",
            "Name",
            "Description"
        };

        // Create a GridLayout matching the above labels length
        JPanel p = new JPanel(new GridLayout(labels.length + 1, 2));
        p.setBorder(BorderFactory.createEmptyBorder(-8, 0, 10, 0));

        try {
            // For each label, call the corresponding getter in the
            // IWsLot class. All methods are camelCase forms of the
            // above labels, so this is a simple way of dynamically
            // adding new fields to the UI without having to rewrite
            // the UI components.
            for (String label : labels) {
                // Add the new label to the left side of the panel
                JLabel l = new JLabel(label + ": ", SwingConstants.RIGHT);
                p.add(l);

                // Find the corresponding method for the label
                Class<?> c = lot.getClass();
                Method method = c.getMethod(Common_Functions.toCamelCase("get " + label, " "));

                // Get the string value of the returned value
                String valueOfField = method.invoke(lot) + "";

                // Add a label with the value against the field label
                JLabel textLabel = new JLabel(valueOfField);
                l.setLabelFor(textLabel);
                p.add(textLabel);
            }
        } catch (Exception e) {
            // Will never happen, because we control the fields and methods
        }

        // Grab the history of IWsBid from the Space
        bidHistory = Common_Functions.getVectorBidMatrix(lot);

        // Behaviour changes based on active lots
        if(lot.isEnded()){
            // Display the winner and the price the item was won for
            currentPriceLabel = new JLabel("Won by " + bidHistory.get(0).get(0) + " -", SwingConstants.RIGHT);
            currentPrice.setText(" Price: " + Common_Functions.getDoubleAsCurrency(lot.getPrice()));
        } else {
            // Display the current price of the item
            currentPriceLabel = new JLabel("Current Price: ", SwingConstants.RIGHT);
            currentPrice.setText(Common_Functions.getDoubleAsCurrency(lot.getPrice()));
        }

        // Add the Current Price labels to the panel
        currentPriceLabel.setLabelFor(currentPrice);
        p.add(currentPriceLabel);
        p.add(currentPrice);

        // Add the panel to the frame
        add(p);

        // Create a new BaseTable with two columns
        bidTable = new Default_Table(bidHistory, new Vector<String>(){{
            add("Buyer ID");
            add("Bid Amount");
        }});

        // Add the table to a scrolling pane
        JScrollPane itemListPanel = new JScrollPane(bidTable);

        // Add the scrolling pane to the UI
        add(itemListPanel, BorderLayout.SOUTH);
    }

    /**
     * Provides a way to update the UI based on new bids added
     * to the lot. This will add a new bid to the table, as well
     * as update the current price. This will also enable the
     * seller to now accept the latest bid if it is the first
     * bid added to the lot.
     */
    private class NewBidListener extends GenericNotifier {

        /**
         * Updates the UI with the new bid added to the lot.
         * This will keep all connected clients in sync with
         * each other without the need for manual polling.
         *
         * @param ev        the remote event
         */
        @Override
        public void notify(RemoteEvent ev) {
            try {
                // Grab the latest version of the current lot and the latest bid from the Space
                final U1654949_Lot_Space latestLot = (U1654949_Lot_Space) space.read(new U1654949_Lot_Space(lot.getId()), null, Default_Variables.SPACE_TIMEOUT);
                final U1654949_Bid_Space latestBid = (U1654949_Bid_Space) space.read(new U1654949_Bid_Space(latestLot.getLastBid()), null, Default_Variables.SPACE_TIMEOUT);

                // Format the lot for the BaseTable
                Vector<String> insertion = new Vector<String>(){{
                    add(latestBid.getUser().getId());
                    add(Common_Functions.getDoubleAsCurrency(latestBid.getPrice()));
                }};

                // If there is a latest bid
                if(latestLot.getLastBid() != null && User.getCurrentUser().equals(lot.getUser())){
                    // Allow the Seller to now accept the bids instead of remove them
                    acceptBidOrRemoveLot.setText("Accept Latest Bid");
                    acceptBidOrRemoveLot.addMouseListener(acceptBidListener);
                    acceptBidOrRemoveLot.removeMouseListener(removeLotListener);
                }

                // Add the bid to the top of the table
                bidHistory.add(0, insertion);

                // Redraw the table to ensure up to date
                bidTable.revalidate();

                // Set the new price to the Current Price label
                currentPrice.setText(Common_Functions.getDoubleAsCurrency(latestLot.getPrice()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Listens for changes to any lot items and disables
     * any actions which should be blocked after an auction
     * has ended. This is to ensure ended lots are updated
     * across all connected clients and stay in sync.
     */
    private class LotChangeListener extends GenericNotifier {

        /**
         * Fetches the latest version of the lot from the space
         * when notified and ensures that the auction has not
         * been closed by the seller. This will also ensure that
         * if a lot has been removed, it is reflected as such to
         * the user.
         *
         * @param ev        the remote event
         */
        @Override
        public void notify(RemoteEvent ev) {
            try {
                // Read the latest IWsItemRemover from the Space (there should only be the one we want)
                final U1654949_Lot_Remover remover = (U1654949_Lot_Remover) space.read(new U1654949_Lot_Remover(lot.getId()), null, Default_Variables.SPACE_TIMEOUT);

                // If it was removed due to being won
                if(remover.isEnded()){
                    // Grab the winning bid from the table
                    Vector<String> winningBid = bidHistory.get(0);

                    // Grab the winning user and the winning price
                    String winningId = winningBid.get(0);
                    String winningPrice = winningBid.get(1);

                    // Remove the ability to remove lot and accept/place bid
                    acceptBidOrRemoveLot.setVisible(false);
                    placeBid.setVisible(false);

                    // Set the winning labels
                    currentPriceLabel.setText("Won by " + winningId + " -");
                    currentPrice.setText(" Price: " + winningPrice);

                    // Short circuit
                    return;
                }

                // If the Seller removed the item
                if(remover.isRemoved()){
                    // Prompt that the lot was removed
                    JOptionPane.showMessageDialog(null, "This lot has been removed!");
                    // Return to the main UI
                    cards.remove(Lot_Card.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
