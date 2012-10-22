package eclipse;

import java.util.Date;

import kademliarouting.KademliaRouting;

public class MainAttackAnalysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Start " + new Date());
		KademliaRouting kad = new KademliaRouting(20,10,1000);
//		double[][] f = kad.getF(5);
//		for (int i = 0; i < f.length; i++){
//			System.out.println(f[i][0] + " " + f[i][1]);
//		}
		double[] res = kad.getRoutingCDF();
		System.out.println("Done " + new Date());
		
		for (int i = 0; i < res.length; i++){
			System.out.println("i= " + i + " " + res[i]);
		}
		int k = 20;
		int a = 10;
		int n = 1000;
		int b = 4;
		// 1.Random placement
		System.out.println("Random:");
		// 1a) Chord
		//System.out.println("Chord: " + AttackChord.randomAttack(n, 12, a, false));
		// b) Pastry
		//System.out.println("Pastry: "
			//	+ RandomPastry.getAttackEfficiency(n, a, b));
		// c) Kademlia
		//System.out.println("Kademlia: "
			//	+ RandomKademlia.getAttackEfficiency(n, a, k));

		// 1.Targeted placement
//		System.out.println("Targeted:");
//		// 1a) Chord
          // System.out.println("Chord: " + AttackChord.targetedAttack(n, 128));
//		// b) Pastry
//		System.out.println("Pastry: "
//				+ TargetedPastry.getAttackEfficiency(n, a, b));
		// c) Kademlia
		//System.out.println("Kademlia: "
			//	+ TargetedKademlia.getAttackEfficiency(n, a, k));
	}

}
