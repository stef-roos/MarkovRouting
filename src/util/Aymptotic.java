package util;

public class Aymptotic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int k = 4;
		int logk = 4;
		double eps = 0.001;
		for (int i = 0; i < k; i++){
			System.out.println(splitfixed(k,k-i) + " " + nonsplit(k-i,eps));
		}
        //System.out.println(nonsplit(1,0.01));
	}
	
	private static double split(double p, int logk){
		double sum = 0;
		double ppower = p;
		for (int i = 1; i < logk; i++){
			sum = sum + ppower*(1-ppower)*(logk+1-i);
			ppower = ppower*p;
		}
		return (1-p)*(logk+2)+ sum;
	}
	
	private static double nonsplit(double p, int k, double epsilon){
		if (p == 0) return nonsplit(k,epsilon);
		double sum = 0;
		Binom bi = new Binom(k,1-p);
		for (int i = 0; i <= k; i++){
			double s = 0;
			double diff = 1;
			int j = 0;
			while (diff > epsilon){
				diff = 1 - Math.pow(1-Math.pow(2, -j), i);
				j++;
				s = s + diff;
			}
			sum = sum + bi.getNext()*s;
		}
		return sum;
	}
	
	private static double nonsplit(int k,double epsilon){
            double s = 0;
			double diff = 1;
			int j = 0;
			while (diff > epsilon){
				diff = 1 - Math.pow(1-Math.pow(2, -j), k);
				j++;
				s = s + diff;
			}
		return s;
	}
	
	private static double splitfixed(int k, int l){
		double logk = Math.log(k)/Math.log(2);
		double s = l/(double)k * (logk +2);
		int all = (int)Calc.binom(k, l);
		for (int i = 0; i <= logk;i++){
			double c = Calc.binom(k-(int)Math.pow(2,i), l);
			if (c == 0) continue;
			s = s +c/(double)all*(1-Calc.binom(k-(int)Math.pow(2,i+1), l)/(double)c)*(logk-i);
		}
		return s;
	}
	
	

}
