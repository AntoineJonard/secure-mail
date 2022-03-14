
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

	public PublicParameters(Element p, Element p_pub) {
		super();
		this.p = p.toBytes();
		this.p_pub = p_pub.toBytes();
	}

	public Element getP(Pairing pairing) {
		return pairing.getG1().newElementFromBytes(p);
	}

	public Element getP_pub(Pairing pairing) {
		return pairing.getG1().newElementFromBytes(p_pub);
	}

}
