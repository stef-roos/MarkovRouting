package util;

public class Binom {
	private int n;
	private int current;
	private double p;
	private double old = -1;
	
	public Binom(int n, double p){
		this.n = n;
		this.current = 0;
		this.p = p;
	}
	
	public Binom(int n, double p, int cur){
		this.n = n;
		this.current = cur;
		this.p = p;
	}
	
	public double getNext(){
		if (old == -1){
			old = Calc.binomDist(n, current, p);
		} else {
			old = old*(n-current+1)/(double)(current)*p/(1-p);
		}
		this.current++;
		return old;
	}
	
	public double getBefore(){
		if (old == -1){
			old = Calc.binomDist(n, current, p);
		} else {
			old = old/(n-current+1)*(double)(current)*(1-p)/p;
		}
		this.current--;
		return old;
	}
	
	public void recompute(int current){
		this.current = current;
		old = Calc.binomDist(n, current, p);
	}

}
