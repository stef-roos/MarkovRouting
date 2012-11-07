package kademliarouting;

public class KademliaAttack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int a = 100;
		int n = 10000;
		int k = 8;
		
		int b = (int)Math.ceil(Math.log(n+a)) +2;
		
		System.out.println("benign nodes = " + n);
		System.out.println("malicious nodes = " + a);
		System.out.println("bucket size = " + k);
		System.out.println("Attack efficiency = " + (new TargetedAttack(b,k,n,a)).getAttackEfficiency());
	}

}
