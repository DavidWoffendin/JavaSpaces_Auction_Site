package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.DWLotRemover;
import U1654949.Space_Auction_Items.DWLot;

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
public class AcceptButtonListener extends Listener {

    private JLabel price;
    private DWLot lot;
    private JavaSpace javaSpace;
    private TransactionManager transactionManager;

    public AcceptButtonListener(DWLot lot, JLabel price) {
        super(lot);
        this.price = price;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        JPanel modal = new JPanel();
        modal.add(new JLabel("Confirm bid: " + price.getText() + "?"));
        int result = JOptionPane.showConfirmDialog(null, modal,"Accept Bid?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            lotRemove(true, false, false);
        }
    }
}

