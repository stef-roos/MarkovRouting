package util;

public class BucketEmpty {

	public static void main(String[] args) {
		System.out.println(kad(10000, 128));
	}

	public static double kademlia(int n, int k, int b) {
		double max = 0;
		double p = 0.5;
		for (int i = 1; i <= b; i++) {
			double pempty = Math.pow(1 - p, n);
			double pmorek = 0;
			double q = 0.5 * p;
			for (int j = i + 1; j < b; j++) {
				double c = 1;
				Binom bi = new Binom(n, q / (1 - p));
				for (int l = 0; l < k + 1; l++) {
					c = c - bi.getNext();
				}
				q = q * 0.5;
				pmorek = pmorek + c;
			}
			p = p * 0.5;
			if (pmorek * pempty > max) {
				max = pmorek * pempty;
			}
		}

		return max;
	}

	public static double kad(int n, int b) {
		double max = 0;
		double p = 0.5/16;
		for (int i = 1; i <= b; i++) {
			double pmorek = 0;
			double q = 0.5 * p;
			for (int j = i + 1; j < b; j++) {
				double c = 1;
				Binom bi = new Binom(n, q / (1 - p));
				for (int l = 0; l < 10 + 1; l++) {
					c = c - bi.getNext();
				}
				q = q * 0.5;
				pmorek = pmorek + c;
			}
			p = p * 0.5;
			//System.out.println(i + " " + pmorek);
			double pdash3 = p / 8;
			double pdash4 = pdash3 * 0.5;
			double pempty3 = Math.pow(1 - pdash3, n);
			double pempty4 = Math.pow(1 - pdash4, n);
			for (int j = 0; j < 3; j++) {
				double c2 = 1;
				double c3 = 1;
				double c4 = 1;
				double c5 = 1;
				double pemptyn2 = 0;
				double pemptyn3 = 0;
				double pemptyn4 = 0;
				double pemptyn5 = 0;
				double pemptyn1 = 0;
				if (j == 0) {
					pemptyn1 = pempty3;
					Binom bi2 = new Binom(n, pdash3 / (1 - pdash3));
					pemptyn2 = bi2.getNext();
					c2 = 1 - pemptyn2;
					for (int l = 1; l < 11; l++) {
						c2 = c2 - bi2.getNext();
					}
					Binom bi3 = new Binom(n, pdash3 / (1 - pdash3 * 2));
					pemptyn3 = bi3.getNext();
					c3 = 1 - pemptyn3;
					for (int l = 1; l < 11; l++) {
						c3 = c3 - bi3.getNext();
					}
					Binom bi4 = new Binom(n, pdash4 / (1 - pdash3 * 3));
					pemptyn4 = bi4.getNext();
					c4 = 1 - pemptyn4;
					for (int l = 1; l < 11; l++) {
						c4 = c4 - bi4.getNext();
					}
					Binom bi5 = new Binom(n, pdash4 / (1 - pdash3 * 3 - pdash4));
					pemptyn5 = bi5.getNext();
					c3 = 1 - pemptyn5;
					for (int l = 1; l < 11; l++) {
						c5 = c5 - bi5.getNext();
					}
				} else {
                   if (j == 1){
                	   pemptyn1 = pempty3;
                	   Binom bi2 = new Binom(n, pdash4 / (1 - pdash3));
   					pemptyn2 = bi2.getNext();
   					c2 = 1 - pemptyn2;
   					for (int l = 1; l < 11; l++) {
   						c2 = c2 - bi2.getNext();
   					}
   					Binom bi3 = new Binom(n, pdash4 / (1 - pdash3 - pdash4));
   					System.out.println(pdash4 / (1 - pdash3-pdash4));
   					pemptyn3 = bi3.getNext();
   					c3 = 1 - pemptyn3;
   					for (int l = 1; l < 11; l++) {
   						c3 = c3 - bi3.getNext();
   					}
   					Binom bi4 = new Binom(n, pdash3 / (1 - pdash3 -pdash4*2));
   					pemptyn4 = bi4.getNext();
   					c4 = 1 - pemptyn4;
   					for (int l = 1; l < 11; l++) {
   						c4 = c4 - bi4.getNext();
   					}
   					Binom bi5 = new Binom(n, pdash3 / (1 - pdash3 * 2 - pdash4*2));
   					pemptyn5 = bi5.getNext();
   					c3 = 1 - pemptyn5;
   					for (int l = 1; l < 11; l++) {
   						c5 = c5 - bi5.getNext();
   					} 
                   } else {
                	   pemptyn1 = pempty4;
                	   Binom bi2 = new Binom(n, pdash4 / (1 - pdash4));
   					pemptyn2 = bi2.getNext();
   					c2 = 1 - pemptyn2;
   					for (int l = 1; l < 11; l++) {
   						c2 = c2 - bi2.getNext();
   					}
   					Binom bi3 = new Binom(n, pdash3 / (1 - pdash4 * 2));
   					pemptyn3 = bi3.getNext();
   					c3 = 1 - pemptyn3;
   					for (int l = 1; l < 11; l++) {
   						c3 = c3 - bi3.getNext();
   					}
   					Binom bi4 = new Binom(n, pdash3 / (1 - pdash3 -pdash4*2));
   					pemptyn4 = bi4.getNext();
   					c4 = 1 - pemptyn4;
   					for (int l = 1; l < 11; l++) {
   						c4 = c4 - bi4.getNext();
   					}
   					Binom bi5 = new Binom(n, pdash3 / (1 - pdash3 * 2 - pdash4*2));
   					pemptyn5 = bi5.getNext();
   					c3 = 1 - pemptyn5;
   					for (int l = 1; l < 11; l++) {
   						c5 = c5 - bi5.getNext();
   					} 
                   }
				}

				// pemptyn5 = Math.pow(n, pdash4/(1-pdash3*3-pdash4*2))
				double maxA = pemptyn1
						* (c2 + pemptyn2
								* (c3 + pemptyn3
										* (c4 + pemptyn4
												* (c5 + pemptyn5 * pmorek))));
				System.out.println(j);
				System.out.println(pemptyn1);
				System.out.println(c2 + " " +pemptyn2);
				System.out.println(c3 + " " +pemptyn3);
				System.out.println(c4 + " " +pemptyn4);
				System.out.println(c5 + " " +pemptyn5);
				System.out.println(i + " " +maxA);
				if (maxA > max) {
					max = maxA;
				}
			}

			// double c4 = 1;
			// Binom bi4 = new Binom(n,pdash4/(1-pdash4));
			// for (int l = 0; l < 11; l++){
			// c4 = c4 - bi4.getNext();
			// }
			// double c34 = 1;
			// Binom bi34 = new Binom(n,pdash4/(1-pdash3));
			// for (int l = 0; l < 11; l++){
			// c34 = c34 - bi34.getNext();
			// }
			// double c4 = 1;
			// Binom bi4 = new Binom(n,pdash4/(1-pdash4));
			// for (int l = 0; l < 11; l++){
			// c4 = c4 - bi4.getNext();
			// }

			// if (pmorek*pempty > max){
			// max = pmorek*pempty;
			// }
		}

		return max;
	}

	public static double imdht(int n, int b) {
		double max = 0;
		double p = 0.5;
		double[] k = new double[128];
		k[1] = 128;
		k[2] = 68;
		k[3] = 34;
		k[4] = 16;
		for (int i = 5; i < k.length; i++) {
			k[i] = 8;
		}
		for (int i = 1; i <= b; i++) {
			double pempty = Math.pow(1 - p, n);
			double pmorek = 0;
			double q = 0.5 * p;
			for (int j = i + 1; j < b; j++) {
				double c = 1;
				Binom bi = new Binom(n, q / (1 - p));
				for (int l = 0; l < k[j] + 1; l++) {
					c = c - bi.getNext();
				}
				q = q * 0.5;
				pmorek = pmorek + c;
			}
			p = p * 0.5;
			if (pmorek * pempty > max) {
				max = pmorek * pempty;
			}
		}

		return max;
	}

}
