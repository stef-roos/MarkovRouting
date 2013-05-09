package util;

public class Hyper1 {
	int found;
	int att;
	int benign;
	int k;
	double old = -1;
	
	public Hyper1(int found, int att,int k){
		this.found = found;
		this.att = att;
		this.k = k;
		this.benign = 0;
	}
	
	public Hyper1(int found, int att, int k,int benign){
		this.found = found;
		this.att = att;
		this.k = k;
		this.benign = benign;
	}
	
	public double getNext(){
		if (old <= 0){
		  recompute(benign);
		} else {
			old = old*(benign)/(double)(benign+1+att)*(benign+att+1-k)/(double)(benign-k+found);
		}
		//System.out.println(benign);
		benign++;
		return old;
	}
	
	public double getBefore(){
		benign--;
		if (old <= 0){
			  recompute(benign);
			} else {
				old = old/(double)(benign+1)*(double)(benign+2+att)/(double)(benign+att+2-k)*(double)(benign-k+found+1);
			}
		return old;
	}
	
	public void recompute(int benign){
		old = Calc.binom(att, found);
		if (old > 0){
			if (benign + att >= k){
		  for (int i = benign;i > benign-k+found; i--){
			old = old*i/(double)(i+att+1);
		  }
		  for (int j = k-found; j < k; j++){
			old = old*(j+1)/(double)(benign+att+1-j);
		  }
			} else {
				old = 0;
			}
		}
		this.benign = benign;
		if (!(old < 1)){
			System.out.println("old " + old + " att"+att + " benign " + benign);
			System.exit(0);
		}
	}
	

}
