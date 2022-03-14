package server;

import java.io.Serializable;
import java.security.PublicKey;

public class ClientAskMessage implements Serializable {

    PublicKey pk;
    byte[] passwordHash;
    
    private static final long serialVersionUID = 6529685098267757691L;

    public ClientAskMessage(PublicKey pk, byte[] passwordHash) {
        this.pk = pk;
        this.passwordHash = passwordHash;
    }

    public PublicKey getPk() {
        return pk;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }
}
