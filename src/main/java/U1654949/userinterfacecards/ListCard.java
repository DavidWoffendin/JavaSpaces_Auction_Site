package U1654949.userinterfacecards;

import U1654949.SpaceUtils;
import U1654949.User;
import U1654949.spacedataobjects.*;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;

public class ListCard extends JPanel {

    private final JavaSpace auctionSpace;
    private final TransactionManager transactionManager;
    private final ArrayList<DIBWLot> lots;
    private final ListTable lotList;

    //Creates a list card
    public ListCard(final ArrayList<DIBWLot> lots, final JPanel cards){
        super(new BorderLayout());

        this.lots = lots; // sets lots from constructor
        this.transactionManager = SpaceUtils.getManager(); // gets the transaction manager
        this.auctionSpace = SpaceUtils.getSpace(); // gets the java space
        // creates a new JPanel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // creates a new text field for item name
        final JTextField itemNameIn = new JTextField("", 12);
        inputPanel.add(new JLabel("Item Name: "));
        inputPanel.add(itemNameIn);
        // creates a new text field for item description
        final JTextField itemDescriptionIn = new JTextField("", 1);
        inputPanel.add(new JLabel("Item description: "));
        inputPanel.add(itemDescriptionIn);
        // creates a new starting price text field
        final JTextField startingPriceIn = new JTextField("", 6);
        inputPanel.add(new JLabel("Starting Price: "));
        inputPanel.add(startingPriceIn);
        // creates a new buy it new text field
        final JTextField buyNowPriceIn = new JTextField("", 6);
        inputPanel.add(new JLabel("Buy It Now Price: "));
        inputPanel.add(buyNowPriceIn);
        // created a new output field
        final JTextField output = new JTextField("", 6);
        output.setEditable(false);
        inputPanel.add(new JLabel("Output: "));
        inputPanel.add(output);
        // add input panel to the card
        add(inputPanel, BorderLayout.CENTER);

        // creates a new lot list with the following headings
        lotList = new ListTable(new String[0][4], new String[]{
                "Item Name", "Current Price", "Buy It Now Price", "Status"
        });

        // add the mouse listener for the table
        lotList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                int row = lotList.rowAtPoint(event.getPoint());
                // checks after two mouse clicks
                if (event.getClickCount() == 2) {
                    // checks to see if the item has ended
                    if (lots.get(row).isEnded()) {
                        JOptionPane.showMessageDialog(null, "This item has already ended!");
                        return;
                    }
                    // checks to see if the lot have been removed
                    if (lots.get(row).isRemoved()){
                        JOptionPane.showMessageDialog(null, "This item is no longer available!");
                        return;
                    }
                    // checks to see if the lot has bee bought
                    if (lots.get(row).isBoughtOutright()) {
                        JOptionPane.showMessageDialog(null, "This item is no longer available!");
                        return;
                    }
                    // resets the inputs to null
                    itemNameIn.setText("");
                    itemDescriptionIn.setText("");
                    startingPriceIn.setText("");
                    buyNowPriceIn.setText("");
                    output.setText("");

                    cards.add(new LotCard(cards, lots.get(row)), "Bid");
                    ((CardLayout) cards.getLayout()).show(cards, "Bid");
                }
            }
        });

        // adds the lot list to a scroll pane and then the card
        JScrollPane itemListPanel = new JScrollPane(
                lotList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        add(itemListPanel, BorderLayout.NORTH);
        // creates the add button
        JButton addLotButton = new JButton();
        addLotButton.setText("Add Lot");
        // creates the action listener to the button
        addLotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                createLot(output, itemNameIn, itemDescriptionIn, startingPriceIn, buyNowPriceIn, lots);
            }
        });
        // adds the button to the card
        JPanel bidListingPanel = new JPanel(new FlowLayout());
        bidListingPanel.add(addLotButton);
        add(bidListingPanel, BorderLayout.SOUTH);

        // attempts to add all the notify functions to the panel
        try {
            auctionSpace.notify(new DIBWLotUpdate(), null, new LotChangeNotifier().getListener(), Lease.FOREVER, null);
            auctionSpace.notify(new DIBWAuctionStatusObject(), null, new NewLotNotifier().getListener(), Lease.FOREVER, null);
            auctionSpace.notify(new DIBWLotRemove(), null, new RemoveLotFromAuctionNotifier().getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // initialises the table on startup
        DefaultTableModel model = (DefaultTableModel) lotList.getModel();
        try {
            // try's to read read the auction status object
            DIBWAuctionStatusObject lotStatus = (DIBWAuctionStatusObject) auctionSpace.read(new DIBWAuctionStatusObject(), null, 1500);
            int i = 0;
            //for all the lot numbers
            while (i <= lotStatus.getLotCounter()) {
                // creates a template for every ID retrieved
                DIBWLot template = new DIBWLot(i++ + 1, null, null, null, null, null, null, false, false, false);
                // attempts to read that item from the space
                DIBWLot listLots = (DIBWLot) auctionSpace.readIfExists(template, null, 1000);
                // if it exists add to table
                if (listLots != null) {
                    this.lots.add(listLots);
                    model.addRow(listLots.returnLotData());
                }
            }
            // error handling
        } catch (UnusableEntryException | TransactionException | RemoteException | InterruptedException e) {
            System.err.println("Error: " + e);
        }
    }

    /**
     *
     *  the create lot function takes all the text field information and creates a lot data object in the space with it
     *
     * The Transactions where based of Gary Allen's Transactions demo
     * Title: JavaSpacesTransactionsDemo
     * Author: Gary Allen
     * Date: 7/11/2019
     * Code version: Commit aaec1b019b33389f1078956a384e3e27154f3ed0
     * Availability: https://github.com/GaryAllenGit/JavaSpaceTransactionsDemo/blob/master/src/TxnExample.java
     *
     * @param output     The results out text field
     * @param itemNameIn        The input for the item name
     * @param itemDescriptionIn the input for the item description
     * @param startingPriceIn   the input for the starting price
     * @param buyNowPriceIn     the input for the buy it now price
     * @param lots              the array of pre existing lots.
     */
    private void createLot(JTextField output, JTextField itemNameIn, JTextField itemDescriptionIn, JTextField startingPriceIn, JTextField buyNowPriceIn, ArrayList<DIBWLot> lots) {
        output.setText("");
        // checks the text fields for name and description are not empty
        if(itemNameIn.getText().length() == 0 || itemDescriptionIn.getText().length() == 0){
            output.setText("Invalid lot details!");
            return;
        }
        // checks that the starting price is valid
        if(startingPriceIn.getText() == null){
            output.setText("Invalid price!");
            return;
        }
        // checks the buy it now price is valid
        if(buyNowPriceIn.getText()  == null){
            output.setText("Invalid buy it now price!");
            return;
        }
        // checks the two input are valid doubles
        try {
            Double.parseDouble(startingPriceIn.getText());
            Double.parseDouble(buyNowPriceIn.getText());
        } catch (Exception e) {
            output.setText("Invalid  price!");
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
        try {
            // attempts to read the status object from the space
            DIBWAuctionStatusObject Counter = (DIBWAuctionStatusObject) auctionSpace.take(new DIBWAuctionStatusObject(),
                    null, 1500);
            // counts the next lot item
            final int lotNumber = Counter.countLot();
            // creates a new lot object with the retrieved data and new lot id
            DIBWLot newLot = new DIBWLot(lotNumber, User.getCurrentUser(), null, itemNameIn.getText(),
                    Double.parseDouble(startingPriceIn.getText()), Double.parseDouble(buyNowPriceIn.getText()),
                    itemDescriptionIn.getText(), false, false, false);
            // writes the new lot to the space
            auctionSpace.write(newLot, txn, 3600000);
            // rewrites the counter to the space
            auctionSpace.write(Counter, txn, Lease.FOREVER);
            if (txn != null) {
                txn.commit();
            }
            itemNameIn.setText("");
            itemDescriptionIn.setText("");
            startingPriceIn.setText("");
            buyNowPriceIn.setText("");
            output.setText("Added Lot: " + lotNumber + "!");
            lots.add(newLot);
            // error handling
        } catch (RemoteException | TransactionException | InterruptedException | UnusableEntryException e) {
            System.err.println("Error: " + e);
            try {
                if (txn != null) {
                    txn.abort();
                }
            } catch (UnknownTransactionException | CannotAbortException | RemoteException ex) {
                System.err.println("Error: " + ex);
            }
        }

    }

    // action listener to check for new lots
    // Believe this is broken in some way and causing a ui bug
    private class NewLotNotifier extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            // get the lot list
            DefaultTableModel model = (DefaultTableModel) lotList.getModel();
            try {
                // try to read the auction status object
                DIBWAuctionStatusObject Counter = (DIBWAuctionStatusObject) auctionSpace.read(new DIBWAuctionStatusObject(), null, 1500);
                // reads the lot based on the lot counter number
                // the newest lot will always have the highest number
                DIBWLot latestLot = (DIBWLot) auctionSpace.read(new DIBWLot(Counter.getLotCounter()), null, 1500);
                //gets the object item from the latest lot
                Object[] insertion = latestLot.returnLotData();
                // adds the latest lot to the array
                lots.add(latestLot);
                // adds the latest lot to the lot list
                model.addRow(insertion);
                // error handling
            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }
    }

    // action listener to look for any updates to the lot
    private class LotChangeNotifier extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            // gets to lot table
            DefaultTableModel model = (DefaultTableModel) lotList.getModel();
            try {
                // attempts to read the lot for an auction updater
                DIBWLotUpdate lotChange = (DIBWLotUpdate) auctionSpace.read(new DIBWLotUpdate(), null, 1500);
                int currentIndex = -1;
                // for loop to go through each retrieved lot updater and match it to a valid lot
                for(int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId().equals(lotChange.getLotId())) {
                        currentIndex = i;
                        break;
                    }
                }
                // sets the lot the the retrieved lot
                DIBWLot lot = lots.get(currentIndex);
                // updates the lot price
                lot.setPrice(lotChange.getLotPrice());
                // remove the old lot from the table
                model.removeRow(currentIndex);
                // add the new lot to the table
                model.addRow(lot.returnLotData());
                //error handling
            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }

    }

    // Notifier to remove lot from the UI
    private class RemoveLotFromAuctionNotifier extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            // generates new table
            DefaultTableModel model = (DefaultTableModel) lotList.getModel();
            try {
                // try to find a remover item from the space
                DIBWLotRemove remover = (DIBWLotRemove) auctionSpace.read(new DIBWLotRemove(), null, 1500);
                // similarly to lot updater the for loop search through all lots and matches them to read lot removers
                int currentIndex = 0;
                for (int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId().equals(remover.getId())) {
                        currentIndex = i;
                        break;
                    }
                }
                // if remover is set to ended set the lot enter to ended
                if(remover.isEnded()){
                    DIBWLot lot = lots.get(currentIndex);
                    lot.setEnded(true);
                    lots.set(currentIndex, lot);
                    model.setValueAt("Ended", currentIndex, 3);
                    // notify the bidder they have won
                    if(User.getCurrentUser().getId().equals(remover.getBuyerName())){
                        JOptionPane.showMessageDialog(null, "You just won " + lot.getName() + "!");
                    }
                }
                // if the remover is set to bought then set the lot to ended
                if(remover.isBoughtOutright()){
                    DIBWLot lot = lots.get(currentIndex);
                    lot.setBoughtOutright(true);
                    lots.set(currentIndex, lot);
                    model.setValueAt("Ended", currentIndex, 3);
                    // Notify the seller their lot was bought
                    if(User.getCurrentUser().getId().equals(lot.getUser().getId())){
                        JOptionPane.showMessageDialog(null, "Lot " + lot.getName() + " was just bought!");
                    }
                }
                // if the lot was removed then remove the lot from the table
                if(remover.isRemoved() && currentIndex > -1){
                    lots.remove(currentIndex);
                    model.removeRow(currentIndex);
                }
                // remove the remover matching the lot id from the space
                auctionSpace.takeIfExists(new DIBWLot(remover.getId()), null, 1000);
                // additional cleanup to remove all related bid objects
                Object o;
                do {
                    o = auctionSpace.takeIfExists(new DIBWBid(remover.getId()), null, 1000);
                } while(o != null);
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
     * JTable Class used to display Lot information
     */
    public static class ListTable extends JTable {

        ListTable(Object[][] data, Object[] columns){
            setModel(new FixedListTableModel(data, columns));
        }
        private static class FixedListTableModel extends DefaultTableModel {
            FixedListTableModel(Object[][] data, Object[] columns){
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

