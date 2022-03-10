/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IBE;

import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author Clément Decroix
 */
public class SettingParameters {
    
    private PublicParameters pp; //generateur
    
    private Element msk; // clef du maitre

	public SettingParameters(PublicParameters pp, Element msk) {
		super();
		this.pp = pp;
		this.msk = msk;
	}

	public PublicParameters getPp() {
		return pp;
	}

	public void setPp(PublicParameters pp) {
		this.pp = pp;
	}

	public Element getMsk() {
		return msk;
	}

	public void setMsk(Element msk) {
		this.msk = msk;
	}


   
    
}
