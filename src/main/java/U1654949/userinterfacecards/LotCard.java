package U1654949.userinterfacecards;

import U1654949.spaceauctionitems.*;
import U1654949.SpaceUtils;
import U1654949.User;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.NumberFormat;
import java.util.*;

public class LotCard extends JPanel {

    private final JavaSpace javaSpace;
    private LotTable bidsList;
    private DWLot lot;
    private TransactionManager transactionManager;

    private final Vector<Vector<String>> bids;

    private JButton end;
    private JButton placeBid;
    private JButton buyNow;

    private JLabel price;
    private JLabel buyItNowPrice;
    private JLabel priceLabel;
    private JLabel buyNowLabel;
    private final JPanel card;

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);

    public LotCard(final JPanel card, DWLot lotForCard) {
        super();
        this.card = card;
        this.javaSpace = SpaceUtils.getSpace();
        this.transactionManager = SpaceUtils.getManager();

        DWLot baseLot = lotForCard;
        setLayout(new BorderLayout());

        try {
            DWLot templateLot = new DWLot(lotForCard.getId());
            baseLot = (DWLot) javaSpace.read(templateLot, null, 1500);
        } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
            System.err.println("Error: " + e);
        }
        this.lot = baseLot;

        bids = getVectorBidMatrix(lot);

        try {
            BidListener bidListener = new BidListener();
            LotListener endedListener = new LotListener();

            DWBid bTemplate = new DWBid(null, null, lot.getId(), null);
            DWLotRemover rTemplate = new DWLotRemover(lot.getId(), null, null, null);

            javaSpace.notify(bTemplate, null, bidListener.getListener(), Lease.FOREVER, null);
            javaSpace.notify(rTemplate, null, endedListener.getListener(), Lease.FOREVER, null);

        } catch (TransactionException | RemoteException e) {
            System.err.println("Error: " + e);
        }

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        cardButtonsCreator(infoPanel);
        bidTableGetter(infoPanel);
        lotDetailsGetter();
    }

    private void cardButtonsCreator(JPanel infoPanel) {
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                card.remove(LotCard.this);
            }
        });
        infoPanel.add(back, BorderLayout.NORTH);

        placeBid = new JButton("Place Bid");
        buyNow = new JButton("Buy it now");
        end = new JButton("Accept Latest Bid");

        price = new JLabel();
        buyItNowPrice = new JLabel();

        if(!lot.isEnded()) {
            if (User.getCurrentUser().equals(lot.getUser())) {
                if(lot.getLastBid() == null){
                    end.setText("Remove Lot");
                    end.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            removeLot();
                        }
                    });
                } else {
                    end.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            endLot();
                        }
                    });
                }
                infoPanel.add(end, BorderLayout.CENTER);
            } else {

                placeBid.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        addBid();
                    }
                });
                buyNow.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        buyLot();
                    }
                });
                infoPanel.add(placeBid, BorderLayout.CENTER);
                infoPanel.add(buyNow,BorderLayout.SOUTH);
            }
        }
    }

    private void addBid() {
        JPanel modal = new JPanel(new GridLayout(2, 2));
        JTextField bidEntry = new JTextField();
        modal.add(new JLabel("Bid Amount: "));
        modal.add(bidEntry);
        int result = JOptionPane.showConfirmDialog(null, modal,"Please enter your bid details:", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Double bid;
            String bidString = bidEntry.getText();
            if (bidString.matches("(?=.)^\\$?(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+)?(\\.[0-9]{1,2})?$") && (bid = Double.parseDouble(bidString)) > 0 && bid > lot.getPrice()) {
                Transaction transaction = null;
                try {
                    Transaction.Created trc = TransactionFactory.create(transactionManager, 3000);
                    transaction = trc.transaction;
                    DWAuctionStatusObject bidCounter = (DWAuctionStatusObject) javaSpace.take(new DWAuctionStatusObject(), transaction, 1500);
                    DWLot updatedLot = (DWLot) javaSpace.take(new DWLot(lot.getId()), transaction, 1500);
                    int bidNumber = bidCounter.countBid();
                    updatedLot.getBids().add(bidNumber);
                    updatedLot.setPrice(bid);
                    DWBid newBid = new DWBid(bidNumber, User.getCurrentUser(), lot.getId(), bid);
                    javaSpace.write(new DWLotUpdater(lot.getId(), bid), transaction, 3000);
                    javaSpace.write(updatedLot, transaction, 3600000);
                    javaSpace.write(newBid, transaction, 5000000);
                    javaSpace.write(bidCounter, transaction, Lease.FOREVER);
                    transaction.commit();
                    lot = updatedLot;
                } catch (TransactionException | InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException | LeaseDeniedException | UnusableEntryException e) {
                    System.err.println("Error: " + e);
                    try {
                        if (transaction != null) {
                            transaction.abort();
                        }
                    } catch (RemoteException | CannotAbortException | UnknownTransactionException ex) {
                        System.err.println("Error: " + ex);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid bid entered!");
            }
        }
    }

    private void buyLot() {
        int result = JOptionPane.showConfirmDialog(null, "Do you want to buy this lot?", "Buy It Now?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            removeLot(false, false, true);
        }
    }

    private void endLot() {
        JPanel modal = new JPanel();
        modal.add(new JLabel("Confirm bid: " + price.getText() + "?"));
        int result = JOptionPane.showConfirmDialog(null, modal,"Accept Bid?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            removeLot(true, false, false);
        }
    }

    private void removeLot() {
        JPanel modal = new JPanel();
        modal.add(new JLabel("Are you sure you want to remove the lot?"));
        int result = JOptionPane.showConfirmDialog(null, modal, "Remove Lot?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            removeLot(false, true, false);
        }
    }

    private void removeLot(Boolean end, Boolean remove, Boolean bought) {
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

    private void bidTableGetter(JPanel infoPanel) {
        bidsList = new LotTable(bids, new Vector<String>() {{
            add("Buyer ID");
            add("Bid Amount");
        }});
        JScrollPane itemListPanel = new JScrollPane(bidsList);
        add(itemListPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.NORTH);
    }

    private void lotDetailsGetter() {
        JPanel lotDetails = new JPanel(new GridLayout(6, 2));
        lotDetails.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel l = new JLabel("ID - ", SwingConstants.RIGHT);
        lotDetails.add(l);
        JLabel textLabel = new JLabel(String.valueOf(lot.getId()));
        l.setLabelFor(textLabel);
        lotDetails.add(textLabel);

        JLabel l2 = new JLabel("User - ", SwingConstants.RIGHT);
        lotDetails.add(l2);
        JLabel textLabel2 = new JLabel(String.valueOf(lot.getUserId()));
        l2.setLabelFor(textLabel2);
        lotDetails.add(textLabel2);

        JLabel l3 = new JLabel("Lot Name - ", SwingConstants.RIGHT);
        lotDetails.add(l3);
        JLabel textLabel3 = new JLabel(String.valueOf(lot.getName()));
        l3.setLabelFor(textLabel3);
        lotDetails.add(textLabel3);

        JLabel l4 = new JLabel("Lot Description - ", SwingConstants.RIGHT);
        lotDetails.add(l4);
        JLabel textLabel4 = new JLabel(String.valueOf(lot.getDescription()));
        l4.setLabelFor(textLabel4);
        lotDetails.add(textLabel4);

        if(lot.isEnded()){
            priceLabel = new JLabel("Won by " + bids.get(0).get(0) + " -", SwingConstants.RIGHT);
            price.setText(" Price: " + nf.format(lot.getPrice()));
            buyNowLabel = new JLabel("Buy it Now Price: ", SwingConstants.RIGHT);
            buyItNowPrice.setText(nf.format(lot.getBuyNowPricePrice()));
        } else {
            priceLabel = new JLabel("Current Price: ", SwingConstants.RIGHT);
            price.setText(nf.format(lot.getPrice()));
            buyNowLabel = new JLabel("Buy it Now Price: ", SwingConstants.RIGHT);
            buyItNowPrice.setText(nf.format(lot.getBuyNowPricePrice()));
        }

        priceLabel.setLabelFor(price);
        lotDetails.add(priceLabel);
        lotDetails.add(price);
        lotDetails.add(buyNowLabel);
        lotDetails.add(buyItNowPrice);
        add(lotDetails, BorderLayout.SOUTH);
    }

    public static ArrayList<DWBid> getBidHistory(DWLot lot) {
        JavaSpace space = SpaceUtils.getSpace();
        ArrayList<DWBid> bidHistory = new ArrayList<DWBid>();
        try {
            DWLot refreshedLot = (DWLot) space.read(new DWLot(lot.getId()), null, 1500);
            ArrayList<Integer> bids = refreshedLot.getBids();
            if(bids.size() == 0){
                return bidHistory;
            }
            for(Integer bidId : bids){
                DWBid template = new DWBid(bidId, null, lot.getId(), null);
                DWBid bidItem = ((DWBid) space.read(template, null, 1500));
                bidHistory.add(bidItem);
            }
        } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
            System.err.println("Error: " + e);
        }
        Collections.sort(bidHistory, new Comparator<DWBid>() {
            @Override
            public int compare(DWBid bid1, DWBid bid2) {
                return bid2.getPrice().compareTo(bid1.getPrice());
            }
        });
        return bidHistory;
    }

    public static Vector<Vector<String>> getVectorBidMatrix(DWLot lot){
        final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);
        ArrayList<DWBid> bids = getBidHistory(lot);
        Vector<Vector<String>> values = new Vector<Vector<String>>();
        for(int iY = 0; iY < bids.size(); iY++){
            final DWBid bid = bids.get(iY);
            values.add(iY, new Vector<String>(){{
                add(bid.getUser().getId());
                add(nf.format(bid.getPrice()));
            }});
        }
        return values;
    }

    private class BidListener extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            try {
                final DWLot latestLot = (DWLot) javaSpace.read(new DWLot(lot.getId()), null, 1500);
                final DWBid latestBid = (DWBid) javaSpace.read(new DWBid(latestLot.getLastBid()), null, 1500);
                Vector<String> insertion = new Vector<String>(){{
                    add(latestBid.getUser().getId());
                    add(nf.format(latestBid.getPrice()));
                }};
                if(latestLot.getLastBid() != null && User.getCurrentUser().equals(lot.getUser())){
                    end.setText("Accept Latest Bid");
                    end.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            endLot();
                        }
                    });
                }
                bids.add(0, insertion);
                bidsList.revalidate();
                price.setText(nf.format(latestLot.getPrice()));
            } catch (UnusableEntryException | TransactionException | RemoteException | InterruptedException e) {
                System.err.println("Error: " + e);
            }
        }

    }

    private class LotListener extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            try {
                final DWLotRemover remover = (DWLotRemover) javaSpace.read(new DWLotRemover(lot.getId()), null, 1500);

                if(remover.isRemoved()){
                    JOptionPane.showMessageDialog(null, "This lot has been removed!");
                    card.remove(LotCard.this);
                }

                if(remover.isEnded()){
                    Vector<String> winningBid = bids.get(0);
                    String winningId = winningBid.get(0);
                    String winningPrice = winningBid.get(1);
                    end.setVisible(false);
                    placeBid.setVisible(false);
                    buyNow.setVisible(false);
                    priceLabel.setText("Won by " + winningId + " -");
                    price.setText(" Price: " + winningPrice);
                    return;
                }

                if(remover.isBoughtOutright()){
                    end.setVisible(false);
                    placeBid.setVisible(false);
                    buyNow.setVisible(false);
                    priceLabel.setText("Lot has been Bought");
                    price.setText(" Bought for: " + lot.buyNowPrice);
                    return;
                }

            } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
                System.err.println("Error: " + e);
            }
        }

    }

    public static class Notifier implements RemoteEventListener {

        Exporter remoteExporter;

        private RemoteEventListener listener;

        Notifier() {
            try{
                remoteExporter =
                        new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                new BasicILFactory(), false, true);
                listener = (RemoteEventListener) remoteExporter.export(this);
            } catch (ExportException e) {
                System.err.println("Error: " + e);
            }
        }

        RemoteEventListener getListener() {
            return listener;
        }

        public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
            super.notify();
        }
    }

    public static class LotTable extends JTable {


        LotTable(Vector<Vector<String>> data, Vector<String> columns){
            setModel(new FixedLotTableModel(data, columns));
        }

        private static class FixedLotTableModel extends DefaultTableModel {

            FixedLotTableModel(Vector<Vector<String>> data, Vector<String> columns){
                super(data, columns);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class getColumnClass(int column) {
                return String.class;
            }

        }

    }
}

