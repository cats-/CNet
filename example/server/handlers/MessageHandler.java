package example.server.handlers;

import cats.net.core.data.Data;
import cats.net.server.ActiveClientConnection;
import cats.net.server.handler.ServerDataHandler;
import example.server.ExampleServer;
import example.server.user.User;

public class MessageHandler extends ServerDataHandler<ExampleServer>{

    public void handle(final ExampleServer server, final ActiveClientConnection connection, final Data data){
        final User user = connection.attachment();
        final Data msg = new Data(1).put("name", user.name).put("msg", data.getString("msg"));
        server.sendToAll(msg);
    }
}
