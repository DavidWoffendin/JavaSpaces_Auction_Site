package com.zackehh.util;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.rmi.RMISecurityManager;

public final class SpaceUtils {

    /**
     * A single instance of the JavaSpace we wish to connect to.
     * This is to save overhead searching for a space multiple times.
     */
    private static JavaSpace space;

    /**
     * A default hostname to connect to.
     */
    private static String hostname = "localhost";

    /**
     * Similar to the `space` property, this will store a found
     * TransactionManager for faster access
     */
    private static TransactionManager manager;

    /**
     * Default constructor which should not be called. All method calls
     * should be static.
     */
    private SpaceUtils(){
        throw new UnsupportedOperationException();
    }

    /**
     * Searches for a space with the given hostname on the network. Stores
     * in the class property `space` to avoid repeated look-ups. Returns null
     * if no space can be found, and leaves a stack trace for debugging.
     *
     * @param  hostname         the hostname to lookup
     * @return JavaSpace        the connected space
     */
    public static JavaSpace getSpace(String hostname) {
        if(space != null){
            return space;
        }
        JavaSpace js = null;
        try {
            LookupLocator l = new LookupLocator("jini://" + hostname);

            ServiceRegistrar sr = l.getRegistrar();

            Class[] classTemplate = {
                Class.forName("net.jini.space.JavaSpace")
            };

            js = (JavaSpace) sr.lookup(new ServiceTemplate(null, classTemplate, null));

            if(js != null){
                System.out.println("Successfully connected!");
                space = js;
            } else {
                System.err.println("Unable to verify space connection");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
        return js;
    }

    /**
     * A wrapper around SpaceUtils#getSpace to default to a given hostname;
     * in the current iteration we search for `localhost`.
     *
     * @return JavaSpace        the connected space
     */
    public static JavaSpace getSpace() {
        return getSpace(hostname);
    }

    /**
     * Searches for a TransactionManager on the given hostname. Stores
     * in the class property `manager` to avoid repeated look-ups. Returns null
     * if no manager can be found, and leaves a stack trace for debugging.
     *
     * @param  hostname             the hostname to lookup
     * @return TransactionManager   the manager object
     */
    public static TransactionManager getManager(String hostname) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        if(manager != null){
            return manager;
        }

        TransactionManager tm = null;
        try {
            LookupLocator l = new LookupLocator("jini://" + hostname);

            ServiceRegistrar sr = l.getRegistrar();

            Class[] classTemplate = {
                Class.forName("net.jini.core.transaction.server.TransactionManager")
            };

            tm = (TransactionManager) sr.lookup(new ServiceTemplate(null, classTemplate, null));
            if(tm != null){
                System.out.println("Found TransactionManager!");
                manager = tm;
            } else {
                System.err.println("Unable to find TransactionManager");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
        return tm;
    }

    /**
     * A wrapper around SpaceUtils#getManager to default to a given hostname;
     * in the current iteration we search for `localhost`.
     *
     * @return TransactionManager   the manager object
     */
    public static TransactionManager getManager() {
        return getManager(hostname);
    }

    public static void setHostname(String host){
        hostname = host;
    }
}

