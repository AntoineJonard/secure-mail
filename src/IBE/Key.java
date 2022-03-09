package IBE;


import it.unisa.dia.gas.jpbc.Element;



public class Key {
    private Element pubkey;
    private Element secretkey;

    public Key(Element pubkey, Element secretkey) {
        this.pubkey = pubkey;
        this.secretkey = secretkey;
    }

    public Element getPubkey() {
        return pubkey;
    }

    public Element getSecretkey() {
        return secretkey;
    }
}
