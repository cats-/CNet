package cats.net.client;

import cats.net.client.event.ClientListener;
import cats.net.client.handler.ClientDataHandler;
import cats.net.core.connection.spot.AbstractConnectionSpot;
import cats.net.core.connection.spot.event.AbstractConnectionSpotListener;
import cats.net.core.data.Data;
import cats.net.core.data.handler.AbstractDataHandler;
import cats.net.core.utils.CoreUtils;
import java.net.InetSocketAddress;

public abstract class AbstractClient extends AbstractConnectionSpot<AbstractClient>{

    AbstractClient(final InetSocketAddress address){
        super(address);
    }

    void fireOnConnect(){
        listeners.stream().filter(
                l -> l instanceof ClientListener
        ).forEach(
                l -> ((ClientListener)l).onConnect(this)
        );
    }

    public void addListener(final ClientListener listener){
        addListener((AbstractConnectionSpotListener<AbstractClient>)listener);
    }

    public void addHandler(final short opcode, final ClientDataHandler handler){
        addHandler(opcode, (AbstractDataHandler)handler);
    }

    public void addHandler(final int opcode, final ClientDataHandler handler){
        addHandler(opcode, (AbstractDataHandler) handler);
    }

    abstract boolean send0(final Data data) throws Exception;

    public boolean send(final Data data){
        try{
            return send0(data);
        }catch(Exception ex){
            CoreUtils.print(ex);
            return false;
        }
    }
}
