package com.zackehh.ui.cards;

import com.zackehh.auction.IWsBid;
import com.zackehh.auction.IWsLot;
import com.zackehh.auction.secretary.IWsLotSecretary;
import com.zackehh.auction.status.IWsItemRemover;
import com.zackehh.auction.status.IWsLotChange;
import com.zackehh.ui.GenericNotifier;
import com.zackehh.ui.components.BaseTable;
import com.zackehh.ui.components.JResultText;
import com.zackehh.util.Constants;
import com.zackehh.util.InterfaceUtils;
import com.zackehh.util.SpaceUtils;
import com.zackehh.util.UserUtils;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * The main body of the application's UI. This card displays
 * the list of lots in the auction currently, along with their
 * information. Clicking a row allows the user to go to a card
 * specifically based on the chosen lot to allow bids etc.
 * This card also provides a way for a user to add a new lot.
 */
public class AuctionCard extends JPanel {

    /**
     * The common JavaSpace instance, stored privately.
     */
    private final JavaSpace space;

    /**
     * The common TransactionManager instance, stored privately.
     */
    private final TransactionManager manager;

    /**
     * An ArrayList to keep track of the lots gathered from the
     * space.
     */
    private final ArrayList<IWsLot> lots;

    /**
     * The table to keep track of any new lots entered.
     */
    private final BaseTable lotTable;

    /**
     * Initialize a new AuctionCard with a list of initial lots
     * to display. Provides access to the list of lots and the cards
     * parent to enable addition/removal of cards representing the
     * bid card.
     *
     * @param lots              the list of lot items
     * @param cards             the cards layout
     */
    public AuctionCard(final ArrayList<IWsLot> lots, final JPanel cards){
        super(new BorderLayout());

        // Setup required parameters
        this.lots = lots;
        this.manager = SpaceUtils.getManager();
        this.space = SpaceUtils.getSpace();

        // Setup the main Grid layout to contain the input form
        JPanel fieldInputPanel = new JPanel(new GridLayout(4, 2));
        fieldInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set item name input
        final JTextField itemNameIn = new JTextField("", 12);
        fieldInputPanel.add(new JLabel("Name of Item: "));
        fieldInputPanel.add(itemNameIn);

        // Set item description input
        final JTextField itemDescriptionIn = new JTextField("", 1);
        fieldInputPanel.add(new JLabel("Item description: "));
        fieldInputPanel.add(itemDescriptionIn);

        // Set starting price input
        final JTextField startingPriceIn = new JTextField("", 6);
        fieldInputPanel.add(new JLabel("Starting Price: "));
        fieldInputPanel.add(startingPriceIn);

        // Setup result output fields
        final JResultText resultTextOut = new JResultText();
        fieldInputPanel.add(new JLabel("Result: "));
        fieldInputPanel.add(resultTextOut);

        // Add the layout to the panel
        add(fieldInputPanel, BorderLayout.NORTH);

        // Create an initial base table with the given column names
        lotTable = new BaseTable(new String[0][5], new String[] {
                "Lot ID", "Item Name", "Seller ID", "Current Price", "Status"
        });

        // Add listener to the row click in the table
        lotTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                // Calculate the row based on the mouse positioning
                int row = lotTable.rowAtPoint(event.getPoint());

                // Only activate the listener on a double click
                if (event.getClickCount() == 2) {

                    // If the item has already ended, deny access and short circuit
                    if (lots.get(row).hasEnded()) {
                        JOptionPane.showMessageDialog(null, "This item has already ended!");
                        return;
                    }

                    // Check for no longer available - should never be needed
                    if (lots.get(row).isMarkedForRemoval()){
                        JOptionPane.showMessageDialog(null, "This item is no longer available!");
                        return;
                    }

                    // Add a new card, using the selected lot
                    cards.add(new LotCard(cards, lots.get(row)), Constants.BID_CARD);

                    // Display the new card
                    ((CardLayout) cards.getLayout()).show(cards, Constants.BID_CARD);
                }
            }
        });

        // Add the table to a scrolling pane
        JScrollPane itemListPanel = new JScrollPane(
            lotTable,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // Add the scrolling pane to the main panel
        add(itemListPanel, BorderLayout.CENTER);

        // Create an "Add Auction" button
        JButton addLotButton = new JButton();
        addLotButton.setText("Add Auction Item");

        // Set the required listener
        addLotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Refresh any prior result text
                resultTextOut.setText("");

                // Gather user inputs
                String itemName = itemNameIn.getText();
                String itemDescription = itemDescriptionIn.getText();
                Number startingPrice = InterfaceUtils.getTextAsNumber(startingPriceIn);
                Double potentialDouble = startingPrice == null ? 0 : startingPrice.doubleValue();

                // Validate item details, short circuit if not met
                if(itemName.length() == 0 || itemDescription.length() == 0){
                    resultTextOut.setText("Invalid item details!");
                    return;
                }

                // Validate price, short circuit if not met
                if(startingPrice == null || potentialDouble == 0){
                    resultTextOut.setText("Invalid price!");
                    return;
                }

                Transaction transaction = null;
                try {
                    // Create a Transaction to handle the modifications
                    Transaction.Created trc = TransactionFactory.create(manager, 3000);
                    transaction = trc.transaction;

                    // Pull the latest IWsLotSecretary from the space
                    IWsLotSecretary secretary = (IWsLotSecretary) space.take(new IWsLotSecretary(), transaction, Constants.SPACE_TIMEOUT);

                    // Increment and retrieve the new item id
                    final int lotNumber = secretary.addNewItem();

                    // Create a new lot based on the user input
                    IWsLot newLot = new IWsLot(lotNumber, UserUtils.getCurrentUser(), null, itemName, potentialDouble, itemDescription, false, false);

                    // Write both the secretary and the lot to the space
                    space.write(newLot, transaction, Constants.LOT_LEASE_TIMEOUT);
                    space.write(secretary, transaction, Lease.FOREVER);

                    // Commit the Transaction
                    transaction.commit();

                    // Reset the input fields
                    itemNameIn.setText("");
                    itemDescriptionIn.setText("");
                    startingPriceIn.setText("");

                    // Output a success message
                    resultTextOut.setText("Added Lot #" + lotNumber + "!");

                    lots.add(newLot);
                    getTableModel().addRow(newLot.asObjectArray());
                } catch(Exception e) {
                    e.printStackTrace();
                    try {
                        // Abort existing Transactions
                        if(transaction != null){
                            transaction.abort();
                        }
                    } catch(Exception e2) {
                        e2.printStackTrace();
                    }
                }

            }
        });

        // Add the "Add Lot" button to the main frame
        JPanel bidListingPanel = new JPanel(new FlowLayout());
        bidListingPanel.add(addLotButton);
        add(bidListingPanel, BorderLayout.SOUTH);

        try {
            // Ensure that all listeners are set for notify
            space.notify(new IWsLotChange(), null, new LotChangeNotifier().getListener(), Lease.FOREVER, null);
            space.notify(new IWsLotSecretary(), null, new NewLotNotifier().getListener(), Lease.FOREVER, null);
            space.notify(new IWsItemRemover(), null, new RemoveLotFromAuctionNotifier().getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the table model of lotTable for access outside
     * this class. This is used to populate the table initially.
     *
     * @return DefaultTableModel    the table model
     */
    public DefaultTableModel getTableModel(){
        return ((DefaultTableModel) lotTable.getModel());
    }

    /**
     * The notifier for when a new lot enters the space. This
     * contains handling for adding new lots to table, but
     * also for marking existing lots as ended, or updating
     * their current price.
     */
    private class NewLotNotifier extends GenericNotifier {

        /**
         * Overrides the parent notify method to allow the
         * space to notify when a new lot is added to the
         * number. This will either add the new lot to the
         * table, or will update the existing record with
         * the new information.
         *
         * @param ev        the remote event
         */
        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();

            try {
                // Grab the latest version of the IWsLotSecretary and the latest lot from the Space
                IWsLotSecretary secretary = (IWsLotSecretary) space.read(new IWsLotSecretary(), null, Constants.SPACE_TIMEOUT);
                IWsLot latestLot = (IWsLot) space.read(new IWsLot(secretary.getItemNumber()), null, Constants.SPACE_TIMEOUT);

                // Convert the lot to an Object[][]
                Object[] insertion = latestLot.asObjectArray();

                // Ensure the lot id does not already exist
                int currentIndex = -1;
                for(int i = 0, j = lots.size(); i < j; i++){
                    if(lots.get(i).getId().intValue() == latestLot.getId().intValue()){
                        currentIndex = i;
                        break;
                    }
                }

                // If it does not exist, insert new entry
                if(currentIndex == -1) {
                    lots.add(latestLot);
                    model.addRow(insertion);
                } else {
                    // Update old entry (should never occur)
                    lots.set(currentIndex, latestLot);
                    model.setValueAt(insertion[3], currentIndex, 3);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class LotChangeNotifier extends GenericNotifier {

        /**
         * Overrides the parent notify method to allow the
         * space to notify when a new lot is added to the
         * number. This will either add the new lot to the
         * table, or will update the existing record with
         * the new information.
         *
         * @param ev        the remote event
         */
        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();

            try {
                // Read the latest IWsLotChange object from the Space (there should only be one)
                IWsLotChange lotChange = (IWsLotChange) space.read(new IWsLotChange(), null, Constants.SPACE_TIMEOUT);

                // Find the existing index of the lot with a matching id
                int currentIndex = -1;
                for(int i = 0, j = lots.size(); i < j; i++){
                    if(lots.get(i).getId().intValue() == lotChange.id){
                        currentIndex = i;
                        break;
                    }
                }

                // If there isn't one, other listeners will handle this
                if(currentIndex == -1){
                    return;
                }

                // Take the lot from the index
                IWsLot lot = lots.get(currentIndex);

                // Apply the new price to the lot
                lot.setPrice(lotChange.getPrice());

                // Convert to an Object[][]
                Object[] insertion = lot.asObjectArray();

                // Replace the lot in the local list and table with the changed lot
                lots.set(currentIndex, lot);
                model.setValueAt(insertion[3], currentIndex, 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Controller for Lot removal, which allows the system to flag a lot as
     * "markedForRemoval"; this will remove the Lot and all associated bids
     * (although the Lot *should* never have any associated bids). This will
     * ensure that all connected clients are updated with the removed lot,
     * without just removing the lot from the initial client.
     */
    private class RemoveLotFromAuctionNotifier extends GenericNotifier {

        /**
         * Custom notify implementation which will fetch the current lot from
         * the space, and remove the lot from the current table and lot list.
         * It will then remove any associated bids before removing the lot from
         * the space entirely. However, this should never occur because you can
         * only remove a lot when no bids have been placed on the item.
         *
         * @param ev        the remote event
         */
        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();

            try {
                // Grab the latest IWsItemRemover from the Space (there should only be one).
                IWsItemRemover remover = (IWsItemRemover) space.read(new IWsItemRemover(), null, Constants.SPACE_TIMEOUT);

                // Find the lot with the matching lot id (if there is one)
                int currentIndex = -1;
                for (int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId().intValue() == remover.getId()){
                        currentIndex = i;
                        break;
                    }
                }

                // If the lot has ended
                if(remover.hasEnded()){
                    // Grab the lot at the current index
                    IWsLot lot = lots.get(currentIndex);

                    // Set the ended field to true
                    lot.setEnded(true);

                    // Update the lot and table with the Ended status
                    lots.set(currentIndex, lot);
                    model.setValueAt("Ended", currentIndex, 4);

                    // Display a dialog if the current user won the ended item
                    if(UserUtils.getCurrentUser().equals(lot.getUser())){
                        JOptionPane.showMessageDialog(null, "You just won " + lot.getItemName() + "!");
                    }
                }

                // If the IWsLot exists and was removed, remove it from the table
                if(remover.hasBeenRemoved() && currentIndex > -1){
                    lots.remove(currentIndex);
                    model.removeRow(currentIndex);
                }

                // Ensure that the matching lot is removed from the Space if it exists
                space.takeIfExists(new IWsLot(remover.getId()), null, 1000);

                // Remove all bids associated with the IWsLot
                Object o;
                do {
                    o = space.takeIfExists(new IWsBid(remover.id), null, 1000);
                } while(o != null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
