package example.client.handlers;

import cats.net.client.handler.ClientDataHandler;
import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;
import example.client.ExampleClient;

public class MessageHandler extends ClientDataHandler<ExampleClient>{

    public void handle(final ExampleClient client, final Data data){
        CoreUtils.print("[%s] %s", data.getString("name"), data.getString("msg"));
    }
}
