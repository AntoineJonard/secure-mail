package IBE;


import it.unisa.dia.gas.jpbc.Element;


import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;




public class Master {
	//Init Pairings
	Pairing pairing = PairingFactory.getPairing("a.properties");
	
	// Generate system parameters

	Element g = pairing.getG1().newRandomElement().getImmutable();

	// Generate the secret key

	Element MasterSecretKey = pairing.getZr().newRandomElement();

}
