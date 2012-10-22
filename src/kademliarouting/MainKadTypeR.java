package kademliarouting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class MainKadTypeR {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double[] kaduni = (new KademliaUniform(10,8,100)).getRoutingCDF();
//		double[] kad = (new KademliaRouting(20,8,1000)).getRoutingCDF();
//		double[] kadlocal = (new KademliaLocal(20,8,1000)).getRoutingCDF();
//		
//		for (int i = 0; i < kad.length; i++){
//			System.out.println(i + " " + kad[i] + " " + kaduni[i] + " " + kadlocal[i]);
//		}

	}

}
