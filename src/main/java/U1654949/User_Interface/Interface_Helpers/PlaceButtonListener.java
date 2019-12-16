package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.*;
import U1654949.User;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

/**
 *
 */
public class PlaceButtonListener extends MouseAdapter {

    private U1654949_Lot_Space lot;
    private JavaSpace space;
    private TransactionManager manager;


    /**
     * @param lot
     */
    public PlaceButtonListener(U1654949_Lot_Space lot) {
        this.lot = lot;
        this.space = U1654949.Space_Utils.getSpace();
        this.manager = U1654949.Space_Utils.getManager();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        JPanel modal = new JPanel(new GridLayout(2, 2));
        JTextField bidEntry = new JTextField();

        modal.add(new JLabel("Bid Amount: "));
        modal.add(bidEntry);

        int result = JOptionPane.showConfirmDialog(null, modal,
                "Please enter your bid details:", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Double bid;
            String bidString = bidEntry.getText();
            if (bidString.matches("(?=.)^\\$?(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+)?(\\.[0-9]{1,2})?$") && (bid = Double.parseDouble(bidString)) > 0 && bid > lot.getPrice()) {
                Transaction transaction = null;
                try {
                    Transaction.Created trc = TransactionFactory.create(manager, 3000);
                    transaction = trc.transaction;

                    U1654949_Bid_Status_Object counter = (U1654949_Bid_Status_Object) space.take(new U1654949_Bid_Status_Object(), transaction, 1500);
                    U1654949_Lot_Space updatedLot = (U1654949_Lot_Space) space.take(new U1654949_Lot_Space(lot.getId()), transaction, 1500);

                    int bidNumber = counter.countNewItem();

                    updatedLot.getBids().add(bidNumber);
                    updatedLot.setPrice(bid);

                    U1654949_Bid_Space newBid = new U1654949_Bid_Space(bidNumber, User.getCurrentUser(), lot.getId(), bid);

                    space.write(new U1654949_Lot_Updater(lot.getId(), bid), transaction, 3000);
                    space.write(updatedLot, transaction, 3600000);
                    space.write(newBid, transaction, 5000000);
                    space.write(counter, transaction, Lease.FOREVER);

                    transaction.commit();

                    lot = updatedLot;
                } catch (TransactionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException | LeaseDeniedException | UnusableEntryException e) {
                    System.err.println("Error: " + e);
                    try {
                        if (transaction != null) {
                            transaction.abort();
                        }
                    } catch (RemoteException | CannotAbortException | UnknownTransactionException ex) {
                        System.err.println("Error: " + ex);
                    }
                }
            } else {
                // Record invalid bid
                JOptionPane.showMessageDialog(null, "Invalid bid entered!");
            }
        }
    }
}