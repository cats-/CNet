package cats.net.core.connection.rsa;

import cats.net.core.utils.CoreUtils;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

public class RSAPubKey {

    private PublicKey key;
    private RSAPublicKeySpec spec;

    public RSAPubKey(final PublicKey key){
        this.key = key;

        try{
            spec = KeyFactory.getInstance("RSA").getKeySpec(key, RSAPublicKeySpec.class);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public RSAPubKey(final BigInteger mod, final BigInteger exp){
        spec = new RSAPublicKeySpec(mod, exp);
        try{
            key = KeyFactory.getInstance("RSA").generatePublic(spec);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public PublicKey key(){
        return key;
    }

    public RSAPublicKeySpec spec(){
        return spec;
    }
}
