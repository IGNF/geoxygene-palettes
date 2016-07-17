package fr.ign.cogit.palettes.extrapalettor;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.Arrays;
import java.util.List;

import fr.ign.cogit.geoxygene.semio.color.CIELabColorSpace;
import fr.ign.cogit.geoxygene.semio.color.CIELchColorSpace;
import fr.ign.rjmcmc.kernel.SimpleObject;

/**
 * Palette color point in CIE L*c*h* coordinates
 * 
 * @author Bertrand Duménieu
 */
public class ColorPoint implements SimpleObject {

	// ColourPoint properties contain : the 3 point color components in Lch +
	// this colour vertex id in the palette graph
	private final float[] properties;

	// The color space of this ColourPoint
	private final ColorSpace cs = new CIELchColorSpace(false);
	private final int csSize = cs.getNumComponents();

	public ColorPoint(int grphId) {
		this.properties = new float[csSize + 1];
		this.properties[csSize] = grphId;
	}

	public Color getColor() {
		float[] rgb = cs.toRGB(this.getComponents());
		Color c = new Color(rgb[0], rgb[1], rgb[2]);
		return c;
	}

	public ColorSpace getColorSpace() {
		return this.cs;
	}

	public void setComponents(float... components) {
		assert (components.length == csSize);
		for (int i = 0; i < csSize; i++) {
			this.properties[i] = components[i];
		}
	}

	public void set(float prop, int propId) {
		assert (propId >= 0 && this.size() > propId);
		this.properties[propId] = prop;
	}

	@Override
	public Object[] getArray() {
		Double[] comps = new Double[this.properties.length];
		int i = 0;
		for (double d : this.properties) {
			comps[i++] = d;
		}
		return comps;
	}

	@Override
	public void set(List<Double> comps) {
		for (int i = 0; i < this.properties.length; i++) {
			assert (Double.MAX_VALUE > comps.get(i));
			this.properties[i++] = comps.get(i).floatValue();
		}
	}

	@Override
	public int size() {
		return this.properties.length;
	}

	@Override
	public double[] toArray() {
		double[] dcomps = new double[this.properties.length];
		int i = 0;
		for (double c : this.properties) {
			dcomps[i++] = c;
		}
		return dcomps;
	}

	public String toString() {
		return Arrays.toString(this.properties);
	}

	public int getId() {
		return (int) this.properties[this.properties.length - 1];
	}

	public float[] getComponents() {
		return new float[] { (float) this.properties[0], (float) this.properties[1], (float) this.properties[2] };
	}
}
