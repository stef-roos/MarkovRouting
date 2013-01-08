package kademliarouting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KademliaAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(0.01850456728505509
				*42475);
//		for (int i = 0; i < 10; i++){
//			keygeneration(i+1);
//		}
//		int a = 10;
//		int n = 10000;
//		int k = 8;
//		
//		int b = (int)Math.ceil(Math.log(n+a)) +2;
//		
//		System.out.println("benign nodes = " + n);
//		System.out.println("malicious nodes = " + a);
//		System.out.println("bucket size = " + k);
//		System.out.println("Attack efficiency = " + (new TargetedAttack(b,k,n,a)).getAttackEfficiency());
	}
	
	public static void keygeneration(int nr){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("/home/stef/svns/paper-WWW/performance/keygeneration"+nr+".txt"));
			for (int i = 0; i < nr; i++){
				bw.write((i+1) +"	" + nr*(i+1) + "	" + 1);
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
