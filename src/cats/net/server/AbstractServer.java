package cats.net.server;

import cats.net.core.connection.spot.AbstractConnectionSpot;
import cats.net.core.connection.spot.event.AbstractConnectionSpotListener;
import cats.net.core.data.handler.AbstractDataHandler;
import cats.net.server.event.ServerListener;
import cats.net.server.handler.ServerDataHandler;
import java.net.InetSocketAddress;
import java.util.Collection;

public abstract class AbstractServer extends AbstractConnectionSpot<AbstractServer> {

    AbstractServer(final InetSocketAddress address){
        super(address);
    }

    void fireOnJoin(final ActiveClientConnection connection){
        listeners.stream().filter(
                l -> l instanceof ServerListener
        ).forEach(
                l -> ((ServerListener)l).onJoin(this, connection)
        );
    }

    void fireOnLeave(final ActiveClientConnection connection){
        listeners.stream().filter(
                l -> l instanceof ServerListener
        ).forEach(
                l -> ((ServerListener)l).onLeave(this, connection)
        );
    }

    public void addListener(final ServerListener listener){
        addListener((AbstractConnectionSpotListener)listener);
    }

    public void addHandler(final short opcode, final ServerDataHandler handler){
        addHandler(opcode, (AbstractDataHandler)handler);
    }

    public void addHandler(final int opcode, final ServerDataHandler handler){
        addHandler(opcode, (AbstractDataHandler)handler);
    }

    abstract boolean disconnect(final ActiveClientConnection connection);

    public abstract Collection<ActiveClientConnection> getConnected();
}
