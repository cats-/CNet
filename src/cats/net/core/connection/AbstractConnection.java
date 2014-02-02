package cats.net.core.connection;

import cats.net.core.connection.spot.AbstractConnectionSpot;
import cats.net.core.data.Data;

public abstract class AbstractConnection<T extends AbstractConnectionSpot> {

    private Object attachment;

    protected final T spot;

    protected AbstractConnection(final T spot){
        this.spot = spot;
    }

    public void attach(final Object attachment){
        this.attachment = attachment;
    }

    public <T> T attachment(){
        return (T) attachment;
    }

    public T getSpot(){
        return spot;
    }

    public abstract boolean send(final Data data);

    public abstract void disconnect();

    public abstract boolean isConnected();
}
