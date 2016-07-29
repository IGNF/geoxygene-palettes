package fr.ign.cogit.extrapalettor.core;

import fr.ign.rjmcmc.kernel.Transform;

public class ColorPointMovement implements Transform {

	private double[] dimRanges;

	public ColorPointMovement(double[] ranges) {
		this.dimRanges = ranges.clone();
	}

	public static final int DIM = 7;

	@Override
	public double apply(boolean direct, double[] in, double[] out) {
		// in should contain the 3 color coordinates, 1 ID and 3 values in [0;1]
		assert (in.length == DIM);
		double dL = (in[4] - 0.5) * dimRanges[0];
		double da = (in[5] - 0.5) * dimRanges[1];
		double db = (in[6] - 0.5) * dimRanges[2];

		out[0] = in[0] + dL;
		out[1] = in[1] + da;
		out[2] = in[2] + db;
		out[3] = in[3];
		out[4] = 1d - in[4];
		out[5] = 1d - in[5];
		out[6] = 1d - in[6];
		return 1d;
	}

	@Override
	public int dimension() {
		return DIM;
	}
}
