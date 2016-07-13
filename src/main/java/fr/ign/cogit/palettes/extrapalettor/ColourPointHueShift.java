package fr.ign.cogit.palettes.extrapalettor;

import fr.ign.rjmcmc.kernel.Transform;

public class ColourPointHueShift implements Transform {

  double shiftrange;

  public ColourPointHueShift(double range) {
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
    out[1] = c;
    out[2] = (shift - 0.5) * shiftrange + h;
    ;
    out[3] = in[3];
    out[4] = 1 - shift;
    return 1.; // TODO check if this is the probability that the
    // modification is valid
  }

  @Override
  public int dimension() {
    return 5;
  }

}
