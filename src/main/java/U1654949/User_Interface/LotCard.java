package U1654949.User_Interface;

import U1654949.Space_Auction_Items.DWBid;
import U1654949.Space_Auction_Items.DWLotRemover;
import U1654949.Space_Auction_Items.DWLot;
import U1654949.Space_Utils;
import U1654949.User;
import U1654949.User_Interface.Defaults.Default_Table;
import U1654949.User_Interface.Interface_Helpers.*;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.*;

public class LotCard extends JPanel {

    private final JavaSpace javaSpace;
    private final Default_Table bidsList;
    private final DWLot lot;
    private final Vector<Vector<String>> bids;
    private final JLabel acceptOrRemove;
    private final JLabel price;
    private final JLabel buyItNowPrice;
    private final JLabel priceLabel;
    private final JLabel buyNowLabel;
    private final JLabel placeBid;
    private final JLabel buyNow;
    private final JPanel card;
    private final AcceptButtonListener acceptButtonListener;
    private final RemoveButtonListener removeButtonListener;
    private final BuyItNowButtonListener buyItNowButtonListener;

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);

    public LotCard(final JPanel card, DWLot lotForCard) {
        super();
        this.card = card;
        this.javaSpace = Space_Utils.getSpace();
        DWLot baseLot = lotForCard;
        try {
            DWLot templateLot = new DWLot(lotForCard.getId());
            baseLot = (DWLot) javaSpace.read(templateLot, null, 1500);
        } catch (UnusableEntryException | InterruptedException | RemoteException | TransactionException e) {
            System.err.println("Error: " + e);
        }
        this.lot = baseLot;
        setLayout(new BorderLayout());
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
        JLabel back = new JLabel("Back");
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                card.remove(LotCard.this);
            }
        });
        infoPanel.add(back, BorderLayout.WEST);
        placeBid = new JLabel("Place Bid");
        buyNow = new JLabel("Buy it now");
        acceptOrRemove = new JLabel("Accept Latest Bid");
        price = new JLabel();
        buyItNowPrice = new JLabel();
        acceptButtonListener = new AcceptButtonListener(lot, price);
        removeButtonListener = new RemoveButtonListener(lot);
        buyItNowButtonListener = new BuyItNowButtonListener(lot);
        JPanel eastPanel = new JPanel();

        if(!lot.isEnded()) {
            if (User.getCurrentUser().equals(lot.getUser())) {
                if(lot.getLastBid() == null){
                    acceptOrRemove.setText("Remove Lot");
                    acceptOrRemove.addMouseListener(removeButtonListener);
                } else {
                    acceptOrRemove.addMouseListener(acceptButtonListener);
                }
                infoPanel.add(acceptOrRemove, BorderLayout.EAST);
            } else {
                placeBid.addMouseListener(new PlaceButtonListener(lot));
                buyNow.addMouseListener(new BuyItNowButtonListener(lot));
                eastPanel.add(placeBid);
                eastPanel.add(buyNow);
                infoPanel.add(eastPanel,BorderLayout.EAST);
            }
        }

        add(infoPanel, BorderLayout.SOUTH);
        String[] labels = {"ID", "User ID", "Name", "Description"};
        JPanel lotDetails = new JPanel(new GridLayout(labels.length + 2, 2));
        lotDetails.setBorder(BorderFactory.createEmptyBorder(-8, 0, 10, 0));

        try {
            for (String label : labels) {
                JLabel l = new JLabel(label + ": ", SwingConstants.RIGHT);
                lotDetails.add(l);
                Class<?> c = lot.getClass();
                Method method = c.getMethod(toCamelCase("get " + label, " "));
                String valueOfField = method.invoke(lot) + "";
                JLabel textLabel = new JLabel(valueOfField);
                l.setLabelFor(textLabel);
                lotDetails.add(textLabel);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            System.err.println("Error: " + e);
        }

        bids = getVectorBidMatrix(lot);

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
        add(lotDetails);
        bidsList = new Default_Table(bids, new Vector<String>(){{
            add("Buyer ID");
            add("Bid Amount");
        }});
        JScrollPane itemListPanel = new JScrollPane(bidsList);
        add(itemListPanel, BorderLayout.NORTH);
    }

    public static ArrayList<DWBid> getBidHistory(DWLot lot) {
        JavaSpace space = Space_Utils.getSpace();
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

    public static String toCamelCase(String str, String split){
        String[] parts = str.toLowerCase().split(split);
        int i = 0;
        String camelCaseString = "";
        for (String part : parts){
            if(i++ > 0) {
                camelCaseString +=
                        part.substring(0, 1).toUpperCase() +
                                part.substring(1).toLowerCase();
            } else {
                camelCaseString += part;
            }
        }
        return camelCaseString.length() == 0 || parts.length == 1 ? str : camelCaseString;
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
                    acceptOrRemove.setText("Accept Latest Bid");
                    acceptOrRemove.addMouseListener(acceptButtonListener);
                    acceptOrRemove.removeMouseListener(removeButtonListener);
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

                System.out.println(remover.isEnded());
                System.out.println(remover.isRemoved());
                System.out.println(remover.isBoughtOutright());

                if(remover.isRemoved()){
                    JOptionPane.showMessageDialog(null, "This lot has been removed!");
                    card.remove(LotCard.this);
                }

                if(remover.isEnded()){
                    Vector<String> winningBid = bids.get(0);
                    String winningId = winningBid.get(0);
                    String winningPrice = winningBid.get(1);
                    acceptOrRemove.setVisible(false);
                    placeBid.setVisible(false);
                    buyNow.setVisible(false);
                    priceLabel.setText("Won by " + winningId + " -");
                    price.setText(" Price: " + winningPrice);
                    return;
                }

                if(remover.isBoughtOutright()){
                    acceptOrRemove.setVisible(false);
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
}

