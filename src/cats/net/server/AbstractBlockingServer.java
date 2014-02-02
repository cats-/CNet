package cats.net.server;

import cats.net.core.utils.CoreUtils;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractBlockingServer extends AbstractServer{

    private ServerSocket socket;

    private final List<ActiveBlockingClientConnection> connections;

    protected AbstractBlockingServer(final InetSocketAddress address){
        super(address);

        connections = new LinkedList<>();
    }

    protected AbstractBlockingServer(final String host, final int port){
        this(new InetSocketAddress(host, port));
    }

    protected AbstractBlockingServer(final int port){
        this("localhost", port);
    }

    public Collection<ActiveClientConnection> getConnected(){
        return Collections.unmodifiableList(connections);
    }

    boolean disconnect(final ActiveClientConnection connection){
        if(connection == null || !connections.contains(connection))
            return false;
        connections.remove(connection);
        fireOnLeave(connection);
        return true;
    }

    protected void connect() throws Exception{
        socket = new ServerSocket();
        socket.bind(address);
    }

    public void disconnect(){
        try{
            socket.close();
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public boolean isConnected(){
        return socket.isBound();
    }

    protected boolean canLoop(){
        return isConnected();
    }

    protected boolean loop() throws Exception{
        final Socket client = socket.accept();
        final ActiveBlockingClientConnection connection = new ActiveBlockingClientConnection(this, client);
        connections.add(connection);
        fireOnJoin(connection);
        connection.thread();
        return true;
    }
}
