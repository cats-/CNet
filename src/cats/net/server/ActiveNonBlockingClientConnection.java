package cats.net.server;

import cats.net.core.Core;
import cats.net.core.buffer.Buffer;
import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;
import cats.net.server.handler.ServerDataHandler;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

final class ActiveNonBlockingClientConnection extends ActiveClientConnection{

    private final SelectionKey key;
    private final SocketChannel channel;

    ActiveNonBlockingClientConnection(final AbstractServer server, final SelectionKey key, final SocketChannel channel){
        super(server);
        this.key = key;
        this.channel = channel;
    }

    public boolean send(final Data data){
        try{
            return data.writeTo(channel);
        }catch(Exception ex){
            CoreUtils.print(ex);
            spot.disconnect(this);
            return false;
        }
    }

    protected void read() throws Exception{
        final ByteBuffer buffer = ByteBuffer.allocate(Core.bufferSize);
        if(channel.read(buffer) < 0)
            throw new EOFException();
        buffer.flip();
        final Buffer buf = Buffer.wrap(buffer.array());
        byte[] bytes = buf.getBytes();
        while(bytes.length != 0){
            final Data data = Data.fromBuffer(Buffer.wrap(bytes));
            CoreUtils.print("received data with opcode %d", data.opcode);
            final ServerDataHandler handler = (ServerDataHandler)spot.getHandler(data.opcode);
            if(handler != null){
                try{
                    handler.handle(spot, this, data);
                }catch(Exception ex){
                    CoreUtils.print(ex);
                    handler.handleException(ex);
                }
            }
            bytes = buf.getBytes();
        }
    }

    public boolean isConnected(){
        return channel.isConnected() || channel.isOpen();
    }

    public void disconnect(){
        key.cancel();
        if(!isConnected())
            return;
        try{
            channel.close();
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
        spot.disconnect(this);
    }
}
