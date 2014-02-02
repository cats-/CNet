package example.client;

import cats.net.client.AbstractBlockingClient;
import cats.net.client.event.ClientListener;
import cats.net.core.data.Data;
import cats.net.core.utils.CoreUtils;

public class ExampleClient extends AbstractBlockingClient implements ClientListener<ExampleClient>{

    public ExampleClient(){
        super(4595);
    }

    public void onStart(){
        CoreUtils.print("ExampleClient start");
        addListener(this);
        addHandlers(ExampleClient.class.getResourceAsStream("handlers.xml"));
    }

    public void onFinish(){
        CoreUtils.print("onFinish");
    }

    public void onConnect(final ExampleClient client){
        CoreUtils.print("ExampleClient connected");
        client.send(new Data(1).put("msg", "i just connected"));
    }

    public static void main(String[] args){
        new ExampleClient().start();
    }
}
