package eclipse.old;

public class KademliaEclipse {
	
	public static double[] getExpectedQ(int alpha, int bits, int k){
		double[] res = new double[bits];
		res[0] = alpha*0.5;
		double[] pG = new double[bits];
		for (int i = 0; i < bits; i++){
			pG[i] = Math.pow(1 - Math.pow(0.5, i), k);
		}
		for (int i = 1; i < bits; i++){
			res[i] = alpha*Math.pow(0.5,i+1);
			for (int j = 0; j < i; j++){
				res[i] = res[i] + res[j]*(pG[i-j]-pG[i-j-1]);
			}
		}
		return res;
	}
	
	public static double[] b(int alpha, int bits, int k, double[] queries, int N, int a){
		double[] b = new double[bits];
		b[0] = 1;
		for (int i = 1; i < b.length; i++){
			b[i] = b[i-1]*Math.max(0,1-queries[i-1]*(a)*Math.pow(2,i)/(double)N*k);
		}
		return b;
	}
	
	public static double[] v(int alpha, int bits, int k, double[] queries, int N){
		double[] b = new double[bits];
		b[0] = 1;
		for (int i = 1; i < b.length; i++){
			b[i] = b[i-1]*Math.max(0,1-queries[i-1]*Math.pow(2,i)/(double)N*k);
		}
		return b;
	}
	
	public static double probAttack(int alpha, int bits, int k, int N, int a){
		double[] r = getExpectedQ(alpha,bits,k);
		double[] b0 = b(alpha,bits,k,r,N,a+1);
		double[] b1 = b(alpha,bits,k,r,N,a);
		double[] b2 = b(alpha,bits,k,r,N,a-1);
		double[] v = v(alpha,bits,k,r,N);
		double[] c = new double[bits];
		for (int i = 0; i < c.length; i++){
			c[i] = Math.min(1, 2*Math.pow(2,i)/(double)N*k);
		}
		double p = 0;
		for (int i = 0; i < r.length-2; i++){
			p = p + r[i]*b1[i]*Math.min(1,a*c[i])*(v[i+2] + Math.min(1,(a-1)*c[i+1])*c[i+2]*v[i+1] + Math.min(1,(a-1)*(a-2)*c[i]*c[i])*c[i+1]*v[i]);
			//System.out.println(r[i] + " " + b[i] + " " +v[i] + " " + c[i]);
		}
		return p;
	}

}
