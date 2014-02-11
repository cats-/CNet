package example.client;

import cats.net.client.AbstractBlockingClient;
import cats.net.client.event.ClientListener;
import cats.net.core.Core;
import cats.net.core.utils.CoreUtils;
import example.client.formers.MessageFormer;

public class ExampleClient extends AbstractBlockingClient implements ClientListener<ExampleClient>{

    public ExampleClient(){
        super(4595);
    }

    public void onStart(){
        Core.addDataFormer(new MessageFormer());
        CoreUtils.print("ExampleClient start");
        addListener(this);
        addHandlers(ExampleClient.class.getResourceAsStream("handlers.xml"));
    }

    public void onFinish(){
        CoreUtils.print("onFinish");
    }

    public void onConnect(final ExampleClient client){
        CoreUtils.print("ExampleClient connected");
        client.send(1, "ExampleClient", "hi i just joined");
    }

    public static void main(String[] args){
        new ExampleClient().start();
    }
}
