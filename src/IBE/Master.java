package IBE;


import it.unisa.dia.gas.jpbc.Element;


import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;




public class Master {
	//Init Pairings
	CurveParams curveParams = new CurveParams().load("curve.properties");
	Pairing pairing = PairingFactory.getPairing(curveParams);
	
	
	// Generate system parameters

	Element g = pairing.getG1().newRandomElement().getImmutable();

	// Generate the secret key

	Element MasterSecretKey = pairing.getZr().newRandomElement();

	2 fonctions de hachage
}
