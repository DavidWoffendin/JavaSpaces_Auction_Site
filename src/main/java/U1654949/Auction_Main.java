package U1654949;

import U1654949.Space_Auction_Items.U1654949_Auction_Status_Object;
import U1654949.Space_Auction_Items.U1654949_Lot;
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

    private final ArrayList<U1654949_Lot> lots = new ArrayList<U1654949_Lot>();

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
                    U1654949_Auction_Status_Object counter = (U1654949_Auction_Status_Object) auctionSpace.read(new U1654949_Auction_Status_Object(), null, 1500);
                    int i = 0;
                    while(i <= counter.getLotCounter()) {
                        U1654949_Lot template = new U1654949_Lot(i++ + 1, null, null, null, null, null, null, false, false);
                        U1654949_Lot latestLot = (U1654949_Lot) auctionSpace.readIfExists(template, null, 1000);
                        if (latestLot != null) {
                            lots.add(latestLot);
                            model.addRow(latestLot.asObjectArray());
                        }
                    }
                    System.out.println(counter.getLotCounter());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private List_Card Interface_Starter() {
        setTitle("Auction Room - " + User.getCurrentUser().getId());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JPanel cards = new JPanel(new CardLayout());
        final List_Card auctionCard = new List_Card(lots, cards);
        cards.add(auctionCard, "Auction");
        cp.add(cards);
        pack();
        setResizable(true);
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
            if(auctionSpace.read(new U1654949_Auction_Status_Object(), null, 1000) == null){
                auctionSpace.write(new U1654949_Auction_Status_Object(0, 0), null, Lease.FOREVER);
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