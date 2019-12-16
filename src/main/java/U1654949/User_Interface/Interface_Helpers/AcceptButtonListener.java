package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.U1654949_Lot_Remover;
import U1654949.Space_Auction_Items.U1654949_Lot;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

/**
 * Class for the accept bid button
 */
public class AcceptButtonListener extends MouseAdapter {

    private JLabel currentPrice;
    private U1654949_Lot lot;
    private JavaSpace space;
    private TransactionManager manager;


    /**
     * @param lot          Lot for the action listener to perform actions against
     * @param currentPrice Current price of the lot
     */
    public AcceptButtonListener(U1654949_Lot lot, JLabel currentPrice) {
        this.currentPrice = currentPrice;
        this.lot = lot;
        this.space = U1654949.Space_Utils.getSpace();
        this.manager = U1654949.Space_Utils.getManager();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        JPanel modal = new JPanel();
        modal.add(new JLabel("Confirm bid: " + currentPrice.getText() + "?"));

        int result = JOptionPane.showConfirmDialog(null, modal,
                "Accept Bid?", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            Transaction transaction = null;
            try {
                Transaction.Created trc = TransactionFactory.create(manager, 3000);
                transaction = trc.transaction;
                U1654949_Lot updatedLot = (U1654949_Lot) space.read(new U1654949_Lot(lot.getId()), transaction, 1500);
                updatedLot.setEnded(true);
                space.write(new U1654949_Lot_Remover(lot.getId(), true, false), transaction, 3000);
                transaction.commit();
                lot = updatedLot;
            } catch (RemoteException | TransactionException | InterruptedException | UnusableEntryException | LeaseDeniedException e) {
                System.err.println("Error: " + e);

                try {
                    if (transaction != null) {
                        transaction.abort();
                    }
                } catch (RemoteException | CannotAbortException | UnknownTransactionException ex) {
                    System.err.println("Error: " + ex);
                }
            }
        }
    }
}

