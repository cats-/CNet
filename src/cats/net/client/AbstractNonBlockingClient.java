package cats.net.client;

import cats.net.client.handler.ClientDataHandler;
import cats.net.core.Core;
import cats.net.core.buffer.Buffer;
import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;
import java.io.EOFException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public abstract class AbstractNonBlockingClient extends AbstractClient{

    private Selector selector;
    private SocketChannel channel;

    protected AbstractNonBlockingClient(final InetSocketAddress address){
        super(address);
    }

    protected AbstractNonBlockingClient(final String host, final int port){
        this(new InetSocketAddress(host, port));
    }

    protected AbstractNonBlockingClient(final int port){
        this("localhost", port);
    }

    protected void connect() throws Exception{
        selector = Selector.open();

        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void disconnect(){
        try{
            channel.close();
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public boolean isConnected(){
        return channel.isConnected() || channel.isOpen();
    }

    boolean send0(final Data data) throws Exception{
        return data != null && data.writeTo(channel);
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
            final ClientDataHandler handler = (ClientDataHandler)handlers.get(data.opcode);
            if(handler != null){
                try{
                    handler.handle(this, data);
                }catch(Exception ex){
                    CoreUtils.print(ex);
                }
            }
            bytes = buf.getBytes();
        }
    }

    protected boolean canLoop(){
        return channel.isConnectionPending() || isConnected();
    }

    void finishConnect(){
        if(channel.isConnectionPending()){
            try{
                if(channel.finishConnect()){
                    fireOnConnect();
                    channel.register(selector, SelectionKey.OP_READ);
                }
            }catch(Exception ex){
                CoreUtils.print(ex);
            }
        }
    }

    protected boolean loop() throws Exception{
        selector.select();
        final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while(keys.hasNext()){
            final SelectionKey key = keys.next();
            keys.remove();
            if(key.isConnectable())
                finishConnect();
            else if(key.isReadable()){
                try{
                    read();
                }catch(Exception ex){
                    CoreUtils.print(ex);
                    disconnect();
                    return false;
                }
            }
        }
        return true;
    }
}
