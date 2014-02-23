package cats.net.core.connection.rsa;

import cats.net.core.utils.CoreUtils;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSAKeySet {

    private RSAPubKey pub;
    private RSAPrivKey priv;

    public RSAKeySet(final int size){
        try{
            final KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(size);
            final KeyPair pair = gen.generateKeyPair();
            pub = new RSAPubKey(pair.getPublic());
            priv = new RSAPrivKey(pair.getPrivate());
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public RSAPubKey publicKey(){
        return pub;
    }

    public RSAPrivKey privateKey(){
        return priv;
    }
}
