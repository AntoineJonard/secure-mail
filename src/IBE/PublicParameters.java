
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
	
	private static final long serialVersionUID = 6529685098267757690L;

	public PublicParameters(byte[] p, byte[] p_pub) {
		super();
		this.p = p;
		this.p_pub = p_pub;
	}


	public Element getP(Pairing pairing) {
		return pairing.getG1().newElementFromBytes(p);
	}

	public Element getP_pub(Pairing pairing) {
		return pairing.getG1().newElementFromBytes(p_pub);
	}

}
