package U1654949.User_Interface.Interface_Helpers;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

/**
 *
 */
public class Notifier implements RemoteEventListener {

    protected Exporter remoteExporter;

    /**
     *
     */
    private RemoteEventListener listener;

    /**
     *
     */
    protected Notifier() {
        try{
            remoteExporter =
                    new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                            new BasicILFactory(), false, true);
            listener = (RemoteEventListener) remoteExporter.export(this);
        } catch (ExportException e) {
            System.err.println("Error: " + e);
        }
    }

    /**
     * @return
     */
    public RemoteEventListener getListener() {
        return listener;
    }

    /**
     * @param remoteEvent
     * @throws UnknownEventException
     * @throws RemoteException
     */
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        super.notify();
    }
}
