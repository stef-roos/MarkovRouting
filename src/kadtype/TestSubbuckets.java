package kadtype;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestSubbuckets {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		String mode = args[0]; 
//		int b = Integer.parseInt(args[1]);
//		int k = Integer.parseInt(args[2]);
//		int n = Integer.parseInt(args[3]);
//		String out = args[4];
		String mode = "kademlia"; 
		int b = 13;
		int k = 8;
		int n = 1000;
		String out = "kademlia10000.txt";
		
		KadType kad;
		if (mode.equals("kademlia")){
		 kad = new KademliaUpper(b,k);
		} else {
			if (mode.equals("kad")){
			kad = new KADUpper(b);
			} else {
				if (mode.equals("imdht")){
					kad = new KashmirUpper(b);
				} else {
				   throw new IllegalArgumentException("Not implemented for this system");
				}
			}
		}
		kad.setRandomID(true);
		//double[] normal = kad.getRoutingLength(n);
		kad.setSubbuckets(true);
		//kad.setN(100);

		 double[] sub = kad.getRoutingLength(n);
		  kad.setSubbuckets(false);
		  //kad.setLocal(true);
		//  double[] local = kad.getRoutingLength(n);
//		  try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(out));
//		    double[] exp = new double[2];
//		 for (int i = 0; i < sub.length; i++){
//		 //System.out.println(i + " " + sub[i] );
//		 bw.write(i + " " + normal[i] + " " + sub[i]);// + " " +
//		// local[i]);
//		 exp[0] = exp[0] + 1 - normal[i];
//		 exp[1] = exp[1] + 1 - sub[i];
//		// exp[2] = exp[2] + 1 - local[i];
//		 bw.newLine();
//		 }
//		 bw.write("#Expected " + exp[0] + " " + exp[1] );//+ " " + exp[2]);
//		 bw.flush();
//		 bw.close();
//		  } catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}

}

//for (int r = 0; r < 7; r++){
//int nr = 8;
//int l = 0;
//int addBit = 3;
//int regions = 8;
//int remainder = 0;
//double pall = 0;
// for (int c1 = 5; c1 < nr; c1++){
//	 for (int c2 = c1; c2 < nr; c2++){
//		 double p = 1;
//	 int countc = (int) Math.pow(2, addBit - (nr - c1-l));
//		if (r > countc-2){
//		  for (int i = 0; i < countc-1; i++){
//			  p = p*(r-i)/(regions-1-i);
//		  }
//		  } else {
//			  p = 0;
//		  }
//		  //c) the other link: at least one contact in subbuckets at this level (countc)
//		double pe = 1;
//		  if (2*countc <= r+1){
//			  pe = 1-Calc.binom(regions-2*countc, regions-1-r)/
//					  (double)Calc.binom(regions-countc, regions-1-r);
//		  }
//		p = p*pe;
//		double q = 0;
//			
//		if (c1 == c2){
//			//there are at least two non-empty subbuckets with that prefix length
//			//if (regions - 2 * countc >= regions - 1 - r) {
////				double pE = (1 - Calc.binom(regions - 2 * countc,
////						regions - 1 - r)
////						/ (double) Calc.binom(regions - countc, regions
////								- 1 - r));
//				if (pe != 1) {
//					double pN = (pe - Calc.binom(regions - 2 * countc,
//							regions - 2 - r)
//							* Calc.binom(countc, 1)
//							/ (double) Calc.binom(regions - countc,
//									regions - 1 - r))
//							/ (pe);
//					// or if not, there are at least two links into the
//					// one region
//					double p2 = (1 - pN)
//							* (1 - Math.pow(1 - 1 / (double) (regions
//									- r - 1), r + remainder));
//					p = p * (pN + p2);
//					q = q + pN+p2;
//				} else {
//					if (regions - countc >= regions-1-r){
//						if (2*countc <= r+2){
//							double pN = 1-Calc.binom(regions - 2 * countc,
//									regions - 2 - r)
//									* Calc.binom(countc, 1)
//									/ (double) Calc.binom(regions - countc,
//											regions - 1 - r);
//							double p2 = (1 - pN)
//									* (1 - Math.pow(1 - 1 / (double) (regions
//											- r - 1), r + remainder));
//							p = p*(pN+p2);
//						}
//					}
//					
//						//p = 0;
//				}
////			} else {
////				p = 0;
////			}
//		} else {
//			//there is exactly one non-empty region and countc
//			double pE=1;
//			if (pe != 1) {
//				double pN = (pe - Calc.binom(regions - 2 * countc,
//						regions - 2 - r)
//						* Calc.binom(countc, 1)
//						/ (double) Calc.binom(regions - countc,
//								regions - 1 - r))
//						/ (pe);
//				// or if not, there are at least two links into the
//				// one region
//				double p2 = Math.pow(1 - 1 / (double) (regions
//								- r - 1), r + remainder);
//				pE = (1-pN)*p2;
//			} else {
//				if (regions - countc >= regions-1-r){
//					if (2*countc <= r+2){
//						double pN = 1-Calc.binom(regions - 2 * countc,
//								regions - 2 - r)
//								* Calc.binom(countc, 1)
//								/ (double) Calc.binom(regions - countc,
//										regions - 1 - r);
//						double p2 = Math.pow(1 - 1 / (double) (regions
//										- r - 1), r + remainder);
//						pE = (1-pN)*p2;
//					}
//				} else {
//					pE = 0;
//				}
//				
//					//p = 0;
//			}
//			p = p*pE;
//			double q2 = 1;
//			int countc2 = (int) Math.pow(2, addBit - (nr - c2-l));
//			if ( 2*countc  <= r+2) {
//				p = p* Calc.binom(regions-countc2, regions-r-2)/(double)Calc.binom(regions-2*countc,regions-r-2);
//				q2 =q2* Calc.binom(regions-countc2, regions-r-2)/(double)Calc.binom(regions-2*countc,regions-r-2);
////				for (int i = countc-1; i < countc2 - 2; i++) {
////					p = p * (r - i) / (regions - i-1);
////					q2 = q2*(r - i) / (regions - i-1);
////				}
//			} else {
//				p = 0;
//				q2 = 0;
//			}
//			// there is at least one non-empty region at c2
//			if (regions - 2 * countc2 >= regions - 2 - r) {
//				p = p
//						* (1 - Calc.binom(regions - 2 * countc2,
//								regions - 2 - r)
//								/ (double) Calc.binom(
//										regions - countc2, regions - 2
//												- r));
//				q2 = q2* (1 - Calc.binom(regions - 2 * countc2,
//						regions - 2 - r)
//						/ (double) Calc.binom(
//								regions - countc2, regions - 2
//										- r));
//			}
//			q = q + q2;
//			//System.out.println(q2);
//		} 
//		pall = pall+p;
//		//System.out.println("c1 " + c1 + " c2 " + c2 + " p" + p);
//	}
//		
//		//System.out.println("c1 " + c1 + " " + q);
// }
//System.out.println(pall);
//}
