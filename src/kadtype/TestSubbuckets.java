package kadtype;

import util.Calc;

public class TestSubbuckets {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KadType kad = new KademliaLower(8, 8);
		// double[] normal = kad.getRoutingLength(1000);
		kad.setSubbuckets(true);
		kad.setN(100);

//		 kad.setSuccess(100);
//		 double[][] t = kad.getT1(1000);
//		 for (int i = 0; i < t[0].length; i++){
//		 double sum = 0;
//		 for (int j = 0; j < t.length; j++){
//		 sum = sum + t[j][i];
//		 }
//		 System.out.println(sum);
//		 }
		int nr = 8;
		int l = 0;
		int addBit = 3;
		int regions = 8;
		int r = 1;
		int remainder = 0;
		double pall = 0;
		 for (int c1 = 5; c1 < nr; c1++){
			 for (int c2 = c1; c2 < nr; c2++){
				 double p = 1;
			 int countc = (int) Math.pow(2, addBit - (nr - c1-l));
				if (r > countc-2){
				  for (int i = 0; i < countc-1; i++){
					  p = p*(r-i)/(regions-1-i);
				  }
				  } else {
					  p = 0;
				  }
				  //c) the other link: at least one contact in subbuckets at this level (countc)
				double pe = 1;
				  if (2*countc <= r+1){
					  pe = 1-Calc.binom(regions-2*countc, regions-1-r)/
							  (double)Calc.binom(regions-countc, regions-1-r);
				  }
				p = p*pe;
				double q = 0;
				
					
				if (c1 == c2){
					//there are at least two non-empty subbuckets with that prefix length
					//if (regions - 2 * countc >= regions - 1 - r) {
//						double pE = (1 - Calc.binom(regions - 2 * countc,
//								regions - 1 - r)
//								/ (double) Calc.binom(regions - countc, regions
//										- 1 - r));
						if (pe != 1) {
							double pN = (pe - Calc.binom(regions - 2 * countc,
									regions - 2 - r)
									* Calc.binom(countc, 1)
									/ (double) Calc.binom(regions - countc,
											regions - 1 - r))
									/ (pe);
							// or if not, there are at least two links into the
							// one region
							double p2 = (1 - pN)
									* (1 - Math.pow(1 - 1 / (double) (regions
											- r - 1), r + remainder));
							p = p * (pN + p2);
							q = q + pN+p2;
						} else {
							if
							
							//p = 0;
						}
//					} else {
//						p = 0;
//					}
				} else {
					//there is exactly one non-empty region and countc
					double pE;
					// and this region gets only one link
					if (pe > 0 && (regions - 2 * countc >= regions - 2 - r)){
					pE = (1-(pe - Calc.binom(regions - 2 * countc,
							regions - 2 - r)
							* Calc.binom(countc, 1)
							/ (double) Calc.binom(regions - countc,
									regions - 1 - r))
							/ (pe))*Math.pow(1 - 1 / (double) (regions - r - 1), r
									+ remainder);
					} else {
						pE = 0; 
					}
					// all regions between c1 and c2 are empty
					p = p * pE;
					double q2 = pE;
					int countc2 = (int) Math.pow(2, addBit - (nr - c2-l));
					if (r - countc2 + 2 >= 0) {
						for (int i = countc; i < countc2 - 1; i++) {
							p = p * (r - i) / (regions - i);
							q2 = q2*(r - i) / (regions - i);
						}
					} else {
						p = 0;
						q2 = 0;
					}
					// there is at least one non-empty region at c2
					if (regions - 2 * countc2 >= regions - 1 - r) {
						p = p
								* (1 - Calc.binom(regions - 2 * countc2,
										regions - 1 - r)
										/ (double) Calc.binom(
												regions - countc2, regions - 1
														- r));
						q2 = q2* (1 - Calc.binom(regions - 2 * countc2,
								regions - 1 - r)
								/ (double) Calc.binom(
										regions - countc2, regions - 1
												- r));
					}
					//q = q + q2;
				} 
				pall = pall+p;
				//System.out.println("c1 " + c1 + " c2 " + c2 + " p" + p);
	    	}
				//System.out.println(q);
		 }
		System.out.println(pall);
		// double[] sub = kad.getRoutingLength(100);
		// // kad.setSubbuckets(false);
		// // kad.setLocal(true);
		// // double[] local = kad.getRoutingLength(1000);
		// for (int i = 0; i < sub.length; i++){
		// System.out.println(i + " " + sub[i] );
		// // System.out.println(i + " " + normal[i] + " " + sub[i] + " " +
		// local[i]);
		// }
	}

}
