package U1654949.userinterfacecards;

import U1654949.spacedataobjects.*;
import U1654949.SpaceUtils;
import U1654949.User;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.NumberFormat;
import java.util.*;

/**
 * Lot card class is called whenever a lot is loaded from the list card
 *
 * It is responsible for displaying the bid history of a lot and its details
 *
 * It is also responsible for dealing with the purchasing and bidding of lots
 */
class LotCard extends JPanel {

    private final JavaSpace javaSpace; // JavaSpace variable
    private LotTable bidsList; // JTable variable
    private DIBWLot lot; // Lot data object variable
    private TransactionManager transactionManager; // transaction manager variable

    private final Vector<Vector<String>> bids; // Vector variable for all bids

    private JButton end; // end lot button
    private JButton placeBid; // place bit button
    private JButton buyNow; // buy it now button

    private JLabel price; // lot price label
    private JLabel priceLabel; // price result label

    private final JPanel card;
    private ActionListener remove; //remove action listener, needed due to needing to remove listener later

    private NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK); // number formatter to change doubles to GPB

    LotCard(final JPanel card, DIBWLot lotForCard) {
        super();
        this.card = card;
        this.javaSpace = SpaceUtils.getSpace();
        this.transactionManager = SpaceUtils.getManager();

        DIBWLot baseLot = lotForCard;
        setLayout(new BorderLayout());

        // This attempts to get a template of the lot with a specific ID
        try {
            DIBWLot templateLot = new DIBWLot(lotForCard.getId());
            baseLot = (DIBWLot) javaSpace.read(templateLot, null, 1500);
        } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
            System.err.println("Error: " + e);
        }
        // sets the read item to base lot;
        this.lot = baseLot;

        // retrieves all associated bids and bid ID's and sorts them
        bids = getVectorBidMatrix(lot);

        // This assigns the actions listeners that will be called when the notify method is triggered from the space
        try {
            BidListener bidListener = new BidListener();
            LotRemoverListener endedListener = new LotRemoverListener();
            // Bid template
            DIBWBid bTemplate = new DIBWBid(null, null, lot.getId(), null);
            //Lot template
            DIBWLotRemove rTemplate = new DIBWLotRemove(lot.getId(), null, null, null, null);
            // Notify method looks for any bids with the current lot ID getting added to the space
            javaSpace.notify(bTemplate, null, bidListener.getListener(), Lease.FOREVER, null);
            // Notify method looks for any remove lot objects with the current lots ID that get added to the space
            javaSpace.notify(rTemplate, null, endedListener.getListener(), Lease.FOREVER, null);
        } catch (TransactionException | RemoteException e) {
            System.err.println("Error: " + e);
        }

        // creates a lot detail panel used for storing lot details
        JPanel lotInteractionPanel = new JPanel(new BorderLayout());
        lotInteractionPanel.setBorder(BorderFactory.createEmptyBorder(10, 250, 0, 250));
        cardButtonsCreator(lotInteractionPanel); //method for added buttons to card
        bidTableGetter(lotInteractionPanel); //method for adding bid list to card
        lotDetailsGetter(); //method for adding lot details to card
    }

    // takes an array of collected bids
    private Vector<Vector<String>> getVectorBidMatrix(DIBWLot lot){
        // creates a number formatter for GBP
        final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);
        // creates an vector string to store bid information
        Vector<Vector<String>> values = new Vector<>();
        //creates a new array list of type bid this will be used to store the retrieved bids
        ArrayList<DIBWBid> lotBids = new ArrayList<>();
        try {
            // attempts to read the current lot from the space based on lot Id
            DIBWLot currentLot = (DIBWLot) javaSpace.read(new DIBWLot(lot.getId()), null, 1500);
            // creates an array of bids from the current lot
            ArrayList<Integer> bids = currentLot.getBids();
            // if there are currently no bid just return
            if(bids.size() == 0){
                return values;
            }
            // for loop to iterate over all retrieved bids
            for(Integer bidId : bids){
                // creates template of bid based on the stored bid Id
                DIBWBid template = new DIBWBid(bidId, null, lot.getId(), null);
                // reads the bid item based on the template
                DIBWBid bidItem = ((DIBWBid) javaSpace.read(template, null, 1500));
                // adds that retrieves bid item to the array
                lotBids.add(bidItem);
            }
            // Sorts the collected bids into the correct order
            Collections.sort(lotBids, new Comparator<DIBWBid>() {
                @Override
                public int compare(DIBWBid b1, DIBWBid B2) {
                    return B2.getPrice().compareTo(b1.getPrice());
                }
            });
            // error handling
        } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
            System.err.println("Error: " + e);
        }
        // for loop to iterate over each bid
        for(int bidCount = 0; bidCount < lotBids.size(); bidCount++){
            // sets bid based on current bid ID
            final DIBWBid bid = lotBids.get(bidCount);
            // creates a vector string with the user id of the bid and the bid value
            values.add(bidCount, new Vector<String>(){{
                add(bid.getUser().getId());
                add(nf.format(bid.getPrice()));
            }});
        }
        return values;
    }

    private void cardButtonsCreator(JPanel infoPanel) {
        // Adding text to the card buttons
        placeBid = new JButton("Place Bid");
        buyNow = new JButton("Buy it now");
        end = new JButton("Accept Highest Bid");
        //back button
        JButton back = new JButton("Back to Lot List");

        // action listener to close the lot
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                card.remove(LotCard.this);
            }
        });
        infoPanel.add(back, BorderLayout.NORTH);

        // This bit of logic looks at who is currently viewing the lot and assigned buttons based on this
        if(!lot.isEnded()) {
            if (User.getCurrentUser().getId().equals(lot.getUser().getId())) {
                if(lot.getLastBid() == null){
                    // Adds the remove lot functionality to the accept/remove button
                    end.setText("End Lot Early");
                    remove = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            removeLot();
                        }
                    };
                    end.addActionListener(remove);
                } else {
                    // Add the accept bit functionality to the accept/remove button
                    end.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            endLot();
                        }
                    });
                }
                infoPanel.add(end, BorderLayout.CENTER);
            } else {
                // Adds the place bid button
                placeBid.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        addBid();
                    }
                });
                // Adds the buy it now button
                buyNow.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        buyLot();
                    }
                });
                infoPanel.add(placeBid, BorderLayout.CENTER);
                infoPanel.add(buyNow,BorderLayout.SOUTH);
            }
        }
    }

    /**
     * The add bid method when called simply will ask for an amount to enter as a bid
     * This is then added into the space through updating the existing lot and by
     * adding a new lot update object to trigger the systems notify methods which
     * update the GUI
     *
     * The Transactions where based of Gary Allen's Transactions demo
     *
     *    Title: JavaSpacesTransactionsDemo
     *    Author: Gary Allen
     *    Date: 7/11/2019
     *    Code version: Commit aaec1b019b33389f1078956a384e3e27154f3ed0
     *    Availability: https://github.com/GaryAllenGit/JavaSpaceTransactionsDemo/blob/master/src/TxnExample.java
     */
    private void addBid() {
        // asks the user for the amount they want to bid
        JPanel bidPanel = new JPanel(new GridLayout(2, 2));
        JTextField bidEntry = new JTextField();
        bidPanel.add(new JLabel("Bid Amount: "));
        bidPanel.add(bidEntry);
        int result = JOptionPane.showConfirmDialog(null, bidPanel,"Please enter your bid details:", JOptionPane.OK_CANCEL_OPTION);
        // if the user clicks ok
        if (result == JOptionPane.OK_OPTION) {
            Double bid;
            String bidString = bidEntry.getText();
            // test to see if the bid is a valid double
            try {
                Double.parseDouble(bidString);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid bid entered!");
                return;
            }
            // test to ensure the bid is high enough
            if (!((bid = Double.parseDouble(bidString)) > 0 && bid > lot.getPrice())) {
                JOptionPane.showMessageDialog(null, "Invalid bid entered!");
                return;
            }
            // creates the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 3000);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }
            Transaction txn = null;
            if (trc != null) {
                txn = trc.transaction;
            }
            // try to get bid counter and take the lot object
            try {
                DIBWAuctionStatusObject bidCounter = (DIBWAuctionStatusObject) javaSpace.take(new DIBWAuctionStatusObject(), txn, 1500);
                DIBWLot updatedLot = (DIBWLot) javaSpace.take(new DIBWLot(lot.getId()), txn, 1500);

                // gets a new bid number from the Auction status object
                int bidId = bidCounter.countBid();
                // adds the new bid id to the lot object
                updatedLot.getBids().add(bidId);
                // set the new price of the lot
                updatedLot.setPrice(bid);
                // create bid item
                DIBWBid newBid = new DIBWBid(bidId, User.getCurrentUser(), lot.getId(), bid);

                // writes the lot update object into the space
                javaSpace.write(new DIBWLotUpdate(lot.getId(), bid), txn, 3000);
                // rewrites the lot object back into the space
                javaSpace.write(updatedLot, txn, 3600000);
                // writes the new bid into the space
                javaSpace.write(newBid, txn, 5000000);
                // writes the bid object back into the space
                javaSpace.write(bidCounter, txn, Lease.FOREVER);

                // the following is all error handling
                if (txn != null) {
                    txn.commit();
                }
                lot = updatedLot;
            } catch (TransactionException | InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException | UnusableEntryException e) {
                System.err.println("Error: " + e);
                try {
                    if (txn != null) {
                        txn.abort();
                    }
                } catch (RemoteException | CannotAbortException | UnknownTransactionException ex) {
                    System.err.println("Error: " + ex);
                }
            }
        }
    }

    private void buyLot() {
        // Double checks the user wants to buy the lot and then continues with removal
        int result = JOptionPane.showConfirmDialog(null, "Do you want to buy this lot?", "Buy It Now?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // now buyer is added as that is only for buy functionality
            removeLot(null, false, false, true);
        }
    }

    private void endLot() {
        // Double checks the seller wants to end the lot and then continues with removal
        JPanel modal = new JPanel();
        modal.add(new JLabel("End Lot?"));
        int result = JOptionPane.showConfirmDialog(null, modal,"Accept Bid?", JOptionPane.OK_CANCEL_OPTION);
        // gets the last bid and its user ID from the bid list and then assigns it to the removal object
        if (result == JOptionPane.OK_OPTION) {
            Vector<String> winningBid = bids.get(0);
            String buyerName = winningBid.get(0);
            removeLot(buyerName, true, false, false);
        }
    }

    private void removeLot() {
        // Double checks the seller wants to remove the lot and then continues with removal
        JPanel modal = new JPanel();
        modal.add(new JLabel("Are you sure you want to remove the lot?"));
        int result = JOptionPane.showConfirmDialog(null, modal, "Remove Lot?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // no buyer is added as this is removal of a lot
            removeLot(null, false, true, false);
        }
    }

    /**
     * The remove lot method is used by all the action listener methods that deal with removal of Lots
     *
     * The Transactions where based of Gary Allen's Transactions demo
     *
     *    Title: JavaSpacesTransactionsDemo
     *    Author: Gary Allen
     *    Date: 7/11/2019
     *    Code version: Commit aaec1b019b33389f1078956a384e3e27154f3ed0
     *    Availability: https://github.com/GaryAllenGit/JavaSpaceTransactionsDemo/blob/master/src/TxnExample.java
     *
     * @param buyerName takes the buyers name if there is one
     * @param end takes the end boolean
     * @param remove takes the remove boolean
     * @param bought takes the bought boolean
     */
    private void removeLot(String buyerName, Boolean end, Boolean remove, Boolean bought) {
        // creates a new transaction object for the transaction
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(transactionManager, 3000);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);
        }
        Transaction txn = null;
        if (trc != null) {
            txn = trc.transaction;
        }
        // attempts to read the lot based of the lot id
        try {
            DIBWLot updatedLot = (DIBWLot) javaSpace.read(new DIBWLot(lot.getId()), txn, 1500);
            //sets the local lot object to the correct settings
            updatedLot.setOverallRemoval(end, remove, bought);
            // writes a new removal object to the space
            javaSpace.write(new DIBWLotRemove(lot.getId(), buyerName, end, remove, bought), txn, 3000);
            // the rest is error handling
            if (txn != null) {
                txn.commit();
            }
            // set lot to updated lot
            lot = updatedLot;
        } catch (RemoteException | TransactionException | InterruptedException | UnusableEntryException e) {
            System.err.println("Error: " + e);
            try {
                if (txn != null) {
                    txn.abort();
                }
            } catch (RemoteException | CannotAbortException | UnknownTransactionException ex) {
                System.err.println("Error: " + ex);
            }
        }
    }

    private void bidTableGetter(JPanel infoPanel) {
        // This generates a JTable with the bidders name and their amount
        bidsList = new LotTable(bids, new Vector<String>() {{
            add("Bidder");
            add("Bid Value");
        }});
        // This table is then added to the panel
        JScrollPane itemListPanel = new JScrollPane(bidsList);
        add(itemListPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
    }

    // collects lot details and displays they on the card
    private void lotDetailsGetter() {
        JPanel lotDetails = new JPanel(new GridLayout(5, 2));
        lotDetails.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        price = new JLabel();
        // lot buy it now price
        JLabel buyItNowPrice = new JLabel();

        // adds the seller name to the card
        JLabel l1 = new JLabel("Seller Name - ", SwingConstants.RIGHT);
        lotDetails.add(l1);
        JLabel textLabel1 = new JLabel(String.valueOf(lot.getUserId()));
        l1.setLabelFor(textLabel1);
        lotDetails.add(textLabel1);
        // adds lot name to the card
        JLabel l2 = new JLabel("Lot Name - ", SwingConstants.RIGHT);
        lotDetails.add(l2);
        JLabel textLabel2 = new JLabel(String.valueOf(lot.getName()));
        l2.setLabelFor(textLabel2);
        lotDetails.add(textLabel2);
        // adds the lot description to the card
        JLabel l3 = new JLabel("Lot Description - ", SwingConstants.RIGHT);
        lotDetails.add(l3);
        JLabel textLabel3 = new JLabel(String.valueOf(lot.getDescription()));
        l3.setLabelFor(textLabel3);
        lotDetails.add(textLabel3);
        // adds the buy it now price to the card
        JLabel buyNowLabel;
        buyNowLabel = new JLabel("Buy it Now Price: ", SwingConstants.RIGHT);
        buyItNowPrice.setText(nf.format(lot.getBuyNowPricePrice()));
        lotDetails.add(buyNowLabel);
        lotDetails.add(buyItNowPrice);
        // logic to see if the lot has ended and if so set the price label to ended
        if(lot.isEnded()){
            priceLabel = new JLabel("Won by " + bids.get(0).get(0) + " -", SwingConstants.RIGHT);
            price.setText(" Price: " + nf.format(lot.getPrice()));
        } else {
            priceLabel = new JLabel("Current Price: ", SwingConstants.RIGHT);
            price.setText(nf.format(lot.getPrice()));
        }
        // add price to cards
        priceLabel.setLabelFor(price);
        lotDetails.add(priceLabel);
        lotDetails.add(price);
        add(lotDetails, BorderLayout.SOUTH);
    }

    // bidListener Class, this is called when a new bid is added and updates the lot UI accordingly
    private class BidListener extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            bidListener();
        }
        // bidListener function
        private void bidListener() {
            try {
                // reads the lot and latest bid associated with a lot
                final DIBWLot latestLot = (DIBWLot) javaSpace.read(new DIBWLot(lot.getId()), null, 1500);
                final DIBWBid latestBid = (DIBWBid) javaSpace.read(new DIBWBid(latestLot.getLastBid()), null, 1500);
                // creates a new vector string with the latest bids users and bid price
                Vector<String> insertion = new Vector<String>(){{
                    add(latestBid.getUser().getId());
                    add(nf.format(latestBid.getPrice()));
                }};
                // checks to see if the there has been a bid added to the lot and that the viewer is the seller
                if(latestLot.getLastBid() != null && User.getCurrentUser().getId().equals(lot.getUser().getId())){
                    //sets the button to accept latest bid
                    end.setText("Accept Latest Bid");
                    end.removeActionListener(remove);
                    end.addActionListener(new ActionListener() {
                        // calls the end lot functionality
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            endLot();
                        }
                    });
                }
                // adds the latest bid to the top of the table
                bids.add(0, insertion);
                // resets the list
                bidsList.revalidate();
                // updates the price to current highest bid
                price.setText(nf.format(latestLot.getPrice()));
                //error handling
            } catch (UnusableEntryException | TransactionException | RemoteException | InterruptedException e) {
                System.err.println("Error: " + e);
            }
        }
    }
    // Notifier class which is used to listen for the lot being removed
    private class LotRemoverListener extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            lotRemoverListener();
        }
        // lot remover method
        private void lotRemoverListener() {
            try {
                //Looks for a lot remover with the same ID as the lot
                final DIBWLotRemove remover = (DIBWLotRemove) javaSpace.read(new DIBWLotRemove(lot.getId()), null, 1500);
                // if the lot remover is set to remove it states the lots removed and then closes the lot
                if(remover.isRemoved()){
                    JOptionPane.showMessageDialog(null, "This lot has been removed!");
                    card.remove(LotCard.this);
                }
                // if the lot is ended by the seller this sets the appropriate information
                if(remover.isEnded()){
                    end.setVisible(false);
                    placeBid.setVisible(false);
                    buyNow.setVisible(false);
                    priceLabel.setText("Lot has been Won");
                    price.setText("");
                    return;
                }
                // if the lot is bought outright by a user
                if(remover.isBoughtOutright()){
                    end.setVisible(false);
                    placeBid.setVisible(false);
                    buyNow.setVisible(false);
                    priceLabel.setText("Lot has been Bought");
                    price.setText("");
                }
            // error handling
            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }

    }

    /***************************************************************************************
     *    The Notify Class is based on Gary Allen's notify demo
     *    This has been refactored into a class for ease of use
     *    All notify methods simply extend this reducing repeated code
     *
     *    Title: JavaSpacesNotifyDemo
     *    Author: Gary Allen
     *    Date: 11/07/2018
     *    Code version: Commit c3df3f380e9c049c4f7fe7aa1ad2b6158abb7f38
     *    Availability: https://github.com/GaryAllenGit/JavaSpaceNotifyDemo/blob/master/src/HelloWorldNotify.java
     *
     ***************************************************************************************/
    public static class Notifier implements RemoteEventListener {
        Exporter remoteExporter;
        private RemoteEventListener listener;
        // notifier creates the remote exporter which is then inherited by all notifier classes
        // helps reduces duplicate code
        Notifier() {
            try{
                remoteExporter =
                        new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                new BasicILFactory(), false, true);
                listener = (RemoteEventListener) remoteExporter.export(this);
                // error handling
            } catch (ExportException e) {
                System.err.println("Error: " + e);
            }
        }
        // gets the listener
        RemoteEventListener getListener() {
            return listener;
        }
        // creates the notify functionality
        public void notify(RemoteEvent remoteEvent) {
            super.notify();
        }
    }

    /**
     * JTable Class used to display Bid information
     */
    public static class LotTable extends JTable {
        // generates a table from vector string which the bids are stored in
        LotTable(Vector<Vector<String>> data, Vector<String> columns){
            setModel(new FixedLotTableModel(data, columns));
        }
        private static class FixedLotTableModel extends DefaultTableModel {
            FixedLotTableModel(Vector<Vector<String>> data, Vector<String> columns){
                super(data, columns);
            }
            // ensures the table cant be edited
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }

        }

    }
}

