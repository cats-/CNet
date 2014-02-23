package cats.net.core.connection.rsa;

import cats.net.core.utils.CoreUtils;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;

public class RSAPubKey {

    private PublicKey key;
    private RSAPublicKeySpec spec;
    private Cipher cipher;

    public RSAPubKey(final PublicKey key){
        this.key = key;

        try{
            spec = KeyFactory.getInstance("RSA").getKeySpec(key, RSAPublicKeySpec.class);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }

        initCipher();
    }

    public RSAPubKey(final BigInteger mod, final BigInteger exp){
        spec = new RSAPublicKeySpec(mod, exp);

        try{
            key = KeyFactory.getInstance("RSA").generatePublic(spec);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }

        initCipher();
    }

    private void initCipher(){
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public byte[] encrypt(final byte[] bytes){
        try{
            return cipher.doFinal(bytes);
        }catch(Exception ex){
            CoreUtils.print(ex);
            return bytes;
        }
    }

    public PublicKey key(){
        return key;
    }

    public RSAPublicKeySpec spec(){
        return spec;
    }

    public Cipher cipher(){
        return cipher;
    }
}
