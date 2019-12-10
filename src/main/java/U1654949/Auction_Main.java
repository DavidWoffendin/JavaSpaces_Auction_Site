package U1654949;

import U1654949.Space_Auction_Items.U1654949_Bid_Counter;
import U1654949.Space_Auction_Items.U1654949_Lot_Counter;
import U1654949.Space_Auction_Items.U1654949_Lot_Space;
import U1654949.User_Interface.List_Card;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Main class designed to run the software package
 */
public class Auction_Main extends JFrame {

    private final ArrayList<U1654949_Lot_Space> lots = new ArrayList<U1654949_Lot_Space>();

    private static JavaSpace auctionSpace;

    /**
     * Auction Room method
     */
    public Auction_Main() {

        Space_Setup();

        final List_Card auctionCard = Interface_Starter();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultTableModel model = auctionCard.getTableModel();
                try {
                    U1654949_Lot_Counter secretary = (U1654949_Lot_Counter) auctionSpace.read(new U1654949_Lot_Counter(), null, Default_Variables.SPACE_TIMEOUT);
                    int i = 0;
                    while(i <= secretary.getItemCounter()) {
                        U1654949_Lot_Space template = new U1654949_Lot_Space(i++ + 1, null, null, null, null, null, false, false);
                        U1654949_Lot_Space latestLot = (U1654949_Lot_Space) auctionSpace.readIfExists(template, null, 1000);
                        if (latestLot != null) {
                            lots.add(latestLot);
                            model.addRow(latestLot.asObjectArray());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private List_Card Interface_Starter() {
        setTitle(Default_Variables.APPLICATION_TITLE + " - " + User.getCurrentUser().getId());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JPanel cards = new JPanel(new CardLayout());
        final List_Card auctionCard = new List_Card(lots, cards);
        cards.add(auctionCard, Default_Variables.AUCTION_CARD);
        cp.add(cards);
        pack();
        setResizable(false);
        setVisible(true);
        return auctionCard;
    }

    private void Space_Setup() {
        auctionSpace = Space_Utils.getSpace();
        if (auctionSpace == null) {
            System.err.println("Failed to find the JavaSpace");
            System.exit(1);
        }
        try {
            if (auctionSpace.read(new U1654949_Lot_Counter(), null, 1000) == null) {
                auctionSpace.write(new U1654949_Lot_Counter(), null, Lease.FOREVER);
            }
            if (auctionSpace.read(new U1654949_Bid_Counter(), null, 1000) == null) {
                auctionSpace.write(new U1654949_Bid_Counter(), null, Lease.FOREVER);
            }
        } catch (UnusableEntryException | TransactionException | RemoteException | InterruptedException e) {
            System.err.println("Error: " + e);
        }
    }

    /**
     * Main function designed to get the username off the user and then start the auction room method
     *
     * @param args
     */
    public static void main(String[] args) {
        String userId = JOptionPane.showInputDialog(null, " Enter your username: ", null);
        if (userId == null || userId.length() == 0) throw new RuntimeException("No username provided!");
        User.setCurrentUser(userId);
        new Auction_Main();
    }
}