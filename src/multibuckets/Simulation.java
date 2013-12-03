package multibuckets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import kadtype.Accuracy;
import kadtype.Alpha1Beta1;
import kadtype.KadType;
import kadtype.KadType.LType;
import kadtype.KademliaUpper;
import attacksKad.FailureKademliaUpper;

public class Simulation {
	
	public static void main(String[] args) {
		failure(args);
	}
	
	private static void splits32(String[] args){
      int[] n = {1000,10000,100000,1000000,10000000};
      int mode = Integer.parseInt(args[0]);
		//int[] b = new int[n.length];
		for (int j = 0; j < n.length; j++){
			int b = Accuracy.getBitCount(1, n[j], 0.001, 128);
		    KadType kad = null;
			if (mode == 0){
				kad = new KademliaUpper(b,8);
			} else {
				if (mode < 4){
					double[][] l = new double[b+1][b+1];
					int[] k = new int[b+1];
					int m = mode +1;
					for (int i = 0; i < m; i++){
						l[i][i] = 1; 
						k[i] = (int) ((int) 8/(Math.pow(2, i)));
					}
					for (int i = m; i <= b; i++){
						l[i][m] = 1; 
						k[i] = (int) ((int) 8/(Math.pow(2, mode)));
					}
					kad = new Alpha3Beta2UpperMult(b,k,l,LType.ALL);
				} else {
					throw new IllegalArgumentException("Invalid mode");
				}
			}
		    double[] dist = kad.getRoutingLength(n[j]);
		    try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(n[j]+ "-"+ 
				mode+".txt"));
				for (int i = 0; i < dist.length; i++){
					bw.write(i + " " + dist[i]);
					bw.newLine();
				}
				bw.flush();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public static void failure(String[] args){
		int[] n = {1000000};
		//int[] n = {100};
		double[] p = {0.0,0.05,0.01,0.15,0.2,0.25,0.3,0.35};
		//double[] p = {0.05};
		
	      int mode = Integer.parseInt(args[0]);
			//int[] b = new int[n.length];
			for (int j = 0; j < n.length; j++){
				for (int a = 0; a< p.length; a++){
				int b = Accuracy.getBitCount(1, n[j], 0.001, 128);
			    KadType kad = null;
				if (mode == 0){
					kad = new FailureKademliaUpper(b,8,p[a]);
				} else {
					if (mode < 4){
						double[][] l = new double[b+1][b+1];
						int[] k = new int[b+1];
						int m = mode +1;
						for (int i = 0; i < m; i++){
							l[i][i] = 1; 
							k[i] = (int) ((int) 8/(Math.pow(2, i)));
						}
						for (int i = m; i <= b; i++){
							l[i][m] = 1; 
							k[i] = (int) ((int) 8/(Math.pow(2, mode)));
						}
						kad = new FailureAlpha3Beta2UpperMult(b,k,l,LType.ALL,p[a]);
					} else {
						throw new IllegalArgumentException("Invalid mode");
					}
				}
			    double[] dist = kad.getRoutingLength(n[j]);
			    try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(n[j]+ "-"+ 
					mode+"-"+p[a]+".txt"));
					for (int i = 0; i < dist.length; i++){
						bw.write(i + " " + dist[i]);
						bw.newLine();
					}
					bw.flush();
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
	}
	
	public static void split11(String[] args){
		int[] n = {1000,10000,100000,1000000,10000000};
	      int mode = Integer.parseInt(args[0]);
			//int[] b = new int[n.length];
			for (int j = 0; j < n.length; j++){
				int b = Accuracy.getBitCount(1, n[j], 0.001, 128);
			    KadType kad = null;
				if (mode == 0){
					kad = new Alpha1Beta1(b,8,1);
				} else {
					if (mode < 4){
						double[][] l = new double[b+1][b+1];
						int[] k = new int[b+1];
						int m = mode +1;
						for (int i = 0; i < m; i++){
							l[i][i] = 1; 
							k[i] = (int) ((int) 8/(Math.pow(2, i)));
						}
						for (int i = m; i <= b; i++){
							l[i][m] = 1; 
							k[i] = (int) ((int) 8/(Math.pow(2, mode)));
						}
						kad = new Alpha1Beta1(b,k,l,LType.ALL);
					} else {
						throw new IllegalArgumentException("Invalid mode");
					}
				}
			    double[] dist = kad.getRoutingLength(n[j]);
			    try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(n[j]+ "-"+ 
					mode+"One.txt"));
					for (int i = 0; i < dist.length; i++){
						bw.write(i + " " + dist[i]);
						bw.newLine();
					}
					bw.flush();
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
	}

}
