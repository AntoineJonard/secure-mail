package RSAFAST;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

public class AsymmetricCryptography {

    private Cipher cipher;

    public AsymmetricCryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA");
    }

    public byte[] encryptBytes(byte[] secretKeyBytes, PublicKey key)
            throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(secretKeyBytes);
    }

    public byte[] decryptBytes(byte[] secretKeyBytes, PrivateKey key)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(secretKeyBytes);
    }

}
