package cats.net.server;

import cats.net.core.connection.spot.AbstractConnectionSpot;
import cats.net.core.connection.spot.event.AbstractConnectionSpotListener;
import cats.net.core.data.Data;
import cats.net.core.data.handler.AbstractDataHandler;
import cats.net.server.event.ServerListener;
import cats.net.server.handler.ServerDataHandler;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public void sendToAll(final Data data){
        getConnected().forEach(c -> c.send(data));
    }

    public void sendToAllExcept(final Data data, final ActiveClientConnection... exceptions){
        getFilteredConnections(exceptions).forEach(c -> c.send(data));
    }

    public List<ActiveClientConnection> getFilteredConnections(final ActiveClientConnection... clientConnections){
        final List<ActiveClientConnection> connections = Arrays.asList(clientConnections);
        return getConnected().stream().filter(c -> !connections.contains(c)).collect(Collectors.toList());
    }

    public abstract Collection<ActiveClientConnection> getConnected();
}
