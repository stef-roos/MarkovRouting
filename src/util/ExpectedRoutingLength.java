package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import kadtype.Accuracy;
import kadtype.Alpha3Beta2Upper2;
import kadtype.Alpha4Beta1Upper;
import kadtype.KADA4B1Upper;
import kadtype.KADUpper;

public class ExpectedRoutingLength {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		System.out.println(Math.pow(2, 20));
//		String[] prefix = {"1", "3", "13", "15", "k1", "k2"};
//		try{
//			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/stef/svns/drafts/p2pmodel/" +
//						"results/bestPrac/hopCount.txt"));
//		for (int i = 0; i < 21; i++){
//			int n = (int)Math.pow(2, i)*1000;
//			String line = "" + n;
//			for (int j = 0; j < prefix.length; j++){
//				line = line + " " + getHopFromFile("/home/stef/svns/drafts/p2pmodel/" +
//						"results/bestPrac/"+prefix[j]+"-"+n+".txt");
//			}
//			bw.write(line);
//			bw.newLine();
//		}
//		bw.flush();
//		bw.close();
//		} catch (IOException e){
//			e.getStackTrace();
//		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/stef/svns/drafts/p2pmodel/" +
							"results/bestPrac/test.txt"));
			for (int j = 18; j < 21; j++){
				System.out.println("Sart " + j + " " + new Date());
				int n = (int)Math.pow(2, j)*1000;
				String line = "" + n;
				int b = Accuracy.getBitCount(10, n, 0.001, 128);
				int[] k = new int[b+1];
				for (int i = 0; i < k.length-1; i++){
					k[i] = 50;
				}
				k[k.length-1] = 80;
				line = line + " " +(new Alpha3Beta2Upper2(b,k,1)).getExpectedDegree(n);
				//line = line + " " +(new Alpha4Beta1Upper(b,k,1)).getExpectedDegree(n);
				//line = line + " " +(new KADUpper(b)).getExpectedDegree(n);
				//line = line + " " +(new KADA4B1Upper(b)).getExpectedDegree(n);
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String[] sys = {"kademlia", "kad", "kashmir"};
//		String[] writeSys = {"Kademlia", "KAD", "DBS"};
//		String[] routing = {"", "4-1"};
//		//String[] writeRouting = {"3-2", "4-1"};
//		String[] size = {"100K", "10M"};
//		for (int i = 0; i < 3; i++){
//			String line = writeSys[i];
//			for (int j = 0; j < size.length; j++){
//				for (int k = 0; k < routing.length; k++){
//					line = line + " & " + "(" +round(getHopFromFile("/home/stef/svns/p2pmodel/results/"+sys[i]+routing[k]+"upper"+size[j]+".txt"),2)
//							+ ","+round(getHopFromFile("/home/stef/svns/p2pmodel/results/"+sys[i]+routing[k]+"lower"+size[j]+".txt"),2) + ")";
//				}
//			}
//			System.out.println(line + " \\\\");
//		}

	}

	public static double getHopFromFile(String file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();
			double exp = 0;
			while ((line = br.readLine()) != null){
				String[] parts = line.split(" ");
				if (parts.length > 1){
					exp = exp + 1 - Double.parseDouble(parts[1]);
				}
			}
			return exp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static double round(double a, int digits){
		int factor = (int) Math.pow(10, digits);
		int r = (int)Math.round(a*factor);
		return r/(double)factor;
	}
	
	public static double getExpectedBits(int k, int l){
		double p = 1;
		double exp = 0;
		for (int i = 1; i < l; i++){
			p = p*0.5;
			exp = exp + 1 - Math.pow(1-p, k);
		}
		exp = exp + 1 - Math.pow(1-p, k);
		return exp;
	}
}
