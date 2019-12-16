package U1654949.User_Interface.Interface_Helpers;

import U1654949.Space_Utils;
import U1654949.Space_Auction_Items.U1654949_Bid_Space;
import U1654949.Space_Auction_Items.U1654949_Lot_Space;
import net.jini.space.JavaSpace;

import javax.swing.text.JTextComponent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Common_Functions {

    /**
     * Default constructor which should not be called. All method calls
     * should be called from a static context.
     */
    private Common_Functions(){
        throw new UnsupportedOperationException();
    }

    /**
     * Parse a text input as a Number. Should the input not be
     * a valid Number, set errorTextOut to display a parse error.
     *
     * @param  component    the component to retrieve text of
     * @return Number       a valid Number object
     */
    public static Number getTextAsNumber(JTextComponent component){
        try {
            String input = component.getText().replaceAll("^[^0-9|\\.|-]", "");
            return NumberFormat.getInstance().parse(input);
        } catch(ParseException e){
            return null;
        }
    }

    /**
     * Parse a Double to a currency formatted string.
     *
     * @param  value        the Double value to convert
     * @return Double       a valid currency string
     */
    public static String getDoubleAsCurrency(Double value){
        // Create enforcer
        DecimalFormat currencyEnforcer = new DecimalFormat("0.00");

        // If value is null, short circuit
        if(value == null){
            return null;
        }

        // Format currency
        String currency = currencyEnforcer.format(value);

        // Handle negative values
        if(currency.charAt(0) == '-'){
            return "-£" + currency.substring(1);
        } else {
            return "£" + currency;
        }

    }

    public static ArrayList<U1654949_Bid_Space> getBidHistory(U1654949_Lot_Space lot) {
        JavaSpace space = Space_Utils.getSpace();

        // Initialise a list to store history
        ArrayList<U1654949_Bid_Space> bidHistory = new ArrayList<U1654949_Bid_Space>();

        try {
            // Fetch the latest version of the lot
            U1654949_Lot_Space refreshedLot = (U1654949_Lot_Space) space.read(new U1654949_Lot_Space(lot.getId()), null, 1500);

            // Get the history from the lot
            ArrayList<Integer> bids = refreshedLot.getBids();

            // If no history, short circuit
            if(bids.size() == 0){
                return bidHistory;
            }

            // Add all bids by id
            for(Integer bidId : bids){
                // Lookup the bid with the given id
                U1654949_Bid_Space template = new U1654949_Bid_Space(bidId, null, lot.getId(), null);
                U1654949_Bid_Space bidItem = ((U1654949_Bid_Space) space.read(template, null, 1500));

                // Add the bid to the history list
                bidHistory.add(bidItem);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        // Sort bids by price, just in case order is incorrect
        Collections.sort(bidHistory, new Comparator<U1654949_Bid_Space>() {
            @Override
            public int compare(U1654949_Bid_Space bid1, U1654949_Bid_Space bid2) {
                return bid2.getPrice().compareTo(bid1.getPrice());
            }
        });

        return bidHistory;
    }

    public static Vector<Vector<String>> getVectorBidMatrix(U1654949_Lot_Space lot){
        // Get the list of historic bids
        ArrayList<U1654949_Bid_Space> bids = getBidHistory(lot);

        // Initialise empty Vector
        Vector<Vector<String>> values = new Vector<Vector<String>>();

        for(int iY = 0; iY < bids.size(); iY++){
            // Grab the bid at the index
            final U1654949_Bid_Space bid = bids.get(iY);

            // Add each bid as a vector
            values.add(iY, new Vector<String>(){{
                add(bid.getUser().getId());
                add(Common_Functions.getDoubleAsCurrency(bid.getPrice()));
            }});
        }

        return values;
    }

    public static String toCamelCase(String str, String split){
        // Convert to lower case and split on splitter
        String[] parts = str.toLowerCase().split(split);

        int i = 0;
        String camelCaseString = "";

        // For each part upper case the first char if needed
        for (String part : parts){
            if(i++ > 0) {
                camelCaseString +=
                        part.substring(0, 1).toUpperCase() +
                        part.substring(1).toLowerCase();
            } else {
                camelCaseString += part;
            }
        }

        // Return camelCase version only if string has a split
        return camelCaseString.length() == 0 || parts.length == 1 ? str : camelCaseString;
    }

}
