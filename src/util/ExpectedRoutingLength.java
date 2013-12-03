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
		System.out.println(98.2*33177);
		//System.out.println(getHopFromFile("/home/stef/svns/drafts/DarknetApproaches/RDarknet/recursive/pathLength/xvine.txt"));
		//makeAbsolute("/home/stef/svns/drafts/DarknetApproaches/RDarknet/recursive/pathLength/swap.txt", "/home/stef/svns/drafts/DarknetApproaches/RDarknet/recursive/pathLength/swapAbsolute.txt");
//		String[] prefix = {"5", "8", "10", "20", "30", "40", "50"};
//		try{
//			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/stef/svns/drafts/p2pmodel/" +
//						"results/test/hopCount.txt"));
//			bw.write("# 5,8,10,20,30,40,50");
//		for (int i = 0; i < 21; i++){
//			int n = (int)Math.pow(2, i);
//			String line = "" + n;
//			for (int j = 0; j < prefix.length; j++){
//				line = line + " " + getHopFromFile("/home/stef/svns/drafts/p2pmodel/" +
//						"results/test/"+prefix[j]+"-"+n+".txt");
//			}
//			bw.newLine();
//			bw.write(line);
//			
//		}
//		bw.flush();
//		bw.close();
//		} catch (IOException e){
//			e.getStackTrace();
//		}
		makeAbsolute("/home/stef/svns/drafts/DarknetApproaches/" +
					"RDarknet/recursive/pathLength/data/routing/weibull-swap.txt",
					"/home/stef/svns/drafts/DarknetApproaches/" +
						"RDarknet/recursive/pathLength/data/routing/weibull-swapA.txt");
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/stef/svns/drafts/DarknetApproaches/" +
//					"RDarknet/recursive/pathLength/data/routing/lognormal-swap.txt"));
//			BufferedReader br = new BufferedReader(new FileReader("/home/stef/svns/drafts/DarknetApproaches/" +
//					"RDarknet/recursive/pathLength/data/routing/lognormal-swap.txt"));
//			br.readLine();
//			String line;
//			while ((line = br.readLine())!= null){
//				String[] parts = line.split(" ");
//				String l = parts[0];
//				double[] f = new double[4];
//				for (int i = 0; i < 4; i++){
//					f[i] = Double.parseDouble(parts[i+1]);
//				}
//				double[] r = new double[12];
//				for (int i = 0; i < 12; i++){
//					r[i] = (Double.parseDouble(parts[i+5])-f[i/3])/f[i/3];
//					l = l + " " + r[i];
//				}
//				bw.write(l);
//				bw.newLine();
//			}
//			br.close();
//			bw.flush();
//			bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
//		try{
//			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/stef/svns/drafts/p2pmodel/" +
//						"results/kadComp/successHop-13.txt"));	
//		for (int i = 0; i < 21; i++){
//			int n = (int)Math.pow(2, i)*1000;
//			double[] cdf =getHopDistFromFile("/home/stef/svns/drafts/p2pmodel/" +
//						"results/kadComp/13-"+n+".txt",30);
//			String line = "" + n;
//			for (int j = 0; j < Math.min(9, cdf.length); j++){
//				line = line + " " + cdf[j];
//			}	
//			bw.newLine();
//			bw.write(line);
//		}
//		bw.flush();
//		bw.close();
//		} catch (IOException e){
//			e.getStackTrace();
//		}
		


	}
	
	public static double[] getHopDistFromFile(String file, int length){
		double[] cdf = new double[length+1];
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			//br.readLine();
			int c = 0;
			while ((line = br.readLine()) != null){
				if (line.startsWith("#")) continue;
				String[] parts = line.split(" ");
				cdf[c] = Double.parseDouble(parts[1]);
				c++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cdf;
	}

	public static double getHopFromFile(String file){
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			//br.readLine();
			double exp = 0;
			while ((line = br.readLine()) != null){
				if (line.startsWith("#")) continue;
				String[] parts = line.split(" ");
				if (parts.length > 1){
					exp = exp + 1 - Double.parseDouble(parts[1]);
				}
			}
			br.close();
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
	
	public static void makeAbsolute(String path, String newFile){
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			//br.readLine();
			double exp = 0;
			while ((line = br.readLine()) != null){
				String[] parts = line.split("	");
				exp = Double.parseDouble(parts[1]);
			}
			br.close();
			br = new BufferedReader(new FileReader(path));
			BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
			while ((line = br.readLine()) != null){
				String[] parts = line.split("	");
				double cur = Double.parseDouble(parts[1]);
				bw.write(parts[0] + "	" + cur/exp);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
