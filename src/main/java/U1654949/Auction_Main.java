package U1654949;

import U1654949.Space_Auction_Items.U1654949_Bid_Counter;
import U1654949.Space_Auction_Items.U1654949_Lot_Counter;
import U1654949.Space_Auction_Items.U1654949_Lot_Space;
import U1654949.User_Interface.List_Card;
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
public class Auction_Main extends JFrame {

    /**
     * A list of lot items which are being tracked in the space.
     */
    private final ArrayList<U1654949_Lot_Space> lots = new ArrayList<U1654949_Lot_Space>();

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

        User.setCurrentUser(userId);

        new Auction_Main();
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
    public Auction_Main() {

        // Initialise a local Space, exit on failure
        space = Space_Utils.getSpace();
        if (space == null){
            System.err.println("Failed to find the JavaSpace");
            System.exit(1);
        }

        try {
            // Ensure an IWsLotSecretary lives in the Space
            Object o = space.read(new U1654949_Lot_Counter(), null, 1000);
            if(o == null){
                space.write(new U1654949_Lot_Counter(0), null, Lease.FOREVER);
            }

            // Ensure an IWsBidSecretary lives in the Space
            o = space.read(new U1654949_Bid_Counter(), null, 1000);
            if(o == null){
                space.write(new U1654949_Bid_Counter(0), null, Lease.FOREVER);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1); // We cannot do anything with no good Space connection
        }

        // Set the application title as well as the username
        setTitle(Default_Variables.APPLICATION_TITLE + " - " + User.getCurrentUser().getId());

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
        final List_Card listCard = new List_Card(lots, cards);

        // Add the card to the CardLayout
        cards.add(listCard, Default_Variables.AUCTION_CARD);

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
                DefaultTableModel model = listCard.getTableModel();
                try {
                    // Read the latest known version of the IWsSecretary from the Space
                    // It could be necessary to re-read this on each iteration on the loop,
                    // but it does not seem to be needed for an application of this scale.
                    U1654949_Lot_Counter secretary = (U1654949_Lot_Counter) space.read(new U1654949_Lot_Counter(), null, Default_Variables.SPACE_TIMEOUT);

                    int i = 0;
                    // Loop for all item ids
                    while(i <= secretary.getItemCounter()) {

                        // Search for the next template in the Space
                        U1654949_Lot_Space template = new U1654949_Lot_Space(i++ + 1, null, null, null, null, null, false, false);

                        // If the object exists in the space
                        U1654949_Lot_Space latestLot = (U1654949_Lot_Space) space.readIfExists(template, null, 1000);

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
