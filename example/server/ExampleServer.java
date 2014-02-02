package example.server;

import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;
import cats.net.server.AbstractBlockingServer;
import cats.net.server.ActiveClientConnection;
import cats.net.server.event.ServerListener;
import example.server.user.User;

public class ExampleServer extends AbstractBlockingServer implements ServerListener<ExampleServer>{

    public ExampleServer(){
        super(4595);
    }

    public void onStart(){
        CoreUtils.print("ExampleServer start");
        addListener(this);
        addHandlers(ExampleServer.class.getResourceAsStream("handlers.xml"));
    }

    public void onFinish(){
        CoreUtils.print("ExampleServer finished");
    }

    public void onJoin(final ExampleServer server, final ActiveClientConnection connection){
        final User user = new User(connection);
        connection.attach(user);
        connection.send(new Data(1).put("name", "Server").put("msg", String.format("Welcome, %s", user.name)));
        final Data msg = new Data(1).put("name", "Server").put("msg", String.format("%s has just joined", user.name));
        server.getConnected().stream().filter(c -> !c.equals(connection)).forEach(c -> c.send(msg));
    }

    public void onLeave(final ExampleServer server, final ActiveClientConnection connection){
        final User user = connection.attachment();
        connection.attach(null);
        final Data msg = new Data(1).put("name", "Server").put("msg", String.format("%s has just left", user.name));
        server.getConnected().forEach(c -> c.send(msg));
    }

    public static void main(String[] args){
        new ExampleServer().start();
    }
}
