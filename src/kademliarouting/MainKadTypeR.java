package kademliarouting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import eclipse.Calc;

public class MainKadTypeR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] kad= (new KademliaRouting(10,8,100)).getRoutingCDF();
		for (int i = 0; i < kad.length; i++){
			System.out.println(i  + " " +kad[i]);
		}
//		double[] kadfile = (new KademliaFile2(15,1000,8)).getRoutingCDF();
//		double[] kad = (new KademliaFileUniform(15,8,1000)).getRoutingCDF();
//		for (int i = 0; i < kadfile.length; i++){
//			System.out.println(i + " " +kadfile[i] + " " +kad[i]);
//		}
//		//int k = Integer.parseInt(args[0]);
//		int n = Integer.parseInt(args[0]);
//		int bits = Math.max((int)(Math.log(n)/Math.log(2)) + 2,10);
//		//kademlia(args);
//		double[] kad = (new KademliaB(bits,n)).getRoutingCDF();
//		double[] kaduni = (new KademliaBUniformUpper(bits,n)).getRoutingCDF();
//		double[] kadlocal = (new KademliaBLocal(bits,n, true)).getRoutingCDF();
//		
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
//			bw.write("-----------CDF Routing Hops-----");
//		    bw.newLine();
//			bw.write("Steps Original	Uniform	Local");
//		    for (int i = 0; i < kad.length; i++){
//		    	bw.newLine();
//			    bw.write(i + " " + kad[i] + " " + kaduni[i] + " " + kadlocal[i]);
//		    }
//		    bw.newLine();
//		    bw.newLine();
//		    bw.write("-----------Expected Routing Hops-----");
//		    bw.newLine();
//		    bw.write("original " + Calc.getEx(kad));
//		    bw.newLine();
//		    bw.write("Uniform " + Calc.getEx(kaduni));
//		    bw.newLine();
//		    bw.write("Local " + Calc.getEx(kadlocal));
//		    bw.flush();
//		    bw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
	
	private static void kademlia(String[] args){
		int k = Integer.parseInt(args[0]);
		int n = Integer.parseInt(args[1]);
		int bits = (int)(Math.log(n)/Math.log(2)) + 2;
		double[] kad = (new KademliaRouting(bits,k,n)).getRoutingCDF();
		double[] kaduni = (new KademliaUniform(bits,k,n)).getRoutingCDF();
		double[] kadlocal = (new KademliaLocal(bits,k,n, false)).getRoutingCDF();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[2]));
			bw.write("-----------CDF Routing Hops-----");
		    bw.newLine();
			bw.write("Steps Original	Uniform	Local");
		    for (int i = 0; i < kad.length; i++){
		    	bw.newLine();
			    bw.write(i + " " + kad[i] + " " + kaduni[i] + " " + kadlocal[i]);
		    }
		    bw.newLine();
		    bw.newLine();
		    bw.write("-----------Expected Routing Hops-----");
		    bw.newLine();
		    bw.write("original " + Calc.getEx(kad));
		    bw.newLine();
		    bw.write("Uniform " + Calc.getEx(kaduni));
		    bw.newLine();
		    bw.write("Local " + Calc.getEx(kadlocal));
		    bw.flush();
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getExpectedNeighbors(int n, int k, int b){
		double p = 0.5;
		double exp = 0;
		for (int i = 0; i < b; i++){
			double sum = Calc.binomDist(n-1, 0, p);
			for (int j = 1; j < k+1; j++){
				exp = exp + 1 - sum;
				sum = sum + Calc.binomDist(n-1, j, p);
			}
			p = p*0.5;
			System.out.println(exp + " " +i);
		}
		
		return exp;
	}

}
