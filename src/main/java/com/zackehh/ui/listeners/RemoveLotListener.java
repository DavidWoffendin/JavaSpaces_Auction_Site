package com.zackehh.ui.listeners;

import com.zackehh.auction.U1654949_Lot_Class;
import com.zackehh.auction.U1654949_Lot_Remover;
import com.zackehh.util.Constants;
import com.zackehh.util.SpaceUtils;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Listener for the LotCard views which allow a buyer to
 * place a bid on an item. Uses Transactions to ensure
 * that the bid is properly accepted in the space. This
 * does not touch the UI, which will be handled by the use
 * of notifier in the RemoteEventListener implementations.
 *
 */
public class RemoveLotListener extends MouseAdapter {

    /**
     * The lot the user wishes to place a bid on.
     */
    private U1654949_Lot_Class lot;

    /**
     * The common JavaSpace instance, stored privately.
     */
    private JavaSpace space;

    /**
     * The common TransactionManager instance, stored privately.
     */
    private TransactionManager manager;

    /**
     * Initializes with a lot item, which is used when updating
     * the space with the new associated bid identifiers.
     *
     * @param lot       the lot being bid on
     */
    public RemoveLotListener(U1654949_Lot_Class lot){
        this.lot = lot;
        this.manager = SpaceUtils.getManager();
        this.space = SpaceUtils.getSpace();
    }

    /**
     * Creates a modal for the user to input their bid amount and
     * select whether they wish their bid to be private or public.
     * Uses a transaction to ensure that the bid is registered in the
     * space along with any other changes needed, such as lot changes.
     * This does not touch the UI, which is controlled by the registered
     * RemoteEventListeners.
     *
     * @param event             the mouse event
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);
        Transaction transaction = null;
        try {
            // Create Transaction
            Transaction.Created trc = TransactionFactory.create(manager, 3000);
            transaction = trc.transaction;

            // Refresh current IWsLot from the Space
            U1654949_Lot_Class template = new U1654949_Lot_Class(lot.getId());
            U1654949_Lot_Class updatedLot = (U1654949_Lot_Class) space.read(template, transaction, Constants.SPACE_TIMEOUT);

            // Mark the lot for removal
            updatedLot.setMarkedForRemoval(true);

            // Add a new IWsItemRemover recording the changes
            space.write(new U1654949_Lot_Remover(lot.getId(), false, true), transaction, Constants.TEMP_OBJECT);

            // Commit the transaction
            transaction.commit();

            // Store the change locally
            lot = updatedLot;
        } catch(Exception e) {
            e.printStackTrace();
            try {
                if(transaction != null){
                    transaction.abort();
                }
            } catch(Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
