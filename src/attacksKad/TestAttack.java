package attacksKad;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import kadtype.KadType;





public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Hyper1 h = new Hyper1(0,0,3);
//		for (int i = 0; i < 5; i++){
//			System.out.println(i + " " +h.getNext());
//		}
		
		int b = 25;
		int[] k = new int[b+1];
		for (int i = 0; i < 16; i++){
			k[i] = 8;
		}
		for (int i = 16; i < k.length; i++){
			k[i] = 9;
		}
		double[][] l = new double[b+1][b+1];
		l[b][4] = 1;
		for (int i = 4; i < b; i++){
			l[i][3] = 0.75;
			l[i][4] = 0.25;
		}
		l[3][3] = 1;
		l[2][2] = 1;
		l[1][1] = 1;
		double rate = Double.parseDouble(args[1]);
		int ttl = Integer.parseInt(args[2]);
        try{
         BufferedWriter bw = new BufferedWriter(new FileWriter(args[0]));
         bw.write("Upper");
         double[] cdf = (new FailureAlpha3Beta2Upper(b,k,l, KadType.LType.ALL,rate)).getRoutingLength(1000000);
         double ex = 0;
         for (int i = 0; i< cdf.length; i++){
        	 ex = ex + 1 -cdf[i];
        	 bw.write(i + " " + cdf[i]);
        	 bw.newLine();
         }
         bw.write("expectation :" + ex);
         bw.newLine();
         bw.newLine();
         bw.write("Upper");
         cdf = (new FailureAlpha3Beta2Lower(b,k,l, KadType.LType.ALL,rate,ttl)).getRoutingLength(1000000);
         ex = 0;
         for (int i = 0; i< cdf.length; i++){
        	 ex = ex + 1 -cdf[i];
        	 bw.write(i + " " + cdf[i]);
        	 bw.newLine();
         }
         bw.write("expectation :" + ex);
         bw.flush();
         bw.close();
        }catch (IOException e){
        	e.printStackTrace();
        }
		
	}

}
