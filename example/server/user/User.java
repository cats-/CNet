package example.server.user;

import cats.net.server.ActiveClientConnection;

public class User {

    public final String name;
    public final ActiveClientConnection connection;

    public User(final ActiveClientConnection connection){
        this.connection = connection;

        name = "Guest" + System.currentTimeMillis();
    }
}
