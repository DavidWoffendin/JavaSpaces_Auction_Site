package U1654949;

import U1654949.spacedataobjects.DIBWAuctionStatusObject;
import U1654949.spacedataobjects.DIBWLot;
import U1654949.userinterfacecards.ListCard;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *  Main class designed to run the software package
 *  Class operates by first ensuring the Auction Status Object is in the space
 *  Then I creates an empty container and loads the list card
 */
public class AuctionMain extends JFrame {

    private final ArrayList<DIBWLot> lots = new ArrayList<>();

    /**
     * Simple Method to call space setup and container generator
     */
    private AuctionMain() {
        spaceSetup();
        containerGenerator();
    }

    /**
     * Main function designed to get the username off the user and then start the auction room method
     *
     * @param args args parameter
     */
    public static void main(String[] args) {
        String id = JOptionPane.showInputDialog(null, " Enter your username: ", null);
        if (id == null || id.length() == 0) throw new RuntimeException("No username provided");
        User.setCurrentUser(id);
        new AuctionMain();
    }

    /**
     * Container Generator method simple creates an empty container for use later
     */
    private void containerGenerator() {
        setTitle("Auction Room - " + User.getCurrentUser().getId());
        Container auctionContainer = getContentPane();
        auctionContainer.setLayout(new BorderLayout());
        JPanel cards = new JPanel(new CardLayout());
        final ListCard ListCard = new ListCard(lots, cards);
        cards.add(ListCard, "Auctioneer");
        auctionContainer.add(cards);
        pack();
        setBounds(0, 0, 700, 600);
        setResizable(false);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
    }

    /***************************************************************************************
     *    This Space setup method was based of Gary Allen's Start print queue class
     *
     *    Title: JavaSpacesPrintQueue
     *    Author: Gary Allen
     *    Date: 5/11/2019
     *    Code version: Commit d92df04377da73d0ff4b328a8b0f6e4e47c0ab79
     *    Availability: https://github.com/GaryAllenGit/JavaSpacesPrintQueue/blob/master/src/StartPrintQueue.java
     *
     ***************************************************************************************/
    private void spaceSetup() {
        JavaSpace auctionSpace = SpaceUtils.getSpace();
        try {
            if(auctionSpace.read(new DIBWAuctionStatusObject(), null, 1000) == null){
                DIBWAuctionStatusObject aso = new DIBWAuctionStatusObject(0, 0);
                auctionSpace.write(aso, null, Lease.FOREVER);
                System.out.println("Auction Status object added to space");
            } else {
                System.out.println("Auction Status object is already in the space");
            }
        } catch (UnusableEntryException | TransactionException | RemoteException | InterruptedException e) {
            System.err.println("Error: " + e);
        }
    }
}