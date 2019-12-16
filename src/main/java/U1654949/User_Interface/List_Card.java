package U1654949.User_Interface;

import U1654949.Space_Auction_Items.*;
import U1654949.Space_Utils;
import U1654949.User;
import U1654949.User_Interface.Defaults.Default_Table;
import U1654949.User_Interface.Defaults.Default_Text;
import U1654949.User_Interface.Interface_Helpers.Common_Functions;
import U1654949.User_Interface.Interface_Helpers.Notifier;

import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class List_Card extends JPanel {

    private final JavaSpace auctionSpace;
    private final TransactionManager manager;
    private final ArrayList<U1654949_Lot_Space> lots;
    private final Default_Table lotTable;

    public List_Card(final ArrayList<U1654949_Lot_Space> lots, final JPanel cards){
        super(new BorderLayout());

        this.lots = lots;
        this.manager = Space_Utils.getManager();
        this.auctionSpace = Space_Utils.getSpace();

        JPanel fieldInputPanel = new JPanel(new GridLayout(4, 2));
        fieldInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final JTextField itemNameIn = new JTextField("", 12);
        fieldInputPanel.add(new JLabel("Name of Item: "));
        fieldInputPanel.add(itemNameIn);

        final JTextField itemDescriptionIn = new JTextField("", 1);
        fieldInputPanel.add(new JLabel("Item description: "));
        fieldInputPanel.add(itemDescriptionIn);

        final JTextField startingPriceIn = new JTextField("", 6);
        fieldInputPanel.add(new JLabel("Starting Price: "));
        fieldInputPanel.add(startingPriceIn);

        final Default_Text resultTextOut = new Default_Text();
        fieldInputPanel.add(new JLabel("Result: "));
        fieldInputPanel.add(resultTextOut);

        // Add the layout to the panel
        add(fieldInputPanel, BorderLayout.NORTH);

        lotTable = new Default_Table(new String[0][5], new String[] {
                "Lot ID", "Item Name", "Seller ID", "Current Price", "Status"
        });

        lotTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                int row = lotTable.rowAtPoint(event.getPoint());

                if (event.getClickCount() == 2) {

                    if (lots.get(row).isEnded()) {
                        JOptionPane.showMessageDialog(null, "This item has already ended!");
                        return;
                    }

                    if (lots.get(row).isRemoved()){
                        JOptionPane.showMessageDialog(null, "This item is no longer available!");
                        return;
                    }
                    cards.add(new Lot_Card(cards, lots.get(row)), "Bid");
                    ((CardLayout) cards.getLayout()).show(cards, "Bid");
                }
            }
        });

        JScrollPane itemListPanel = new JScrollPane(
                lotTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(itemListPanel, BorderLayout.CENTER);

        JButton addLotButton = new JButton();
        addLotButton.setText("Add Auction Item");

        addLotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resultTextOut.setText("");

                String itemName = itemNameIn.getText();
                String itemDescription = itemDescriptionIn.getText();
                Number startingPrice = Common_Functions.getTextAsNumber(startingPriceIn);
                Double potentialDouble = startingPrice == null ? 0 : startingPrice.doubleValue();

                if(itemName.length() == 0 || itemDescription.length() == 0){
                    resultTextOut.setText("Invalid item details!");
                    return;
                }

                if(startingPrice == null || potentialDouble == 0){
                    resultTextOut.setText("Invalid price!");
                    return;
                }

                Transaction transaction = null;
                try {
                    Transaction.Created trc = TransactionFactory.create(manager, 3000);
                    transaction = trc.transaction;
                    U1654949_Lot_Status_Object Counter = (U1654949_Lot_Status_Object) auctionSpace.take(new U1654949_Lot_Status_Object(), null, 1500);
                    final int lotNumber = Counter.countNewItem();
                    U1654949_Lot_Space newLot = new U1654949_Lot_Space(lotNumber, User.getCurrentUser(), null, itemName, potentialDouble, itemDescription, false, false);

                    auctionSpace.write(newLot, transaction, 3600000);
                    auctionSpace.write(Counter, transaction, Lease.FOREVER);

                    transaction.commit();
                    itemNameIn.setText("");
                    itemDescriptionIn.setText("");
                    startingPriceIn.setText("");
                    resultTextOut.setText("Added Lot #" + lotNumber + "!");

                    lots.add(newLot);
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
        });

        JPanel bidListingPanel = new JPanel(new FlowLayout());
        bidListingPanel.add(addLotButton);
        add(bidListingPanel, BorderLayout.SOUTH);

        try {
            auctionSpace.notify(new U1654949_Lot_Updater(), null, new LotChangeNotifier().getListener(), Lease.FOREVER, null);
            auctionSpace.notify(new U1654949_Lot_Status_Object(), null, new NewLotNotifier().getListener(), Lease.FOREVER, null);
            auctionSpace.notify(new U1654949_Lot_Remover(), null, new RemoveLotFromAuctionNotifier().getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DefaultTableModel getTableModel(){
        return ((DefaultTableModel) lotTable.getModel());
    }

    private class NewLotNotifier extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();

            try {
                U1654949_Lot_Status_Object Counter = (U1654949_Lot_Status_Object) auctionSpace.read(new U1654949_Lot_Status_Object(), null, 1500);
                U1654949_Lot_Space latestLot = (U1654949_Lot_Space) auctionSpace.read(new U1654949_Lot_Space(Counter.getItemCounter()), null, 1500);
                Object[] insertion = latestLot.asObjectArray();

                int currentIndex = -1;
                for(int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId().intValue() == latestLot.getId().intValue()) {
                        currentIndex = i;
                        break;
                    }
                }
                lots.add(latestLot);
                model.addRow(insertion);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class LotChangeNotifier extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();

            try {
                U1654949_Lot_Updater lotChange = (U1654949_Lot_Updater) auctionSpace.read(new U1654949_Lot_Updater(), null, 1500);

                int currentIndex = -1;
                for(int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId().equals(lotChange.getLotId())) {
                        currentIndex = i;
                        break;
                    }
                }

                if(currentIndex == -1){
                    return;
                }

                U1654949_Lot_Space lot = lots.get(currentIndex);

                lot.setPrice(lotChange.getLotPrice());

                Object[] insertion = lot.asObjectArray();

                lots.set(currentIndex, lot);
                model.setValueAt(insertion[3], currentIndex, 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class RemoveLotFromAuctionNotifier extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();

            try {
                U1654949_Lot_Remover remover = (U1654949_Lot_Remover) auctionSpace.read(new U1654949_Lot_Remover(), null, 1500);

                int currentIndex = 0;
                for (int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId() == remover.getId()) {
                        currentIndex = i;
                        break;
                    }
                }

                if(remover.isEnded()){
                    System.out.println(lots.size());
                    U1654949_Lot_Space lot = lots.get(currentIndex);

                    lot.setEnded(true);

                    lots.set(currentIndex, lot);
                    model.setValueAt("Ended", currentIndex, 4);


                    if(User.getCurrentUser().equals(lot.getUser())){
                        JOptionPane.showMessageDialog(null, "You just won " + lot.getName() + "!");
                    }
                }

                if(remover.isRemoved() && currentIndex > -1){
                    lots.remove(currentIndex);
                    model.removeRow(currentIndex);
                }

                auctionSpace.takeIfExists(new U1654949_Lot_Space(remover.getId()), null, 1000);

                Object o;
                do {
                    o = auctionSpace.takeIfExists(new U1654949_Bid_Space(remover.getId()), null, 1000);
                } while(o != null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

