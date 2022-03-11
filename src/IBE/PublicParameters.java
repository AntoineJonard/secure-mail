
package IBE;

import java.io.Serializable;

import it.unisa.dia.gas.jpbc.Element;

/**
*
* @author Clément Decroix
*/
public class PublicParameters implements Serializable{
	private byte[] p; //generateur
	    
	private byte[] p_pub; // clef publique du système

	public PublicParameters(byte[] p, byte[] p_pub) {
		super();
		this.p = p;
		this.p_pub = p_pub;
	}

	public Element getP() {
		return Element
	}

	public void setP(Element p) {
		this.p = p;
	}

	public Element getP_pub() {
		return p_pub;
	}

	public void setP_pub(Element p_pub) {
		this.p_pub = p_pub;
	}
	   
	   
}
