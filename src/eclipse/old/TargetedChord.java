package eclipse.old;

import java.math.BigDecimal;

public class TargetedChord {
	
	public static BigDecimal getAttackEfficiency(int n){
		BigDecimal n1 = new BigDecimal((double)n);
		BigDecimal m = new BigDecimal((Math.pow(2, 128)));
		BigDecimal p = n1.divide(m);
		//System.out.println(p);
		BigDecimal[] x = new BigDecimal[128];
		x[0] = p;
		for (int i = 1; i < x.length; i++){
			x[i] = p;
			for (int j = 0; j < i; j++){
				x[i] = x[i].add(p.multiply(x[j]));
			}
		}
		//System.out.println(x[127]);
		return (n1.subtract(x[127]).divide(n1));
	}

}
