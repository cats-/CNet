package cats.net.core.connection.utils;

import cats.net.core.utils.CoreUtils;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public final class ConnectionUtils {

    private ConnectionUtils(){}

    public static void close(final Closeable... closeable){
        Arrays.stream(closeable).forEach(
                c -> {
                    try{
                        c.close();
                    }catch(Exception ex){
                        CoreUtils.print(ex);
                    }
                }
        );
    }

    public static InputStream in(final Socket socket){
        try{
            return socket.getInputStream();
        }catch(Exception ex){
            CoreUtils.print(ex);
            return null;
        }
    }

    public static OutputStream out(final Socket socket){
        try{
            final OutputStream out = socket.getOutputStream();
            out.flush();
            return out;
        }catch(Exception ex){
            CoreUtils.print(ex);
            return null;
        }
    }
}
