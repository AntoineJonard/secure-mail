
package IBE;

import it.unisa.dia.gas.jpbc.Element;

/**
*
* @author Clément Decroix
*/
public class PublicParameters {
	private Element p; //generateur
	    
	private Element p_pub; // clef publique du système

	public PublicParameters(Element p, Element p_pub) {
		super();
		this.p = p;
		this.p_pub = p_pub;
	}

	public Element getP() {
		return p;
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
