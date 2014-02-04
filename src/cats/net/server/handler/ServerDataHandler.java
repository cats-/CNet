package cats.net.server.handler;

import cats.net.core.data.Data;
import cats.net.core.data.handler.AbstractDataHandler;
import cats.net.core.utils.CoreUtils;
import cats.net.server.AbstractServer;
import cats.net.server.ActiveClientConnection;

public abstract class ServerDataHandler<T extends AbstractServer> extends AbstractDataHandler<T>{

    public abstract void handle(final T server, final ActiveClientConnection connection, final Data data);

    public void handleException(final T server, final ActiveClientConnection connection, final Data data, final Exception ex){
        CoreUtils.print(ex);
    }
}
