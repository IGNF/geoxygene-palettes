package fr.ign.cogit.extrapalettor.core;

import fr.ign.mpp.kernel.ObjectBuilder;

public class ColorPointBuilder implements ObjectBuilder<ColorPoint> {

	@Override
	public int size() {
		return 4;
	}

	/**
	 * Fill dblcomponents with the components of cp
	 */
	@Override
	public void setCoordinates(ColorPoint cp, double[] dblproperties) {
		assert (cp.size() == dblproperties.length && dblproperties.length == this.size());
		float[] lch = cp.getComponents();
		for (int i = 0; i < lch.length; i++) {
			dblproperties[i] = lch[i];
		}
		dblproperties[cp.size() - 1] = cp.getId();
	}

	@Override
	public ColorPoint build(double[] pointProperties) {
		assert (pointProperties.length == this.size());
		ColorPoint cp = new ColorPoint((int) pointProperties[pointProperties.length - 1]);
		for (int i = 0; i < cp.size(); i++) {
			assert (Float.MAX_VALUE > pointProperties[i]);
			cp.set((float) pointProperties[i], i);
		}
		return cp;
	}

}
