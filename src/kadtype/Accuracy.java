package kadtype;

import util.Binom;


/**
 * compute the accuracy when using less bits
 * @author stefanie
 *
 */
public class Accuracy {
	
	/**
	 * number of bits needed to achieve desired error
	 * @param k: max. size of lowest capacity bucket
	 * @param n
	 * @param error
	 * @param aimB: the actual number of bits, standard = 160
	 * @return
	 */
	public static int getBitCount(int k, int n, double error, int aimB){
		int b = 1;
		while (getError(k,n,b,aimB) > error){
			b++;
		}
		return b;
	}
	
	/**
	 * compute accuracy of using b rather than aimB bits
	 * @param k: maximal bucket size
	 * @param n: #nodes
	 * @param b: bits used
	 * @param aimB: bits in real systems
	 * @return
	 */
   public static double getError(int k, int n, int b, int aimB){
	   //prob to have longer prefix than b
		double p = Math.pow(2, -b-1) - Math.pow(2, -aimB-1);
		//T= prob that less than k nodes have longer prefix
		double q = Math.pow(1-p, n);
		double t = 0;
		for (int i = 0; i < k+1; i++){
			t = t + q;
			q = q*(n-i)/(double)(i+1)*p/(1-p);
		}
		//unaccuracy happen only if more than k are nearer
		return 1-t;
	}
   
   public static double successNonRegion(int b, int[] k, int n){
	   double p = 0;
	   double rp = 0.5;
	   for (int l = 0; l < k.length; l++){
		   double p0 = Math.pow(1-rp, n-1);
		   double p1 = 0;
		   double pn = 0.5;
		   for (int m = l+1; m < k.length; m++){
			   double pl = 1/(1-rp)*pn*rp;
			 // System.out.println("   " + m + " pl= " + pl);
			   double psum = 1;
			   Binom bi = new Binom(n-1,pl);

			  // System.out.println("   test 0" + Math.pow(1-pl, n-1));
			   for (int j = 0; j <= k[b-m]; j++){
				   psum = psum - bi.getNext();
			   }
			  // System.out.println("   " + m + " psum= " + psum);
			   p1 = p1 + pn*psum;
			   pn = pn/2;
		   }
		   p = p + (1-p)*p0*p1;
		 //  System.out.println(l + " p0= " + p0 + " p1=" + p1);
		   rp = rp/2;
	   }
	   
	   return p;
   }

}
