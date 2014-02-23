package cats.net.core.connection.rsa;

import cats.net.core.utils.CoreUtils;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import javax.crypto.Cipher;

public class RSAPrivKey {

    private PrivateKey key;
    private RSAPrivateKeySpec spec;
    private Cipher cipher;

    public RSAPrivKey(final PrivateKey key){
        this.key = key;

        try{
            spec = KeyFactory.getInstance("RSA").getKeySpec(key, RSAPrivateKeySpec.class);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }

        initCipher();
    }

    public RSAPrivKey(final BigInteger mod, final BigInteger exp){
        spec = new RSAPrivateKeySpec(mod, exp);

        try{
            key = KeyFactory.getInstance("RSA").generatePrivate(spec);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }

        initCipher();
    }

    private void initCipher(){
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
        }catch(Exception ex){
            CoreUtils.print(ex);
        }
    }

    public byte[] decrypt(final byte[] bytes){
        try{
            return cipher.doFinal(bytes);
        }catch(Exception ex){
            CoreUtils.print(ex);
            return bytes;
        }
    }

    public PrivateKey key(){
        return key;
    }

    public RSAPrivateKeySpec spec(){
        return spec;
    }

    public Cipher cipher(){
        return cipher;
    }
}
