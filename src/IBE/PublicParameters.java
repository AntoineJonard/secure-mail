
package IBE;

import java.io.Serializable;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

/**
*
* @author Clément Decroix
*/
public class PublicParameters implements Serializable{
	private byte[] p; //generateur
	    
	private byte[] p_pub; // clef publique du système

	public PublicParameters(byte[] p, byte[] p_pub) {
		super();
		this.p = p.toBytes();
		this.p_pub = p_pub.toBytes();
	}

<<<<<<< HEAD
	public Element getP() {
		return Element
=======
	public Element getP(Pairing pairing) {
		return pairing.getG1().newElementFromBytes(p);
>>>>>>> 9704d4a7c490de437097c61718cbb8c113102bc6
	}

	public void setP(Element p) {
		this.p = p.toBytes();
	}

	public Element getP_pub(Pairing pairing) {
		return pairing.getG1().newElementFromBytes(p_pub);
	}

	public void setP_pub(Element p_pub) {
		this.p_pub = p_pub.toBytes();
	}
	   
	   
}
