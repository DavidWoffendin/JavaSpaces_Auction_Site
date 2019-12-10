package com.zackehh.ui;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import java.rmi.RemoteException;

/**
 * A generic implementation to create a RemoteEventListener
 * easily, without having to explicitly define an Exporter.
 * This is inherited by any notifiers used inside the app.
 */
public class GenericNotifier implements RemoteEventListener {

    /**
     * The exporter used to gain the listener.
     */
    protected Exporter remoteExporter;

    /**
     * Our RemoteEventListener, starting at null.
     */
    protected RemoteEventListener listener;

    /**
     * Does nothing except instantiate the Exporter. This ensures
     * that any classes inheriting from GenericNotificationListener
     * do not have to explicitly create their own Exporter.
     */
    protected GenericNotifier() {
        try{
            remoteExporter =
                    new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                            new BasicILFactory(), false, true);
            listener = (RemoteEventListener) remoteExporter.export(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A public accessor method to the RemoteEventListener.
     * This is what should be called before passing the listener
     * into any space#notify method calls.
     *
     * @return listener     the RemoteEventListener
     */
    public RemoteEventListener getListener(){
        return listener;
    }

    /**
     * Implement notify so that we can inherit from this class
     * more easily.
     *
     * @param remoteEvent               the remote event
     * @throws UnknownEventException
     * @throws RemoteException
     */
    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        super.notify();
    }
}
