package cats.net.server;

import cats.net.core.connection.rsa.RSAKeySet;
import cats.net.core.connection.spot.AbstractConnectionSpot;
import cats.net.core.connection.spot.event.ConnectionSpotListener;
import cats.net.core.data.Data;
import cats.net.core.data.handler.AbstractDataHandler;
import cats.net.server.event.ServerListener;
import cats.net.server.event.ServerStateListener;
import cats.net.server.handler.ServerDataHandler;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractServer extends AbstractConnectionSpot<AbstractServer> {

    public static final int RSA_SIZE = 2048;

    private RSAKeySet keys;

    AbstractServer(final InetSocketAddress address){
        super(address);
    }

    public final boolean initRSAKeys(final int size){
        if(isConnected())
            return false;
        keys = new RSAKeySet(size);
        return true;
    }

    public final boolean initRSAKeys(){
        return initRSAKeys(RSA_SIZE);
    }

    public final RSAKeySet RSAKeys(){
        return keys;
    }

    public final boolean isUsingRSA(){
        return keys != null;
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
        addListener((ConnectionSpotListener)listener);
    }

    public void addListener(final ServerStateListener listener){
        addListener((ConnectionSpotListener)listener);
    }

    public void addHandler(final short opcode, final ServerDataHandler handler){
        addHandler(opcode, (AbstractDataHandler)handler);
    }

    public void addHandler(final int opcode, final ServerDataHandler handler){
        addHandler(opcode, (AbstractDataHandler)handler);
    }

    public void sendToAll(final Data... datas){
        getConnected().forEach(c -> Arrays.stream(datas).forEach(c::send));
    }

    public void sendToAll(final short opcode, final Object... args){
        getConnected().forEach(c -> c.send(opcode, args));
    }

    public void sendToAll(final int opcode, final Object... args){
        sendToAll((short) opcode, args);
    }

    public void sendToAllExcept(final ActiveClientConnection exception, final Data... datas){
        getFilteredConnections(exception).forEach(c -> Arrays.stream(datas).forEach(c::send));
    }

    public void sendToAllExcept(final Data data, final ActiveClientConnection... exceptions){
        getFilteredConnections(exceptions).forEach(c -> c.send(data));
    }

    public void sendToAllExcept(final ActiveClientConnection exception, final short opcode, final Object... args){
        getFilteredConnections(exception).forEach(c -> c.send(opcode, args));
    }

    public void sendToAllExcept(final ActiveClientConnection exception, final int opcode, final Object... args){
        sendToAllExcept(exception, (short)opcode, args);
    }

    public <J> List<J> getConnectedAttachments(){
        return getConnected().stream().filter(c -> c != null).map(c -> (J)c).collect(Collectors.toList());
    }

    public List<ActiveClientConnection> getFilteredConnections(final ActiveClientConnection... clientConnections){
        final List<ActiveClientConnection> connections = Arrays.asList(clientConnections);
        return getConnected().stream().filter(c -> !connections.contains(c)).collect(Collectors.toList());
    }

    public ActiveClientConnection getConnectionByAttachment(final Object attachment){
        return getConnected().stream().filter(c -> Objects.equals(c.attachment(), attachment)).findFirst().orElse(null);
    }

    public abstract Collection<ActiveClientConnection> getConnected();

    abstract boolean disconnect(final ActiveClientConnection connection);

}
