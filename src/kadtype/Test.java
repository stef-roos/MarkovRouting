package kadtype;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int modus = Integer.parseInt(args[0]);
		int k = Integer.parseInt(args[1]);
		double error = Double.parseDouble(args[2]);
		int n = Integer.parseInt(args[3]);
		int aimB = 160;
		int b = Accuracy.getBitCount(k, n, error, aimB);
		
		double[] cdf = new double[0];
		switch (modus){
		case 1: cdf = (new KademliaUpper(b,k)).getRoutingLength(n); 
		break;
		case 2: cdf = (new KademliaLower(b,k)).getRoutingLength(n); 
		break;
		case 3: cdf = (new KademliaA4B1Upper(b,k)).getRoutingLength(n); 
		break;
		case 4: cdf = (new KademliaA4B1Lower(b,k)).getRoutingLength(n); 
		break;
		case 5: cdf = (new KashmirUpper(b)).getRoutingLength(n); 
		break;
		case 6: cdf = (new KashmirLower(b)).getRoutingLength(n); 
		break;
		case 7: cdf = (new KashmirA4B1Upper(b)).getRoutingLength(n); 
		break;
		case 8: cdf = (new KashmirA4B1Lower(b)).getRoutingLength(n); 
		break;
		case 9: cdf = (new KADUpper(b)).getRoutingLength(n); 
		break;
		case 10: cdf = (new KADLower(b)).getRoutingLength(n); 
		break;
		case 11: cdf = (new KADA4B1Upper(b)).getRoutingLength(n); 
		break;
		case 12: cdf = (new KADA4B1Lower(b)).getRoutingLength(n); 
		break;
		default: throw new IllegalArgumentException("Modus not known");
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[4]));
			bw.write("#b="+b + "n="+n);
			for (int i = 0; i < cdf.length; i++){
				bw.newLine();
				bw.write(i + " " + cdf[i]);
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
