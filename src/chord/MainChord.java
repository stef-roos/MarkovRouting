package chord;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import eclipse.Calc;



public class MainChord {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int type = Integer.parseInt(args[0]);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
			double[] cdf;
		    for (int i = 1; i < 101; i++){
			  int n = i*100;
			  
			  int b = (int)Math.ceil(Math.log(n)/Math.log(2))+1;
			  System.out.println(n + " " + b);
			  switch (type) {
			  case 0: cdf = ChordFile.getRoutingCDF(b, n);
			         break;
			  case 1: cdf = ChordFileExt.getRoutingCDF(b, n);
		         break;   
		      default: cdf = ExactChordRouting.getRoutingCDFNode(b,n);   
			  }
			  bw.write(n + " " + Calc.getEx(cdf));
			  bw.newLine();
		    }
		    bw.flush();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter("attacksIncluded.txt"));
//		
//		int[] N = {100,   1000,   5000,    10000,    15000 };
//		int[] attacker  = {1, 2, 4, 8, 16, 25,  50, 100};
//		String line =  "Nodes ";
//		for (int i = 0; i < attacker.length; i++){
//			line = line + "	" + attacker[i];
//		}
//		bw.write(line);
//		for (int j = 0; j < N.length; j++){
//			line = N[j] + "";
//			for (int i = 0; i < attacker.length; i++){
//				line = line + "	" + AttackChord.randomAttack(N[j]-attacker[i], Integer.parseInt(args[0]), attacker[i], false);
//			}
//			bw.newLine();
//			bw.write(line);
//		}
//		bw.newLine();
//		bw.newLine();
//		double[] perc = {0.01, 0.05, 0.1, 0.2};
//		line =  "Nodes ";
//		for (int i = 0; i < perc.length; i++){
//			line = line + "	" + perc[i];
//		}
//		bw.write(line);
//		for (int j = 0; j < N.length; j++){
//			line = N[j] + "";
//			for (int i = 0; i < perc.length; i++){
//				line = line + "	" + AttackChord.randomAttack(N[j]-(int)(perc[i]*N[j]), Integer.parseInt(args[0]), (int)(perc[i]*N[j]), false);
//			}
//			bw.newLine();
//			bw.write(line);
//		}
//		bw.flush();
//		bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		double[] res100 = ExactChordRouting.getRoutingCDFNode(12, 100);
//		double[] res1000 = ExactChordRouting.getRoutingCDFNode(15, 1000);
//		double[] res5000 = ExactChordRouting.getRoutingCDFNode(15, 5000);
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter("results.txt"));
//			bw.write("# step, 100, 1000, 5000");
//			for (int k = 0; k < res5000.length; k++){
//				 bw.newLine(); 
//				 double r100 = (k<res100.length)?res100[k]:1;
//				 double r1000 = (k<res1000.length)?res1000[k]:1;
//	             bw.write(k + " " + r100 + " " + r1000 + " " + res5000[k]);
//	        }
//			bw.flush();
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
		
		
//		int[] n = {100,1000,5000,10000};
//		int[] b = {10};
////		int[] n = {10};
////		int[] b = {7};
//		(new File("ChordResults/")).mkdir();
//		for (int i = 0; i < b.length; i++){
//			for (int j = 0; j < n.length; j++){
//				double[] res = ExactChordRouting.getRoutingCDFFile(b[i], n[j]);
//				try {
//					BufferedWriter bw = new BufferedWriter(new FileWriter("ChordResults/b="+b[i]+"n="+n[j]+".txt"));
//					for (int k = 0; k < res.length; k++){
//						bw.write(k + " " + res[k]);
//						bw.newLine();
//					}
//					bw.flush();
//					bw.close();
//					System.out.println("Done with b="+b[i]+"n="+n[j]+"Node.txt " + new Date());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}

	}

}
