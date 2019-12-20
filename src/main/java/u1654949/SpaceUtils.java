package u1654949;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.IOException;
/***************************************************************************************
 *    This SpaceUtils class is based of of Gary Allen's Space Utils Class
 *    This Class has a small amount of code cleanup and has my preferred was of handling errors implemented.
 *    An example of this Class can be found in
 *
 *    Title: JavaSpacesPrintQueue
 *    Author: Gary Allen
 *    Date: 5/11/2019
 *    Code version: Commit d92df04377da73d0ff4b328a8b0f6e4e47c0ab79
 *    Availability: https://github.com/GaryAllenGit/JavaSpaceNotifyDemo/blob/master/src/SpaceUtils.java
 *
 ***************************************************************************************/

public class SpaceUtils {

    /**
     * Returns the JavaSpace object to allow use of the JavaSpace
     *
     * @return Returns the JavaSpace object
     */
    public static JavaSpace getSpace() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        JavaSpace js = null;
        try {
            LookupLocator l = new LookupLocator("jini://" + "waterloo");
            ServiceRegistrar sr = l.getRegistrar();
            Class c = Class.forName("net.jini.space.JavaSpace");
            Class[] classTemplate = {c};
            js = (JavaSpace) sr.lookup(new ServiceTemplate(null, classTemplate, null));
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Error: " + e);
        }
        return js;
    }

    /**
     * Returns the transaction manager object to allow the use of transactions
     *
     * @return Returns the transaction manager
     */
    public static TransactionManager getManager() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        TransactionManager tm = null;
        try {
            LookupLocator l = new LookupLocator("jini://" + "waterloo");
            ServiceRegistrar sr = l.getRegistrar();
            Class c = Class.forName("net.jini.core.transaction.server.TransactionManager");
            Class[] classTemplate = {c};
            tm = (TransactionManager) sr.lookup(new ServiceTemplate(null, classTemplate, null));
        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Error: " + e);
        }
        return tm;
    }
}