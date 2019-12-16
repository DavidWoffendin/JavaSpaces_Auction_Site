package U1654949.User_Interface;

import U1654949.Space_Auction_Items.U1654949_Bid_Space;
import U1654949.Space_Auction_Items.U1654949_Lot_Remover;
import U1654949.Space_Auction_Items.U1654949_Lot;
import U1654949.Space_Utils;
import U1654949.User;
import U1654949.User_Interface.Defaults.Default_Table;
import U1654949.User_Interface.Interface_Helpers.*;

import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.Vector;

public class Lot_Card extends JPanel {

    private final JavaSpace javaSpace;
    private final Default_Table bidsList;
    private final U1654949_Lot lot;
    private final Vector<Vector<String>> bids;
    private final JLabel acceptOrRemove;
    private final JLabel price;
    private final JLabel buyItNowPrice;
    private final JLabel priceLabel;
    private final JLabel placeBid;
    private final JPanel card;
    private final AcceptButtonListener acceptButtonListener;
    private final RemoveButtonListener removeButtonListener;

    public Lot_Card(final JPanel card, U1654949_Lot lotForCard) {
        super();

        this.card = card;
        this.javaSpace = Space_Utils.getSpace();

        U1654949_Lot baseLot = lotForCard;
        try {
            U1654949_Lot templateLot = new U1654949_Lot(lotForCard.getId());
            baseLot = (U1654949_Lot) javaSpace.read(templateLot, null, 1500);
        } catch(Exception e){
            e.printStackTrace();
        }

        this.lot = baseLot;

        setLayout(new BorderLayout());

        try {
            NewBidListener bidListener = new NewBidListener();
            LotChangeListener lotListener = new LotChangeListener();

            U1654949_Bid_Space bidTemplate = new U1654949_Bid_Space(null, null, lot.getId(), null);
            U1654949_Lot_Remover removerTemplate = new U1654949_Lot_Remover(lot.getId(), null, null);

            javaSpace.notify(bidTemplate, null, bidListener.getListener(), Lease.FOREVER, null);
            javaSpace.notify(removerTemplate, null, lotListener.getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel back = new JLabel("Back");
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                card.remove(Lot_Card.this);
            }
        });

        panel.add(back, BorderLayout.WEST);

        placeBid = new JLabel("Place Bid");
        acceptOrRemove = new JLabel("Accept Latest Bid");
        price = new JLabel();
        buyItNowPrice = new JLabel();

        acceptButtonListener = new AcceptButtonListener(lot, price);
        removeButtonListener = new RemoveButtonListener(lot);

        if(!lot.isEnded()) {
            if (User.getCurrentUser().equals(lot.getUser())) {
                if(lot.getLastBid() == null){
                    acceptOrRemove.setText("Remove Lot");
                    acceptOrRemove.addMouseListener(removeButtonListener);
                } else {
                    acceptOrRemove.addMouseListener(acceptButtonListener);
                }
                panel.add(acceptOrRemove, BorderLayout.EAST);

            } else {
                placeBid.addMouseListener(new PlaceButtonListener(lot));
                panel.add(placeBid, BorderLayout.EAST);
            }
        }

        add(panel, BorderLayout.NORTH);

        String[] labels = {
                "ID",
                "User ID",
                "Name",
                "Description"
        };

        JPanel lotDetails = new JPanel(new GridLayout(labels.length + 2, 2));
        lotDetails.setBorder(BorderFactory.createEmptyBorder(-8, 0, 10, 0));

        try {
            for (String label : labels) {
                JLabel l = new JLabel(label + ": ", SwingConstants.RIGHT);
                lotDetails.add(l);

                Class<?> c = lot.getClass();
                Method method = c.getMethod(Common_Functions.toCamelCase("get " + label, " "));

                String valueOfField = method.invoke(lot) + "";

                JLabel textLabel = new JLabel(valueOfField);
                l.setLabelFor(textLabel);
                lotDetails.add(textLabel);
            }
        } catch (Exception e) {
        }

        bids = Common_Functions.getVectorBidMatrix(lot);

        if(lot.isEnded()){
            // Display the winner and the price the item was won for
            priceLabel = new JLabel("Won by " + bids.get(0).get(0) + " -", SwingConstants.RIGHT);
            price.setText(" Price: " + Common_Functions.getDoubleAsCurrency(lot.getPrice()));
            buyItNowPrice.setText(Common_Functions.getDoubleAsCurrency(lot.getBuyNowPricePrice()));
        } else {
            priceLabel = new JLabel("Current Price: ", SwingConstants.RIGHT);
            price.setText(Common_Functions.getDoubleAsCurrency(lot.getPrice()));
            buyItNowPrice.setText(Common_Functions.getDoubleAsCurrency(lot.getBuyNowPricePrice()));
        }

        priceLabel.setLabelFor(price);
        lotDetails.add(priceLabel);
        lotDetails.add(price);
        lotDetails.add(buyItNowPrice);

        add(lotDetails);

        bidsList = new Default_Table(bids, new Vector<String>(){{
            add("Buyer ID");
            add("Bid Amount");
        }});

        JScrollPane itemListPanel = new JScrollPane(bidsList);

        add(itemListPanel, BorderLayout.SOUTH);
    }

    private class NewBidListener extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            try {
                final U1654949_Lot latestLot = (U1654949_Lot) javaSpace.read(new U1654949_Lot(lot.getId()), null, 1500);
                final U1654949_Bid_Space latestBid = (U1654949_Bid_Space) javaSpace.read(new U1654949_Bid_Space(latestLot.getLastBid()), null, 1500);

                Vector<String> insertion = new Vector<String>(){{
                    add(latestBid.getUser().getId());
                    add(Common_Functions.getDoubleAsCurrency(latestBid.getPrice()));
                }};

                if(latestLot.getLastBid() != null && User.getCurrentUser().equals(lot.getUser())){
                    acceptOrRemove.setText("Accept Latest Bid");
                    acceptOrRemove.addMouseListener(acceptButtonListener);
                    acceptOrRemove.removeMouseListener(removeButtonListener);
                }

                bids.add(0, insertion);

                bidsList.revalidate();

                price.setText(Common_Functions.getDoubleAsCurrency(latestLot.getPrice()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class LotChangeListener extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            try {
                final U1654949_Lot_Remover remover = (U1654949_Lot_Remover) javaSpace.read(new U1654949_Lot_Remover(lot.getId()), null, 1500);

                if(remover.isEnded()){
                    Vector<String> winningBid = bids.get(0);

                    String winningId = winningBid.get(0);
                    String winningPrice = winningBid.get(1);

                    acceptOrRemove.setVisible(false);
                    placeBid.setVisible(false);

                    priceLabel.setText("Won by " + winningId + " -");
                    price.setText(" Price: " + winningPrice);

                    return;
                }

                if(remover.isRemoved()){
                    JOptionPane.showMessageDialog(null, "This lot has been removed!");
                    card.remove(Lot_Card.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

