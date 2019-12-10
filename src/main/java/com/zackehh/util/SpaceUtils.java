package com.zackehh.util;

import net.jini.space.JavaSpace;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

import java.io.IOException;

/**
 * This SpaceUtils class is based of of Gary Allen's Space Utils Class
 * The link to where this can be found is:
 *  "https://github.com/GaryAllenGit/JavaSpaceNotifyDemo/blob/master/src/SpaceUtils.java"
 *
 *  This Class has a small amount of code cleanup and has my preferred was of handling errors implemented.
 */
public class SpaceUtils {

    /**
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