package server;

import java.io.Serializable;
import java.security.PublicKey;

public class ClientAskMessage implements Serializable {

    PublicKey pk;
    byte[] passwordHash;

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
