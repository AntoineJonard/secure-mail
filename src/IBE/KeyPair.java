/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IBE;

import java.io.Serializable;

import it.unisa.dia.gas.jpbc.Element;

/**
 *
 * @author imino
 */
public class KeyPair implements Serializable{
   
  public  String pk; //identité de l'utilisateur
  private  Element sk; // clef privée de l'utilisateur

    public KeyPair(String pk, Element sk) {
        this.pk = pk;
        this.sk = sk;
    }

    public String getPk() {
        return pk;
    }

    public Element getSk() {
        return sk;
    }

	public void setPk(String pk) {
		this.pk = pk;
	}

	public void setSk(Element sk) {
		this.sk = sk;
	}
    
    
    
}
