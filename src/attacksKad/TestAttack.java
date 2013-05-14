package attacksKad;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import kadtype.Test;





public class TestAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Hyper1 h = new Hyper1(0,0,3);
//		for (int i = 0; i < 5; i++){
//			System.out.println(i + " " +h.getNext());
//		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("failuresKAD1M.txt"));
			double[] cdf = (new FailureKADUpper(18,0.1)).getRoutingLength(1000000); 
			bw.write("Upper bound:");
			double ex = 0;
			for (int i = 0; i < cdf.length; i++){
				bw.newLine();
				bw.write(i + " " + cdf[i]);
				ex = ex + 1 - cdf[i];
			}
		    bw.newLine();
		    bw.write("Expected " + ex);
		    
		    bw.newLine();
		    bw.newLine();
		    
		    cdf = (new FailureKADLower(18,0.1,6)).getRoutingLength(1000000); 
			bw.write("Lower bound:");
			ex = 0;
			for (int i = 0; i < cdf.length; i++){
				bw.newLine();
				bw.write(i + " " + cdf[i]);
				ex = ex + 1 - cdf[i];
			}
		    bw.newLine();
		    bw.write("Expected " + ex);
		    bw.flush();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
