package kadtype;

public class Accuracy {
	
	public static int getBitCount(int k, int n, double error, int aimB){
		int b = 1;
		while (getError(k,n,b,aimB) > error){
			b++;
		}
		return b;
	}
	
   public static double getError(int k, int n, int b, int aimB){
		double p = Math.pow(2, -b-1) - Math.pow(2, -aimB-1);
		double q = Math.pow(1-p, n);
		double t = 0;
		for (int i = 0; i < k+1; i++){
			t = t + q;
			q = q*(n-i)/(double)(i+1)*p/(1-p);
		}
		return 1-t;
	}

}
