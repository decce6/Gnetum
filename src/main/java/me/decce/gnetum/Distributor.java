package me.decce.gnetum;

public class Distributor {
	private static double[] elementTimes;

	private static void update() {
		var newSize = Gnetum.config.map.size();
		if (elementTimes == null || elementTimes.length != newSize) {
			elementTimes = new double[newSize];
		}
	}
	public static void resolve() {
		update();
		double totalTime = 0.0d;
		var map = Gnetum.config.map;
		int elementIndex = 0;
		int count = Gnetum.config.numberOfPasses;
		for (var element : map.values()) {
			var times = element.time;
			elementTimes[elementIndex] = 0.0d;
			for (int i = 0; i < times.length; i++) {
				elementTimes[elementIndex] += times[i];
			}
			totalTime += elementTimes[elementIndex];
			elementIndex++;
		}
		double target = totalTime / count;
		resolve(target);
	}

	private static void resolve(double target) {
		double time = 0.0d;
		int i = 0;
		int pass = 1;
		for (var element : Gnetum.config.map.values()) {
			time += elementTimes[i++];
			if (time >= target && pass < Gnetum.config.numberOfPasses) {
				time %= target;
				pass++;
			}
			element.pass = pass;
		}
	}
}
