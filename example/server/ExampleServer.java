package example.server;

import cats.net.core.Core;
import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;
import cats.net.server.AbstractNonBlockingServer;
import cats.net.server.ActiveClientConnection;
import cats.net.server.event.ServerListener;
import example.server.user.User;

public class ExampleServer extends AbstractNonBlockingServer implements ServerListener<ExampleServer>{

    public ExampleServer(){
        super(4595);
    }

    public void onStart(){
        CoreUtils.print("ExampleServer start");
        Core.addDataFormers(ExampleServer.class.getResourceAsStream("formers.xml"));
        addListener(this);
        addHandlers(ExampleServer.class.getResourceAsStream("handlers.xml"));
    }

    public void onFinish(){
        CoreUtils.print("ExampleServer finished");
    }

    public void onJoin(final ExampleServer server, final ActiveClientConnection connection){
        final User user = new User(connection);
        connection.attach(user);
        connection.send(1, String.format("Welcome, %s", user.name));
        final Data msg = Core.getDataFormer(1).form(String.format("%s has just joined", user.name));
        server.sendToAllExcept(msg, connection);
    }

    public void onLeave(final ExampleServer server, final ActiveClientConnection connection){
        final User user = connection.attachment();
        connection.attach(null);
        final Data msg = Core.getDataFormer(1).form(String.format("%s has just left", user.name));
        server.sendToAll(msg);
    }

    public static void main(String[] args){
        new ExampleServer().start();
    }
}
