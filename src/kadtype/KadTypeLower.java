package kadtype;

public abstract class KadTypeLower extends KadTypeCDFs {

	public KadTypeLower(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadTypeLower(int b, int alpha, int beta, int[] k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	public KadTypeLower(int b, int alpha, int beta, int k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}

	public KadTypeLower(int b, int alpha, int beta, int k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	protected void makeDistinct(int[][] returned, double[][] t, int index, int d1, int n, double p){
		int[][] max = new int[d1][2];
		for (int i=0; i < d1; i++){
			for (int j=0; j < this.alpha; j++){
				int count = 0;
				for (int m=0; m < this.beta; m++){
					if (returned[j][m] == i){
						count++;
					}
				}
				if (count > max[i][1]){
					max[i][0] = j;
					max[i][1] = count;
				}
			}
		}
		boolean[][] contain = new boolean[this.alpha][this.beta];
		this.recusivecombine(returned, t, index, 0, 0, n, max, contain, p);
	}
	
	protected void recusivecombine(int[][] returned, double[][] t, int index, 
			int a, int c, int n, int[][] max, boolean[][] contain, double p){
		while (a < this.alpha && 
				(returned[a][c] > max.length-1 
						|| max[returned[a][c]][0] == a)){
			if (returned[a][c] < max.length){
				contain[a][c] = true;
			}else {
				contain[a][c] = false;
			}
			int anew = (a*this.beta+c+1)/this.beta;
			c = (a*this.beta+c+1) % this.beta;
			a = anew;
			//System.out.println("a= " + a + " c= " + c + " returned-length " + returned.length);
		}
		if (a < this.alpha){
			//compute p'
			double pdash = 0;
			int ndash = n - this.alpha*(this.beta+1);
			int count = max[returned[a][c]][1];
			for (int i = 0; i < a; i++){
				if (max[returned[a][c]][0] != i){
					for (int j = 0; j < this.beta; j++){
						if (returned[a][c] == returned[i][j] && contain[i][j]){
							count++;
						}
					}
				}
			}
			for (int j = 0; j < c; j++){
				if (returned[a][c] == returned[a][j] && contain[a][j]){
					count--;
				}
			}
			double prob = Math.pow(2, returned[a][c]-this.b-1);
			double binom = Math.pow(1-prob, ndash);
			for (int m = 0; m < ndash; m++){
				pdash = pdash + binom*m/(double)(m+count);
				binom = binom*(ndash-m-1)/(double)(m+1)*prob/(1-prob); 
			}
			int adash = (a*this.beta+c+1)/this.beta;
			int cdash = (a*this.beta+c+1) % this.beta;
			contain[a][c] = true;
			this.recusivecombine(returned, t, index, adash, cdash, count, max, contain, p*pdash);
			contain[a][c] = false;
			this.recusivecombine(returned, t, index, adash, cdash, count, max, contain, p*(1-pdash));
		}else {
			//Evaluation
			int[][] combi = new int[this.alpha][this.beta];
			for (int i = 0; i < this.alpha; i++){
				for (int j = 0; j < this.beta; j++){
					if (contain[i][j]){
						combi[i][j] = returned[i][j]; 
					} else {
						combi[i][j] = b;
					}
				}
			}
			int[] next = this.topAlpha(combi);
			int indexN = this.getIndex(next);
			t[indexN][index] = t[indexN][index] + p;
		}
		
	}

}
