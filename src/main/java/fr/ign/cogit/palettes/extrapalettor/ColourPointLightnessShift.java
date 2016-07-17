package fr.ign.cogit.palettes.extrapalettor;

import fr.ign.rjmcmc.kernel.Transform;

public class ColourPointLightnessShift implements Transform {

	private double dimRange;
	public static final int DIM = 5;

	public ColourPointLightnessShift(double range) {
		this.dimRange = range;
	}

	@Override
	public double apply(boolean direct, double[] in, double[] out) {
		double L = in[0];
		double c = in[1];
		double h = in[2];
		double shift = in[4];
		out[0] = (shift - 0.5) *dimRange + L;
		out[1] = c;
		out[2] = h;
		out[3] = in[3];
		out[4] = 1d - shift;
		return 1d; 
		// TODO check if this is the probability that the
					// modification is valid
	}

	@Override
	public int dimension() {
		return DIM;
	}

}
