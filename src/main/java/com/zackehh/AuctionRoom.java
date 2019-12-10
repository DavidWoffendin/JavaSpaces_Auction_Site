package com.zackehh;

import com.zackehh.auction.IWsLot;
import com.zackehh.auction.secretary.IWsBidSecretary;
import com.zackehh.auction.secretary.IWsLotSecretary;
import com.zackehh.ui.cards.AuctionCard;
import com.zackehh.util.Constants;
import com.zackehh.util.SpaceUtils;
import com.zackehh.util.UserUtils;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * The main entry into the program, which simply creates the
 * base of the UI and sets up any required fields in the
 * SpaceUtils and UserUtils classes. Also handles loading of
 * the initial objects into the main AuctionCard view, via a
 * background thread.
 */
public class AuctionRoom extends JFrame {

    /**
     * A list of lot items which are being tracked in the space.
     */
    private final ArrayList<IWsLot> lots = new ArrayList<IWsLot>();

    /**
     * The common JavaSpace instance, stored privately.
     */
    private final JavaSpace space;

    /**
     * Main entry to the AuctionRoom. This prompts for a
     * user's name as a prerequisite to using the application.
     * Should this not be provided, the program will exit.
     * This is where it is defined as to which hostname we
     * will search for a JavaSpace in.
     *
     * @param args      the main program arguments
     */
    public static void main(String[] args) {

        String userId = JOptionPane.showInputDialog(null, " Enter your student ID or username: ", null);

        if(userId == null || userId.length() == 0){
            System.err.println("No user credentials provided!");
            System.exit(1);
        }

        SpaceUtils.setHostname(args.length == 0 ? "localhost" : args[0]);
        UserUtils.setCurrentUser(userId);

        new AuctionRoom();
    }

    /**
     * Initializes a JavaSpace and ensures there is a
     * Secretary in the space, as these preconditions
     * are required to allow the user to continue. Should
     * these conditions be met, the UI will be created.
     * Existing lots are loaded in a background Thread and
     * pushed to the list of lots displayed inside the
     * AuctionCard.
     */
    public AuctionRoom() {

        // Initialise a local Space, exit on failure
        space = SpaceUtils.getSpace();
        if (space == null){
            System.err.println("Failed to find the JavaSpace");
            System.exit(1);
        }

        try {
            // Ensure an IWsLotSecretary lives in the Space
            Object o = space.read(new IWsLotSecretary(), null, 1000);
            if(o == null){
                space.write(new IWsLotSecretary(0), null, Lease.FOREVER);
            }

            // Ensure an IWsBidSecretary lives in the Space
            o = space.read(new IWsBidSecretary(), null, 1000);
            if(o == null){
                space.write(new IWsBidSecretary(0), null, Lease.FOREVER);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1); // We cannot do anything with no good Space connection
        }

        // Set the application title as well as the username
        setTitle(Constants.APPLICATION_TITLE + " - " + UserUtils.getCurrentUser().getId());

        // Exit on the exit button press
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });

        // Set the container BorderLayout
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        // Create a new card layout
        JPanel cards = new JPanel(new CardLayout());

        // Create a new AuctionCard
        final AuctionCard auctionCard = new AuctionCard(lots, cards);

        // Add the card to the CardLayout
        cards.add(auctionCard, Constants.AUCTION_CARD);

        // Add the CardLayout to the Container
        cp.add(cards);

        // Pack the UI and set the frame
        pack();
        setResizable(false);
        setVisible(true);

        // Start the initial loading of the existing items
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultTableModel model = auctionCard.getTableModel();
                try {
                    // Read the latest known version of the IWsSecretary from the Space
                    // It could be necessary to re-read this on each iteration on the loop,
                    // but it does not seem to be needed for an application of this scale.
                    IWsLotSecretary secretary = (IWsLotSecretary) space.read(new IWsLotSecretary(), null, Constants.SPACE_TIMEOUT);

                    int i = 0;
                    // Loop for all item ids
                    while(i <= secretary.getItemNumber()) {

                        // Search for the next template in the Space
                        IWsLot template = new IWsLot(i++ + 1, null, null, null, null, null, false, false);

                        // If the object exists in the space
                        IWsLot latestLot = (IWsLot) space.readIfExists(template, null, 1000);

                        // Add any existing lots to the tables
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
}
