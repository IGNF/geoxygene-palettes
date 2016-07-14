package fr.ign.cogit.palettes.extrapalettor;

import fr.ign.rjmcmc.kernel.Transform;

public class ColourPointBShift implements Transform {

	private double dimRange;
	public static final int DIM = 5;

	public ColourPointBShift(double range) {
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
		out[1] = c;
		out[2] = (shift - 0.5) * dimRange + h;
		;
		out[3] = in[3];
		out[4] = 1d - shift;
		return 1.; // TODO check if this is the probability that the
		// modification is valid
	}

	@Override
	public int dimension() {
		return DIM;
	}

}
