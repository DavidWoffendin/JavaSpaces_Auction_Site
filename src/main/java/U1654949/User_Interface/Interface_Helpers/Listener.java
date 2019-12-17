package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Auction_Items.DWLot;
import U1654949.Space_Auction_Items.DWLotRemover;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.awt.event.MouseAdapter;
import java.rmi.RemoteException;

/**
 * Class for the adding the lot remove object
 */
public class Listener extends MouseAdapter {

    private DWLot lot;
    private JavaSpace javaSpace;
    private TransactionManager transactionManager;

    public Listener(DWLot lot) {
        this.lot = lot;
        this.javaSpace = U1654949.Space_Utils.getSpace();
        this.transactionManager = U1654949.Space_Utils.getManager();
    }

    public void lotRemove(Boolean end, Boolean remove, Boolean bought) {
        Transaction transaction = null;
        try {
            Transaction.Created trc = TransactionFactory.create(transactionManager, 3000);
            transaction = trc.transaction;
            DWLot updatedLot = (DWLot) javaSpace.read(new DWLot(lot.getId()), transaction, 1500);
            updatedLot.setBoughtOutright(true);
            javaSpace.write(new DWLotRemover(lot.getId(), end, remove, bought), transaction, 3000);
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


