package cats.net.core.connection.rsa;

import cats.net.core.utils.CoreUtils;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;

public class RSAPrivKey {

    private PrivateKey key;
    private RSAPrivateKeySpec spec;

    public RSAPrivKey(final PrivateKey key){
        this.key = key;

        try{
            spec = KeyFactory.getInstance("RSA").getKeySpec(key, RSAPrivateKeySpec.class);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public RSAPrivKey(final BigInteger mod, final BigInteger exp){
        spec = new RSAPrivateKeySpec(mod, exp);
        try{
            key = KeyFactory.getInstance("RSA").generatePrivate(spec);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public PrivateKey key(){
        return key;
    }

    public RSAPrivateKeySpec spec(){
        return spec;
    }
}
