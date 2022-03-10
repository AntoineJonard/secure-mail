/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IBE;

import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author imine / Clément Decroix
 */
public class IBEcipher {
    
    private Element U; // rP (vu dans le cours)
    
    byte[] V; // K xor e(Q_id,P_pub) avec K la clef symmetrique AES
    
    byte[] Aescipher; // résultat du chiffrement avec AES

    
    
    public IBEcipher(Element U, byte[] V, byte[] Aescipher) {
        this.U = U;
        this.V = V;
        this.Aescipher = Aescipher;
    }
    
    

    public byte[] getAescipher() {
        return Aescipher;
    }

    public Element getU() {
        return U;
    }

    public byte[] getV() {
        return V;
    }

	public void setU(Element u) {
		U = u;
	}

	public void setV(byte[] v) {
		V = v;
	}

	public void setAescipher(byte[] aescipher) {
		Aescipher = aescipher;
	}
    
    
}
