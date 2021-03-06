package fr.ign.cogit.extrapalettor.core;

import fr.ign.rjmcmc.kernel.Transform;

public class ColourPointChromaShift implements Transform {

	private double dimRange;
	public static final int DIM = 5;

	public ColourPointChromaShift(double range) {
		this.dimRange = range;
	}

	@Override
	public double apply(boolean direct, double[] in, double[] out) {
		// in = 4
		double L = in[0];
		double c = in[1];
		double h = in[2];

		double shift = in[4];

		out[0] = L;
		out[1] = (shift - 0.5) * dimRange + c;
		out[2] = h;
		out[3] = in[3];// 3 is the colourpoint ID
		out[4] = 1d - shift;
		return 1d; // TODO check if this is the probability that the
					// modification is valid
	}

	@Override
	public int dimension() {
		return DIM;
	}

}
