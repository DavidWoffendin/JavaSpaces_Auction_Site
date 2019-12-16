package U1654949.User_Interface;

import U1654949.Space_Auction_Items.U1654949_Bid_Space;
import U1654949.Space_Auction_Items.U1654949_Lot_Remover;
import U1654949.Space_Auction_Items.U1654949_Lot_Space;
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

    private final JavaSpace space;
    private final Default_Table bidTable;
    private final U1654949_Lot_Space lot;
    private final Vector<Vector<String>> bidHistory;
    private final JLabel acceptBidOrRemoveLot;
    private final JLabel currentPrice;
    private final JLabel currentPriceLabel;
    private final JLabel placeBid;
    private final JPanel cards;
    private final AcceptButtonListener acceptBidListener;
    private final RemoveButtonListener removeLotListener;

    public Lot_Card(final JPanel cards, U1654949_Lot_Space lotForCard) {
        super();

        this.cards = cards;
        this.space = Space_Utils.getSpace();

        U1654949_Lot_Space baseLot = lotForCard;
        try {
            U1654949_Lot_Space templateLot = new U1654949_Lot_Space(lotForCard.getId());
            baseLot = (U1654949_Lot_Space) space.read(templateLot, null, 1500);
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

            space.notify(bidTemplate, null, bidListener.getListener(), Lease.FOREVER, null);
            space.notify(removerTemplate, null, lotListener.getListener(), Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel back = new JLabel("Back");
        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                cards.remove(Lot_Card.this);
            }
        });

        panel.add(back, BorderLayout.WEST);

        placeBid = new JLabel("Place Bid");
        acceptBidOrRemoveLot = new JLabel("Accept Latest Bid");
        currentPrice = new JLabel();

        acceptBidListener = new AcceptButtonListener(lot, currentPrice);
        removeLotListener = new RemoveButtonListener(lot);

        if(!lot.isEnded()) {
            if (User.getCurrentUser().equals(lot.getUser())) {
                if(lot.getLastBid() == null){
                    acceptBidOrRemoveLot.setText("Remove Lot");
                    acceptBidOrRemoveLot.addMouseListener(removeLotListener);
                } else {
                    acceptBidOrRemoveLot.addMouseListener(acceptBidListener);
                }
                panel.add(acceptBidOrRemoveLot, BorderLayout.EAST);

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

        JPanel p = new JPanel(new GridLayout(labels.length + 1, 2));
        p.setBorder(BorderFactory.createEmptyBorder(-8, 0, 10, 0));

        try {
            for (String label : labels) {
                JLabel l = new JLabel(label + ": ", SwingConstants.RIGHT);
                p.add(l);

                Class<?> c = lot.getClass();
                Method method = c.getMethod(Common_Functions.toCamelCase("get " + label, " "));

                String valueOfField = method.invoke(lot) + "";

                JLabel textLabel = new JLabel(valueOfField);
                l.setLabelFor(textLabel);
                p.add(textLabel);
            }
        } catch (Exception e) {
        }

        bidHistory = Common_Functions.getVectorBidMatrix(lot);

        if(lot.isEnded()){
            // Display the winner and the price the item was won for
            currentPriceLabel = new JLabel("Won by " + bidHistory.get(0).get(0) + " -", SwingConstants.RIGHT);
            currentPrice.setText(" Price: " + Common_Functions.getDoubleAsCurrency(lot.getPrice()));
        } else {
            currentPriceLabel = new JLabel("Current Price: ", SwingConstants.RIGHT);
            currentPrice.setText(Common_Functions.getDoubleAsCurrency(lot.getPrice()));
        }

        currentPriceLabel.setLabelFor(currentPrice);
        p.add(currentPriceLabel);
        p.add(currentPrice);

        add(p);

        bidTable = new Default_Table(bidHistory, new Vector<String>(){{
            add("Buyer ID");
            add("Bid Amount");
        }});

        JScrollPane itemListPanel = new JScrollPane(bidTable);

        add(itemListPanel, BorderLayout.SOUTH);
    }

    private class NewBidListener extends Notifier {

        @Override
        public void notify(RemoteEvent ev) {
            try {
                final U1654949_Lot_Space latestLot = (U1654949_Lot_Space) space.read(new U1654949_Lot_Space(lot.getId()), null, 1500);
                final U1654949_Bid_Space latestBid = (U1654949_Bid_Space) space.read(new U1654949_Bid_Space(latestLot.getLastBid()), null, 1500);

                Vector<String> insertion = new Vector<String>(){{
                    add(latestBid.getUser().getId());
                    add(Common_Functions.getDoubleAsCurrency(latestBid.getPrice()));
                }};

                if(latestLot.getLastBid() != null && User.getCurrentUser().equals(lot.getUser())){
                    acceptBidOrRemoveLot.setText("Accept Latest Bid");
                    acceptBidOrRemoveLot.addMouseListener(acceptBidListener);
                    acceptBidOrRemoveLot.removeMouseListener(removeLotListener);
                }

                bidHistory.add(0, insertion);

                bidTable.revalidate();

                currentPrice.setText(Common_Functions.getDoubleAsCurrency(latestLot.getPrice()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class LotChangeListener extends Notifier {
        @Override
        public void notify(RemoteEvent ev) {
            try {
                final U1654949_Lot_Remover remover = (U1654949_Lot_Remover) space.read(new U1654949_Lot_Remover(lot.getId()), null, 1500);

                if(remover.isEnded()){
                    Vector<String> winningBid = bidHistory.get(0);

                    String winningId = winningBid.get(0);
                    String winningPrice = winningBid.get(1);

                    acceptBidOrRemoveLot.setVisible(false);
                    placeBid.setVisible(false);

                    currentPriceLabel.setText("Won by " + winningId + " -");
                    currentPrice.setText(" Price: " + winningPrice);

                    return;
                }

                if(remover.isRemoved()){
                    JOptionPane.showMessageDialog(null, "This lot has been removed!");
                    cards.remove(Lot_Card.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

