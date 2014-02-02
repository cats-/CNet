package cats.net.client.event;

import cats.net.client.AbstractClient;
import cats.net.core.connection.spot.event.AbstractConnectionSpotListener;

public interface ClientListener<T extends AbstractClient> extends AbstractConnectionSpotListener<T>{

    public void onConnect(final T client);
}
