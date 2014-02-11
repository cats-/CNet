package cats.net.core.connection;

import cats.net.core.Core;
import cats.net.core.connection.spot.AbstractConnectionSpot;
import cats.net.core.data.Data;
import cats.net.core.data.former.DataFormer;
import cats.net.core.data.former.DataFormerNotSetException;

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

    public boolean send(final short opcode, final Object... args) throws DataFormerNotSetException {
        final DataFormer former = Core.getDataFormer(opcode);
        if(former == null)
            throw new DataFormerNotSetException(opcode);
        return send(former.form(args));
    }

    public T getSpot(){
        return spot;
    }

    public abstract boolean send(final Data data);

    public abstract void disconnect();

    public abstract boolean isConnected();
}
