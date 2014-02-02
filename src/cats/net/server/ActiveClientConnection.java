package cats.net.server;

import cats.net.core.connection.AbstractConnection;

public abstract class ActiveClientConnection extends AbstractConnection<AbstractServer>{

    ActiveClientConnection(final AbstractServer server){
        super(server);
    }

    protected abstract void read() throws Exception;
}
