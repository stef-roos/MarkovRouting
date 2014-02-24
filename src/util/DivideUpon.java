package util;

public class DivideUpon {
	int n;
	int c;
	int l = 1;
	double p = 0;
	
	public DivideUpon(int n, int c){
		this.n = n;
		this.c = c;
	}
	
	public double getNext(){
		if (p <= 0){
			p = 1;
			int m = Math.min(c-2, l-1);
			for (int i = 0; i< m; i++){
				p = p*(i+1)/(double)(n+i+1)*(n-l+i+1)/(double)(i+1);
			}
			for (int i = m; i < c-2; i++){
				p = p*(i+1)/(double)(n+i+1);
			}
			for (int i = m; i < l-1; i++){
				p = p*(n-l+i+1)/(double)(i+1);
			}
			for (int i = 0; i < c-1-l; i++){
				p=p*(c-1-i)/(double)(c-1-l-i);
			}
		} else {
			p = p*(n-l+1)/(double)(l-1)*(c-l)/(double)(l);
		}
		l++;
		return p;
	}

}
