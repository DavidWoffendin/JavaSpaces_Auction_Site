package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.DWLot;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Class for the accept bid button
 */
public class BuyItNowButtonListener extends Listener {

    private DWLot lot;
    private JavaSpace javaSpace;
    private TransactionManager transactionManager;

    /**
     * @param lot Lot for the action listener to perform actions against
     */
    public BuyItNowButtonListener(DWLot lot) {
        super(lot);
    }

    /**
     * @param lot Lot for the action listener to perform actions against
     */

    @Override
    public void mouseClicked(MouseEvent event) {
        int result = JOptionPane.showConfirmDialog(null, "Do you want to buy this lot?", "Buy It Now?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            lotRemove(false, false, true);
        }
    }
}


