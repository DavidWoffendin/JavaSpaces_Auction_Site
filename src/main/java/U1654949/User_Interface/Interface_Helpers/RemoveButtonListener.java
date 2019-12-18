package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.DWLot;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Class implementing the remove listener for the remove lot button
 */
public class RemoveButtonListener extends Listener {

    private DWLot lot;
    private JavaSpace javaSpace;
    private TransactionManager transactionManager;

    public RemoveButtonListener(DWLot lot) {
        super(lot);
    }


    @Override
    public void mouseClicked(MouseEvent event) {
        JPanel modal = new JPanel();
        modal.add(new JLabel("Are you sure you want to remove the lot?"));
        int result = JOptionPane.showConfirmDialog(null, modal, "Remove Lot?", JOptionPane.OK_CANCEL_OPTION);
        System.out.println("Remove");
        if (result == JOptionPane.OK_OPTION) {
            lotRemove(false, true, false);
        }

    }
}
