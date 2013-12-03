package attacksKad;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import kadtype.Accuracy;





public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] kad = (new FailureKademliaUpper(14,8,0.44)).getRoutingLength(10000);
		double mean = 0;
		for (int i = 0; i < kad.length; i++){
			System.out.println(i + " " + kad[i]);
			mean = mean + 1-kad[i];
		}
		System.out.println(mean);
//		int modus = Integer.parseInt(args[0]);
//		int k = 8;
//		double error = Double.parseDouble(args[1]);
//		double rate = Double.parseDouble(args[2]);
//		int ttl = Integer.parseInt(args[3]);
//		int start = Integer.parseInt(args[4]);
//		for (int i = start; i < 21; i++){
//			System.out.println("i="+i );
//		int n = (int)(Math.pow(2, i)*1000);
//		int aimB = 160;
//		int b = Accuracy.getBitCount(k, n, error, aimB);
//		double[] cdf = new double[0];
//		switch (modus){
//		case 101: cdf = (new FailureKademliaUpper(b,8,rate)).getRoutingLength(n); break;
//		case 102: cdf = (new FailureKademliaLower(b,8,rate,ttl)).getRoutingLength(n);; break;
//		case 103: cdf = (new FailureKademliaA4B1Upper(b,8,rate)).getRoutingLength(n); break;
//		case 104: cdf = (new FailureKademliaA4B1Lower(b,8,rate,ttl)).getRoutingLength(n); break;
//		case 105: cdf = (new FailureKADUpper(b,rate)).getRoutingLength(n); break;
//		case 106: cdf = (new FailureKADLower(b,rate,ttl)).getRoutingLength(n); break;
//		case 107: cdf = (new FailureKADA4B1Upper(b,rate)).getRoutingLength(n); break;
//		case 108: cdf = (new FailureKADA4B1Lower(b,rate, ttl)).getRoutingLength(n); break;
//		default: throw new IllegalArgumentException();
//		}
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(args[5]+ n+".txt"));
//			for (int j = 0; j < cdf.length; j++){
//				bw.write(j + " " + cdf[j]);
//				bw.newLine();
//			}
//			bw.flush();
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		}
		
	}
	
	
	

}
