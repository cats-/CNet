package cats.net.server.event;

import cats.net.core.connection.spot.event.ConnectionSpotListener;
import cats.net.server.AbstractServer;
import cats.net.server.ActiveClientConnection;

public interface ServerListener<T extends AbstractServer> extends ConnectionSpotListener<T> {

    public void onJoin(final T server, final ActiveClientConnection connection);

    public void onLeave(final T server, final ActiveClientConnection connection);
}
