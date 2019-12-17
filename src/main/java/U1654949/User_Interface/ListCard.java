package U1654949.User_Interface;

import U1654949.Space_Auction_Items.*;
import U1654949.Space_Utils;
import U1654949.User;
import U1654949.User_Interface.Defaults.Default_Table;
import U1654949.User_Interface.Interface_Helpers.Notifier;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ListCard extends JPanel {

    private final JavaSpace auctionSpace;
    private final TransactionManager transactionManager;
    private final ArrayList<DWLot> lots;
    private final Default_Table lotList;

    public ListCard(final ArrayList<DWLot> lots, final JPanel cards){
        super(new BorderLayout());

        this.lots = lots;
        this.transactionManager = Space_Utils.getManager();
        this.auctionSpace = Space_Utils.getSpace();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final JTextField itemNameIn = new JTextField("", 12);
        inputPanel.add(new JLabel("Item Name: "));
        inputPanel.add(itemNameIn);

        final JTextField itemDescriptionIn = new JTextField("", 1);
        inputPanel.add(new JLabel("Item description: "));
        inputPanel.add(itemDescriptionIn);

        final JTextField startingPriceIn = new JTextField("", 6);
        inputPanel.add(new JLabel("Starting Price: "));
        inputPanel.add(startingPriceIn);

        final JTextField buyNowPriceIn = new JTextField("", 6);
        inputPanel.add(new JLabel("Buy It Now Price: "));
        inputPanel.add(buyNowPriceIn);

        final JTextField resultTextOut = new JTextField("", 6);
        resultTextOut.setEditable(false);
        inputPanel.add(new JLabel("Result: "));
        inputPanel.add(resultTextOut);

        add(inputPanel, BorderLayout.CENTER);

        lotList = new Default_Table(new String[0][6], new String[] {
                "Item Name", "Seller ID", "Current Price", "Buy It Now Price", "Status"
        });

        lotList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                int row = lotList.rowAtPoint(event.getPoint());
                if (event.getClickCount() == 2) {

                    if (lots.get(row).isEnded()) {
                        JOptionPane.showMessageDialog(null, "This item has already ended!");
                        return;
                    }
                    if (lots.get(row).isRemoved()){
                        JOptionPane.showMessageDialog(null, "This item is no longer available!");
                        return;
                    }
                    if (lots.get(row).isBoughtOutright()){
                        JOptionPane.showMessageDialog(null, "This item is no longer available!");
                        return;
                    }
                    cards.add(new LotCard(cards, lots.get(row)), "Bid");
                    ((CardLayout) cards.getLayout()).show(cards, "Bid");
                }
            }
        });

        JScrollPane itemListPanel = new JScrollPane(
                lotList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(itemListPanel, BorderLayout.NORTH);

        JButton addLotButton = new JButton();
        addLotButton.setText("Add Lot");

        addLotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resultTextOut.setText("");

                String itemName = itemNameIn.getText();
                String itemDescription = itemDescriptionIn.getText();
                Number startingPrice = Double.parseDouble(startingPriceIn.getText());
                Number buyNowPrice = Double.parseDouble(buyNowPriceIn.getText());
                Double priceDouble = startingPrice == null ? 0 : startingPrice.doubleValue();
                Double buyNowDouble = buyNowPrice == null ? 0 : buyNowPrice.doubleValue();


                if(itemName.length() == 0 || itemDescription.length() == 0){
                    resultTextOut.setText("Invalid lot details!");
                    return;
                }

                if(startingPrice == null || priceDouble == 0){
                    resultTextOut.setText("Invalid price!");
                    return;
                }

                if(buyNowPrice == null || buyNowDouble == 0){
                    resultTextOut.setText("Invalid buy it now price!");
                    return;
                }

                Transaction transaction = null;
                try {
                    Transaction.Created trc = TransactionFactory.create(transactionManager, 3000);
                    transaction = trc.transaction;
                    DWAuctionStatusObject Counter = (DWAuctionStatusObject) auctionSpace.take(new DWAuctionStatusObject(), null, 1500);
                    final int lotNumber = Counter.countLot();
                    DWLot newLot = new DWLot(lotNumber, User.getCurrentUser(), null, itemName, priceDouble, buyNowDouble, itemDescription, false, false, false);
                    auctionSpace.write(newLot, transaction, 3600000);
                    auctionSpace.write(Counter, transaction, Lease.FOREVER);
                    transaction.commit();
                    itemNameIn.setText("");
                    itemDescriptionIn.setText("");
                    startingPriceIn.setText("");
                    buyNowPriceIn.setText("");
                    resultTextOut.setText("Added Lot: " + lotNumber + "!");

                    lots.add(newLot);
                } catch (RemoteException | LeaseDeniedException | TransactionException | InterruptedException | UnusableEntryException e) {
                    System.err.println("Error: " + e);
                    try {
                        if(transaction != null){
                            transaction.abort();
                        }
                    } catch (UnknownTransactionException | CannotAbortException | RemoteException ex) {
                        System.err.println("Error: " + ex);
                    }
                }
            }
        });

        JPanel bidListingPanel = new JPanel(new FlowLayout());
        bidListingPanel.add(addLotButton);
        add(bidListingPanel, BorderLayout.SOUTH);

        try {
            auctionSpace.notify(new DWLotUpdater(), null, new LotChangeNotifier().getListener(), Lease.FOREVER, null);
            auctionSpace.notify(new DWAuctionStatusObject(), null, new NewLotNotifier().getListener(), Lease.FOREVER, null);
            auctionSpace.notify(new DWLotRemover(), null, new RemoveLotFromAuctionNotifier().getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DefaultTableModel getTableModel(){
        return ((DefaultTableModel) lotList.getModel());
    }

    private class NewLotNotifier extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();
            try {
                DWAuctionStatusObject Counter = (DWAuctionStatusObject) auctionSpace.read(new DWAuctionStatusObject(), null, 1500);
                DWLot latestLot = (DWLot) auctionSpace.read(new DWLot(Counter.getLotCounter()), null, 1500);
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
            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }

    }

    private class LotChangeNotifier extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();
            try {
                DWLotUpdater lotChange = (DWLotUpdater) auctionSpace.read(new DWLotUpdater(), null, 1500);
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
                DWLot lot = lots.get(currentIndex);
                lot.setPrice(lotChange.getLotPrice());
                Object[] insertion = lot.asObjectArray();
                lots.set(currentIndex, lot);
                model.setValueAt(insertion[3], currentIndex, 3);
            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }

    }

    private class RemoveLotFromAuctionNotifier extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            DefaultTableModel model = getTableModel();
            try {
                DWLotRemover remover = (DWLotRemover) auctionSpace.read(new DWLotRemover(), null, 1500);

                int currentIndex = 0;
                for (int i = 0, j = lots.size(); i < j; i++){
                    if (lots.get(i).getId() == remover.getId()) {
                        currentIndex = i;
                        break;
                    }
                }

                if(remover.isEnded()){
                    DWLot lot = lots.get(currentIndex);
                    lot.setEnded(true);
                    lots.set(currentIndex, lot);
                    model.setValueAt("Ended", currentIndex, 4);
                    if(User.getCurrentUser().equals(lot.getUser())){
                        JOptionPane.showMessageDialog(null, "You just won " + lot.getName() + "!");
                    }
                }

                if(remover.isBoughtOutright()){
                    DWLot lot = lots.get(currentIndex);
                    lot.setBoughtOutright(true);
                    lots.set(currentIndex, lot);
                    model.setValueAt("Ended", currentIndex, 4);
                    if(User.getCurrentUser().equals(lot.getUser())){
                        JOptionPane.showMessageDialog(null, "Lot " + lot.getName() + " was just bought!");
                    }
                }

                if(remover.isRemoved() && currentIndex > -1){
                    lots.remove(currentIndex);
                    model.removeRow(currentIndex);
                }

                auctionSpace.takeIfExists(new DWLot(remover.getId()), null, 1000);

                Object o;
                do {
                    o = auctionSpace.takeIfExists(new DWBid(remover.getId()), null, 1000);
                } while(o != null);
            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }

    }

}
