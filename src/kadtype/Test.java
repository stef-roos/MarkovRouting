package kadtype;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import util.Binom;
import util.Calc;

import combined.CombinedSystems;

//import eclipse.Calc;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CombinedSystems kad = new CombinedSystems(new KademliaUpper(10,8), new KADUpper(10));
		double[] dist = kad.getRoutingLength(1000);
		for (int i = 0; i < dist.length; i++){
			System.out.println(i + " " + dist[i]);
		}
//		KadType kad = new KADUpper(13);
//		//kad.setRandomID(true);
//        double[] dist = kad.getRoutingLength(10000);
//		for (int i = 0; i < dist.length; i++){
//			System.out.println(i + " " + dist[i]);
//		}
//        double[][] t = kad.getT2(100);
//        for (int i = 0; i < t.length; i++){
//        	for (int j = 0; j < t[0].length; j++){
//        		if (t[i][j] < 0){
//        			System.out.println(t[i][j]);
//        		}
//        	}
//        }
		
		
		
//		double f = 0.15;
//		KadType kad = new FailureKademliaUpper(13,8,f);
//		double[] d = kad.getRoutingLength(10000);
//		System.out.println("Upper MDHT");
//		double mean = 0;
//		for (int i = 0; i < d.length; i++){
//			System.out.println(i + " " + d[i]);
//			mean = mean + 1-d[i];
//		}
//		System.out.println("Mean " + mean);
//		
//		kad = new FailureKademliaLower(13,8,f,6);
//		d = kad.getRoutingLength(10000);
//		System.out.println("Lower MDHT");
//		mean = 0;
//		for (int i = 0; i < d.length; i++){
//			System.out.println(i + " " + d[i]);
//			mean = mean + 1-d[i];
//		}
//		System.out.println("Mean " + mean);
//		
//		int[] ks = new int[14];
//		for (int i = 0; i < ks.length-4;i++){
//			ks[i] = 8;
//		}
//		ks[ks.length-1] = 128;
//		ks[ks.length-2] = 64;
//		ks[ks.length-3] = 32;
//		ks[ks.length-4] = 16;
//		
//		kad = new FailureAlpha3Beta2Upper(13,ks,1,f);
//		d = kad.getRoutingLength(10000);
//		System.out.println("Upper iMDHT");
//		mean = 0;
//		for (int i = 0; i < d.length; i++){
//			System.out.println(i + " " + d[i]);
//			mean = mean + 1-d[i];
//		}
//		System.out.println("Mean " + mean);
//		
//		kad = new FailureAlpha3Beta2Lower(13,ks,1,f,6);
//		d = kad.getRoutingLength(10000);
//		System.out.println("Lower iMDHT");
//		mean = 0;
//		for (int i = 0; i < d.length; i++){
//			System.out.println(i + " " + d[i]);
//			mean = mean + 1-d[i];
//		}
//		System.out.println("Mean " + mean);
//		
//		kad = new FailureKADUpper(13,f);
//		d = kad.getRoutingLength(10000);
//		System.out.println("Upper KAD");
//		mean = 0;
//		for (int i = 0; i < d.length; i++){
//			System.out.println(i + " " + d[i]);
//			mean = mean + 1-d[i];
//		}
//		System.out.println("Mean " + mean);
//		
//		kad = new FailureKADLower(13,f,6);
//		d = kad.getRoutingLength(10000);
//		System.out.println("Lower KAD");
//		mean = 0;
//		for (int i = 0; i < d.length; i++){
//			System.out.println(i + " " + d[i]);
//			mean = mean + 1-d[i];
//		}
//		System.out.println("Mean " + mean);
		
//        int[] n = {1000,10000,100000,1000000,10000000};
//        int mode = Integer.parseInt(args[0]);
//		//int[] b = new int[n.length];
//		for (int j = 0; j < n.length; j++){
//			int b = Accuracy.getBitCount(1, n[j], 0.001, 128);
//		    KadType kad = null;
//			if (mode == 0){
//				kad = new KademliaUpper(b,8);
//			} else {
//				if (mode < 4){
//					double[][] l = new double[b+1][b+1];
//					int[] k = new int[b+1];
//					int m = mode +1;
//					for (int i = 0; i < m; i++){
//						l[i][i] = 1; 
//						k[i] = (int) ((int) 8/(Math.pow(2, i)));
//					}
//					for (int i = m; i <= b; i++){
//						l[i][m] = 1; 
//						k[i] = (int) ((int) 8/(Math.pow(2, mode)));
//					}
//					kad = new Alpha3Beta2UpperMult(b,k,l,LType.ALL);
//				} else {
//					throw new IllegalArgumentException("Invalid mode");
//				}
//			}
//		    double[] dist = kad.getRoutingLength(n[j]);
//		    try {
//				BufferedWriter bw = new BufferedWriter(new FileWriter(n[j]+ "-"+ 
//				mode+".txt"));
//				for (int i = 0; i < dist.length; i++){
//					bw.write(i + " " + dist[i]);
//					bw.newLine();
//				}
//				bw.flush();
//				bw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}

		
	}
	
	public static double getExpDeg(int n, int k, int b){
		double exp = 0;
		for (int i = 0; i < b+1; i++){
			double sum = 1;
			double p = Math.pow(2, -i-1);
			for (int j= 0; j <k; j++){
				exp = exp +sum;
				sum = sum -Calc.binomDist(n, j, p);
			}
		}
		return exp;
	}
	
	public static void testPerformance(String[] args){
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
		case 5: cdf = (new KademliaA4B3Upper(b,k)).getRoutingLength(n); 
		break;
		case 6: cdf = (new KademliaA4B3Lower(b,k)).getRoutingLength(n); 
		break;
		case 7: cdf = (new KashmirUpper(b)).getRoutingLength(n); 
		break;
		case 8: cdf = (new KashmirLower(b)).getRoutingLength(n); 
		break;
		case 9: cdf = (new KashmirA4B1Upper(b)).getRoutingLength(n); 
		break;
		case 10: cdf = (new KashmirA4B1Lower(b)).getRoutingLength(n); 
		break;
		case 11: cdf = (new KashmirA4B3Upper(b)).getRoutingLength(n); 
		break;
		case 12: cdf = (new KashmirA4B3Lower(b)).getRoutingLength(n); 
		break;
		case 13: cdf = (new KADUpper(b)).getRoutingLength(n); 
		break;
		case 14: cdf = (new KADLower(b)).getRoutingLength(n); 
		break;
		case 15: cdf = (new KADA4B1Upper(b)).getRoutingLength(n); 
		break;
		case 16: cdf = (new KADA4B1Lower(b)).getRoutingLength(n); 
		break;
		case 17: cdf = (new KADA4B3Upper(b)).getRoutingLength(n); 
		break;
		case 18: cdf = (new KADA4B3Lower(b)).getRoutingLength(n); 
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
	
	public static void testTiming(String[] args){
		int mode = Integer.parseInt(args[0]);
		int max = Integer.parseInt(args[1]);
		int step = Integer.parseInt(args[2]);
		String file = args[3];
		int k;
		if (mode < 5){
			k = 8;
		} else {
			k = 10;
		}
		KadType kadtype;
		long[] times = new long[max/step+1];
		double[][] cdfs = new double[max/step][];
		for (int i = 1; i <= max/step; i++){
			int n = i*step;
			long start = System.currentTimeMillis();
			int b = Accuracy.getBitCount(k, n, 0.001, 160);
			switch (mode){
			case 1: cdfs[i-1] = (new KademliaUpper(b,k)).getRoutingLength(n); 
			break;
			case 2: cdfs[i-1] = (new KademliaLower(b,k)).getRoutingLength(n); 
			break;
			case 3: cdfs[i-1] = (new KademliaA4B1Upper(b,k)).getRoutingLength(n); 
			break;
			case 4: cdfs[i-1] = (new KademliaA4B1Lower(b,k)).getRoutingLength(n); 
			break;
			case 5: cdfs[i-1] = (new KADUpper(b)).getRoutingLength(n); 
			break;
			case 6: cdfs[i-1] = (new KADLower(b)).getRoutingLength(n); 
			break;
			case 7: cdfs[i-1] = (new KADA4B1Upper(b)).getRoutingLength(n); 
			break;
			case 8: cdfs[i-1] = (new KADA4B1Lower(b)).getRoutingLength(n); 
			break;
			default: throw new IllegalArgumentException("Modus not known");
			}
			times[i] =  (System.currentTimeMillis()-start)/1000;
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file + "Times.txt"));
			bw.write("#n, time in sec");
			for (int i = 0; i < times.length; i++){
				bw.newLine();
				bw.write(i*step + " " + times[i]);
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file + "CDFs.txt"));
			String line = "#hops";
			for (int i = 0; i < cdfs.length; i++){
				line = line + ", " + (i+1)*step;
			}
			bw.write(line);
			for (int j = 0; j < cdfs[cdfs.length-1].length; j++){
				bw.newLine();
				line = ""+j;
				for (int i = 0; i < cdfs.length; i++){
					line = line + " " + (cdfs[i].length>j?cdfs[i][j]:1);
				}
				bw.write(line);
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file + "Averages.txt"));
			bw.write("#n, average hop count");
			bw.newLine();
			bw.write("0 0"); 
			for (int i = 0; i < cdfs.length; i++){
				bw.newLine();
				//bw.write((i+1)*step + " " + Calc.getEx(cdfs[i]));
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testTimingSimple(String[] args){
		int mode = Integer.parseInt(args[0]);
		int max = Integer.parseInt(args[1]);
		int step = Integer.parseInt(args[2]);
		String file = args[3];
		int k;
		if (mode < 5){
			k = 8;
		} else {
			k = 10;
		}
		long[] times = new long[max/step+1];
		for (int i = 1; i <= max/step; i++){
			int n = i*step;
			long start = System.currentTimeMillis();
			int b = Accuracy.getBitCount(k, n, 0.001, 160);
			switch (mode){
			case 1: (new KademliaUpper(b,k)).getRoutingLength(n); 
			break;
			case 2: (new KademliaLower(b,k)).getRoutingLength(n); 
			break;
			case 3: (new KademliaA4B1Upper(b,k)).getRoutingLength(n); 
			break;
			case 4:  (new KademliaA4B1Lower(b,k)).getRoutingLength(n); 
			break;
			case 5: (new KADUpper(b)).getRoutingLength(n); 
			break;
			case 6: (new KADLower(b)).getRoutingLength(n); 
			break;
			case 7: (new KADA4B1Upper(b)).getRoutingLength(n); 
			break;
			case 8: (new KADA4B1Lower(b)).getRoutingLength(n); 
			break;
			default: throw new IllegalArgumentException("Modus not known");
			}
			times[i] =  (System.currentTimeMillis()-start)/1000;
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file + "Times.txt"));
			bw.write("#n, time in sec");
			for (int i = 0; i < times.length; i++){
				bw.newLine();
				bw.write(i*step + " " + times[i]);
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void errorRate(String[] args){
		int mode = Integer.parseInt(args[0]);
		int n = Integer.parseInt(args[1]);
		String file = args[2];
		double[] errors = new double[args.length-3];
		for (int i = 3; i < args.length; i++){
			errors[i-3] = Double.parseDouble(args[i]);
		}
		int k;
		if (mode < 5){
			k = 8;
		} else {
			k = 10;
		}
		KadType kadtype;
		long[] times = new long[errors.length];
		double[][] cdfs = new double[errors.length][];
		for (int i = 0; i <= errors.length; i++){
			long start = System.currentTimeMillis();
			int b = Accuracy.getBitCount(k, n, errors[i], 160);
			switch (mode){
			case 1: cdfs[i] = (new KademliaUpper(b,k)).getRoutingLength(n); 
			break;
			case 2: cdfs[i] = (new KademliaLower(b,k)).getRoutingLength(n); 
			break;
			case 3: cdfs[i] = (new KademliaA4B1Upper(b,k)).getRoutingLength(n); 
			break;
			case 4: cdfs[i] = (new KademliaA4B1Lower(b,k)).getRoutingLength(n); 
			break;
			case 5: cdfs[i] = (new KADUpper(b)).getRoutingLength(n); 
			break;
			case 6: cdfs[i] = (new KADLower(b)).getRoutingLength(n); 
			break;
			case 7: cdfs[i] = (new KADA4B1Upper(b)).getRoutingLength(n); 
			break;
			case 8: cdfs[i] = (new KADA4B1Lower(b)).getRoutingLength(n); 
			break;
			default: throw new IllegalArgumentException("Modus not known");
			}
			times[i] =  (System.currentTimeMillis()-start)/1000;
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file + "Times.txt"));
			bw.write("#error, time in sec");
			for (int i = 0; i < times.length; i++){
				bw.newLine();
				bw.write(errors[i] + " " + times[i]);
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file + "Diffs.txt"));
			String line = " ";
			for (int i = 0; i < errors.length; i++){
				line = line + " & " + errors[i];
			}
			bw.write(line + " \\\\");
			for (int j = 0; j < errors.length; j++){
				bw.newLine();
				line = "" + errors[j];
				for (int i = 0; i < errors.length; i++){
					line = line + " & " + maxDist(cdfs[i],cdfs[j]);
				}
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static double maxDist(double[] cdf1, double[] cdf2){
		double m = 0;
		for (int i = 0; i < Math.max(cdf1.length, cdf2.length); i++){
			double v1 = cdf1.length>i?Math.min(cdf1[i],1):1;
			double v2 = cdf2.length>i?Math.min(cdf2[i],1):1;
			double diff = v1-v2;
			if (Math.abs(diff) > Math.abs(m)){
				m = diff;
			}
		}
		return m;
	}
	
	public static void exp(){
		KadType kad = new KademliaUpper(10,8);
		int c = 2;
		double[][][] simu = new double[c][c][c]; 
		int it = 1000000;
		int[] draw = new int[8];
		Random rand = new Random();
		double[][] cdf = kad.getCDFs(c);
		for (int i = 0; i < cdf.length; i++){
			System.out.println(cdf[i][0]);
		}
		for (int i = 0; i < it; i++){
			for (int j = 0; j < 8; j++){
			double p = rand.nextDouble();
			int nr = 0;
			while (cdf[nr][0] < p){
				nr++;
			}
			draw[j] = nr;
			}
			Arrays.sort(draw);
			simu[draw[0]][draw[1]][draw[2]]++;
		}
		for (int i = 0; i < cdf.length; i++){
			for (int j = i; j < cdf.length; j++){
				for (int k = j; k < cdf.length; k++){
				simu[i][j][k] = simu[i][j][k]/(double)it;
				System.out.println(" i=" + i + " j=" + j + " k=" + k +" simu = " + simu[i][j][k] + " model= " + kad.getProb(new int[]{i,j,k}, c, 0));
				}
			}
		}
	}
	
	public static double getSuccessProb(int n, int b, int d, int k){
		double p = Math.pow(2, -d-b);
		int exp = (int) ((n-2)*p);
		Binom bi = new Binom(n-2,p,exp);
		double s = 0;
		double binom;
	  for (int i = exp; i < n-1; i++){
		  binom = bi.getNext();
		  if (i < k){
		         s = s + binom;
		  } else{
				 s = s + binom*(double)(k)/(double)(i+1);
		  }
	  }
	  bi.recompute(exp);
	  for (int i = exp-1; i > -1; i--){
		  binom = bi.getBefore();
		  if (i < k){
		         s = s + binom;
		  } else{
				 s = s + binom*(double)(k)/(double)(i+1);
		  }
	  }
	  return s;
	}

}


