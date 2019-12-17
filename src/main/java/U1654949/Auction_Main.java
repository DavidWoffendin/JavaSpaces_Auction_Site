package U1654949;

import U1654949.Space_Auction_Items.DWAuctionStatusObject;
import U1654949.Space_Auction_Items.DWLot;
import U1654949.User_Interface.ListCard;
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

    private final ArrayList<DWLot> lots = new ArrayList<>();

    private static JavaSpace auctionSpace;

    /**
     * Auction Room method
     */
    public Auction_Main() {

        Space_Setup();

        final ListCard listCard = Interface_Starter();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultTableModel model = listCard.getTableModel();
                try {
                    DWAuctionStatusObject lotStatus = (DWAuctionStatusObject) auctionSpace.read(new DWAuctionStatusObject(), null, 1500);
                    int i = 0;
                    while (i <= lotStatus.getLotCounter()) {
                        DWLot template = new DWLot(i++ + 1, null, null, null, null, null, null, false, false, false);
                        DWLot nextLot = (DWLot) auctionSpace.readIfExists(template, null, 1000);
                        if (nextLot != null) {
                            lots.add(nextLot);
                            model.addRow(nextLot.asObjectArray());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Main function designed to get the username off the user and then start the auction room method
     *
     * @param args
     */
    public static void main(String[] args) {
        String id = JOptionPane.showInputDialog(null, " Enter your username: ", null);
        if (id == null || id.length() == 0) throw new RuntimeException("No username provided");
        User.setCurrentUser(id);
        new Auction_Main();
    }

    private ListCard Interface_Starter() {
        setTitle("Auction Room - " + User.getCurrentUser().getId());
        Container auctionContainer = getContentPane();
        auctionContainer.setLayout(new BorderLayout());
        JPanel cards = new JPanel(new CardLayout());
        final ListCard listCard = new ListCard(lots, cards);
        cards.add(listCard, "Auction");
        auctionContainer.add(cards);
        pack();
        setBounds(0, 0, 700, 700);
        setResizable(true);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        return listCard;
    }

    private void Space_Setup() {
        auctionSpace = Space_Utils.getSpace();
        if (auctionSpace == null) {
            System.err.println("Failed to find the JavaSpace, please try again");
            System.exit(1);
        }
        try {
            if(auctionSpace.read(new DWAuctionStatusObject(), null, 1000) == null){
                auctionSpace.write(new DWAuctionStatusObject(0, 0), null, Lease.FOREVER);
            }
        } catch (UnusableEntryException | TransactionException | RemoteException | InterruptedException e) {
            System.err.println("Error: " + e);
        }
    }
}