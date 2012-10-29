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
		
		//int k = Integer.parseInt(args[0]);
		int n = Integer.parseInt(args[0]);
		int bits = Math.max((int)(Math.log(n)/Math.log(2)) + 2,10);
		//kademlia(args);
		double[] kad = (new KademliaB(bits,n)).getRoutingCDF();
		double[] kaduni = (new KademliaBUniformUpper(bits,n)).getRoutingCDF();
		double[] kadlocal = (new KademliaBLocal(bits,n, true)).getRoutingCDF();
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
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

}
