package RSAFAST;

public class DecryptionException extends Exception{

    public DecryptionException() {

        super(message);
        System.out.println("Error while decrypting secret key");

    }
}
