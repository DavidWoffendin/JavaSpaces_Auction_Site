package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.U1654949_Lot_Remover;
import U1654949.Space_Auction_Items.U1654949_Lot_Space;
import U1654949.Space_Utils;

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
 * Class implementing the remove listener for the remove lot button
 */
public class RemoveButtonListener extends MouseAdapter {

    private U1654949_Lot_Space lot;
    private JavaSpace space;
    private TransactionManager manager;


    /**
     * @param lot the lot that this class will perform actions against
     */
    public RemoveButtonListener(U1654949_Lot_Space lot) {
        this.lot = lot;
        this.space = Space_Utils.getSpace();
        this.manager = Space_Utils.getManager();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        JPanel modal = new JPanel();
        modal.add(new JLabel("Are you sure you want to remove the lot?"));

        int result = JOptionPane.showConfirmDialog(null, modal,
                "Remove Lot?", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Transaction transaction = null;
            try {
                Transaction.Created trc = TransactionFactory.create(manager, 3000);
                transaction = trc.transaction;
                U1654949_Lot_Space template = new U1654949_Lot_Space(lot.getId());
                U1654949_Lot_Space updatedLot = (U1654949_Lot_Space) space.read(template, transaction, 1500);
                updatedLot.setRemoved(true);
                space.write(new U1654949_Lot_Remover(lot.getId(), false, true), transaction, 3000);
                transaction.commit();
                lot = updatedLot;
            } catch (RemoteException | LeaseDeniedException | TransactionException | InterruptedException | UnusableEntryException e) {
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
