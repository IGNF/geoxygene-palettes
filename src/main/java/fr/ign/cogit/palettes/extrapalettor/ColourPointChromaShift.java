package fr.ign.cogit.palettes.extrapalettor;

import fr.ign.rjmcmc.kernel.Transform;

public class ColourPointChromaShift implements Transform {

	double shiftrange;

	public ColourPointChromaShift(double range) {
		this.shiftrange = range;
	}

	@Override
	public double apply(boolean direct, double[] in, double[] out) {
		// in = 4
		double L = in[0];
		double c = in[1];
		double h = in[2];

		double shift = in[4];

		out[0] = L;
		out[1] = (shift - 0.5) * shiftrange + c;
		out[2] = h;
		out[3] = in[3];//3 is the colourpoint ID
		out[4] = 1 - shift;
		return 1.; // TODO check if this is the probability that the
					// modification is valid
	}

	@Override
	public int dimension() {
		return 5;
	}

}
