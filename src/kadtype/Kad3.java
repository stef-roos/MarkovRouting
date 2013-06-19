package kadtype;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Kad3 {
	
	public static void main(String[] args) {
		int modus = Integer.parseInt(args[0]);
		double error = Double.parseDouble(args[1]);
		int n = Integer.parseInt(args[2]);
		int aimB = 160;
		int b = Accuracy.getBitCount(10, n, error, aimB);
		int[] k = new int[b+1];
		k[b] = 80;
		for (int i = 0; i < b; i++){
			k[i] = 50;
		}
		double[] cdf = new double[0];
		switch (modus){
		case 1: cdf = (new Alpha3Beta2Upper2(b,k,1)).getRoutingLength(n);
		break;
		case 2: cdf = (new Alpha4Beta1Upper2(b,k,1)).getRoutingLength(n); 
		break;
		default: throw new IllegalArgumentException("Modus not known");
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[3]));
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
