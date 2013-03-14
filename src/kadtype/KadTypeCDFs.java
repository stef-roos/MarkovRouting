package kadtype;

/**
 * implementations for getIndex(), getCDFs for alpha, beta <=4
 * @author stefanie
 *
 */

public abstract class KadTypeCDFs extends KadType {

	/**
	 * constructor: see super
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 * @param ltype
	 */
	public KadTypeCDFs(int b, int alpha, int beta, int[] k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}
	
	public KadTypeCDFs(int b, int alpha, int beta, int k, double[][] l,
			LType ltype) {
		super(b, alpha, beta, k, l, ltype);
	}
	
	/**
	 * constructor: see super
	 * @param b
	 * @param alpha
	 * @param beta
	 * @param k
	 * @param l
	 */
	public KadTypeCDFs(int b, int alpha, int beta, int[] k, int l) {
		super(b, alpha, beta, k, l);
	}
	
	public KadTypeCDFs(int b, int alpha, int beta, int k, int l) {
		super(b, alpha, beta, k, l);
	}

	protected int getIndex(int[] lookups){
		switch (this.alpha){
		case 1: return lookups[0]+1;
		case 2: return (int) Math.round(0.5 * lookups[1] * (lookups[1] + 1)) + lookups[0] + 1;
		case 3: return this.getIndexThree(lookups[0], lookups[1], lookups[2]);
		case 4: return this.getIndexFour(lookups[0], lookups[1], lookups[2], lookups[3]);
		default: throw new IllegalArgumentException("Only implemented for alpha/beta <= 4");
		}
		
	}
	
	/**
	 * index for alpha=3
	 * index(a,b,c) = c(c+1)(2c+1)/12+b(b+1)/2+a+1
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
    private int getIndexThree(int a, int b, int c){
    	int index = (int) Math.round(0.5 * c * (c + 1)
				* ((double) (2 * c + 1) / (double) 6 + 0.5));
		index = index + (int) Math.round(0.5 * b * (b + 1)) + a+1;
		return index;
	}
    
    /**
     * index for alpha=4
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    private int getIndexFour(int a, int b, int c, int d){
    	int index = (int)(Math.pow(d*(d+1)*0.5,2)/6.0 + 7.0/12.0*d*(d+1)*(2*d+1)/6.0
    			+ d*(d+1)/6.0);
    	index = index + this.getIndexThree(a, b, c);
		return index;
	}

	@Override
	protected double[][] getCDFs(int d, int c) {
		if (d==0){
			int count = d*c;
			if (this.ltype == LType.SIMPLE){
				count = c;
			}
			double[][] res = new double[1][count];
			for (int i = 0; i < res[0].length; i++){
				res[0][i] = 1;
			}
		}
		switch (c) {
		case 1: return this.getCDFsOne(d); 
		case 2: return this.getCDFsTwo(d); 
		case 3: return this.getCDFsThree(d); 
		case 4: return this.getCDFsFour(d); 
		default: throw new IllegalArgumentException("Only implemented for alpha/beta <= 4");
		}
	}

	/**
	 * cdf for distance after next step for one returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsOne(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1}};
			}
		    double[][] res = new double[d-digit+1][1];
		    double p = 1;
		    for (int i = res.length-1; i > -1; i--){
		    	res[i][0] = 1 - Math.pow(1-p, this.k[d]);
		    	p = p*0.5;
		    }
		    return res;
		}  
		if (this.ltype == LType.ALL){
			double[][] res = new double[d][d];
			for (int a = 1; a < d+1; a++){
				if (l[d][a] > 0){
		      double p = 1;
		      for (int i = res.length-a; i > -1; i--){
		    	res[i][a-1] = 1 - Math.pow(1-p, this.k[d]);
		    	p = p*0.5;
		      }
			 }
			}
		    return res;
		} 
		return null;
	}
	
	/**
	 * cdf for distance after next step for two returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsTwo(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1,1}};
			}
		    double[][] res = new double[d-digit+1][2];
		    double p = 1;
		    //probability prop to fraction of remaining ID space size
		    for (int i = res.length-1; i > -1; i--){
		    	res[i][0] = 1 - Math.pow(1-p, this.k[d]);
		    	res[i][1] = 1 - Math.pow(1-p, this.k[d]-1);
		    	p = p*0.5;
		    }
		    return res;
		}  
		if (this.ltype == LType.ALL){
			double[][] res = new double[d][2*d];
			for (int a = 1; a < d+1; a++){
				if (l[d][a] > 0){
		      double p = 1;
		      for (int i = res.length-a; i > -1; i--){
		    	res[i][a-1] = 1 - Math.pow(1-p, this.k[d]);
		    	res[i][d + a-1] = 1 - Math.pow(1-p, this.k[d]-1);
		    	p = p*0.5;
		      }
			 }
			}
		    return res;
		} 
		return null;
	}
	
	/**
	 * cdf for distance after next step for three returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsThree(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1,1,1}};
			}
		    double[][] res = new double[d-digit+1][3];
		    double p = 1;
		    for (int i = res.length-1; i > -1; i--){
		    	double diff = Math.pow(1-p, this.k[d]-2);
		    	res[i][2] = 1 - diff;
		    	diff = diff*(1-p);
		    	res[i][1] = 1 - diff;
		    	res[i][0] = 1 - diff*(1-p);
		    	p = p*0.5;
		    }
		    for (int i = 0; i < res.length-1; i++){
		    	for (int j = 0; j < res[i].length; j++){
		    		if (!(res[i][j]< 1)){
		    			System.out.println(res[i][j]);
		    		}
		    	}
		    }
		    return res;
		}  
		if (this.ltype == LType.ALL){
			double[][] res = new double[d][3*d];
			for (int a = 1; a < d+1; a++){
				if (l[d][a] > 0){
		      double p = 1;
		      for (int i = res.length-a; i > -1; i--){
		    	  double diff = Math.pow(1-p, this.k[d]-2);
			    	res[i][2*d+a-1] = 1 - diff;
			    	diff = diff*(1-p);
			    	res[i][d+a-1] = 1 - diff;
			    	res[i][a-1] = 1 - diff*(1-p);
			    	p = p*0.5;
		      }
			 }
			}
		    return res;
		} 
		return null;
	}
	
	/**
	 * cdf for distance after next step for four returned values
	 * @param d
	 * @return
	 */
	private double[][] getCDFsFour(int d){
		if (this.ltype == LType.SIMPLE){
			int digit = (int)this.l[0][0];
			if (d-digit < 1){
				return new double[][]{new double[]{1,1,1,1}};
			}
		    double[][] res = new double[d-digit+1][4];
		    double p = 1;
		    for (int i = res.length-1; i > -1; i--){
		    	double diff = Math.pow(1-p, this.k[d]-3);
		    	res[i][3] = 1 - diff;
		    	diff = diff*(1-p);
		    	res[i][2] = 1 - diff;
		    	diff = diff*(1-p);
		    	res[i][1] = 1 - diff;
		    	res[i][0] = 1 - diff*(1-p);
		    	p = p*0.5;
		    }
		    return res;
		}  
		if (this.ltype == LType.ALL){
			double[][] res = new double[d][4*d];
			for (int a = 1; a < d+1; a++){
				if (l[d][a] > 0){
		      double p = 1;
		      for (int i = res.length-a; i > -1; i--){
		    	  double diff = Math.pow(1-p, this.k[d]-3);
		    	  res[i][3*d+a-1] = 1 - diff;
			    	diff = diff*(1-p);
			    	res[i][2*d+a-1] = 1 - diff;
			    	diff = diff*(1-p);
			    	res[i][d+a-1] = 1 - diff;
			    	res[i][a-1] = 1 - diff*(1-p);
			    	p = p*0.5;
		      }
			 }
			}
		    return res;
		} 
		return null;
	}

}
