package ch.cern.atlas.apvs.client.traceplot;

import java.util.ArrayList;
import java.util.List;

public class LabelMaker {

	public static List<Double> getValueAxisLabels(double max, double min,
			double valueInterval, int noOfPeriods) {
		List<Double> valueAxisLabels = new ArrayList<Double>();
		max = getMaxValueForEvenDist(max, min, noOfPeriods);
		// FIXME 
//		min = -3.0;
		if (valueInterval <= 0) {
			valueInterval = (max - min) / noOfPeriods;
		}
		System.err.println("LM: "+max+" "+min+" "+valueInterval);

		double d = min;

		while (d <= max) {
			valueAxisLabels.add(d);
			d = d + valueInterval;
		}

		System.err.println("# of Double labels "+valueAxisLabels.size());
		return valueAxisLabels;
	}

	public static List<Long> getValueAxisLabels(long max, long min,
			long valueInterval, int noOfPeriods) {
		List<Long> valueAxisLabels = new ArrayList<Long>();
		// FIXME
		// max = getMaxValueForEvenDist(max, min, noOfPeriods);
		if (valueInterval <= 0) {
			long delta = max - min;
			valueInterval = delta / noOfPeriods;
			
			if (valueInterval <= 0) {
				valueInterval = 1;
			}
		}

		long i = min;
		while (i <= max) {
			valueAxisLabels.add(i);
			i = i + valueInterval;
		}
		
		System.err.println("# of Long labels "+valueAxisLabels.size());
		for (Long x : valueAxisLabels) {
			System.err.println("   "+x);
		}
		return valueAxisLabels;
	}

	private static double getMaxValueForEvenDist(double max, double min,
			double interval) {
		double maxValue = max;
		if (Math.abs(min) > maxValue) {
			maxValue = Math.abs(min);
		}
		maxValue = Math.ceil(maxValue);
		if (maxValue < 1) {
			return 1;
		} else if (maxValue < 5) {
			return 5;
		} else if (maxValue < 10) {
			return 10;
		}
		double returnMax = 0;
		double quo = maxValue / interval;
		double mod = quo;
		int i = 10;
		while (true) {
			mod = (int) mod / i;
			if (mod == 0) {
				break;
			} else {
				i = i * 10;
			}
		}
		i = i / 10;
		quo = quo / i;
		quo = Math.ceil(quo);
		quo = quo * i;
		returnMax = quo * interval;
		return returnMax;

	}

}
