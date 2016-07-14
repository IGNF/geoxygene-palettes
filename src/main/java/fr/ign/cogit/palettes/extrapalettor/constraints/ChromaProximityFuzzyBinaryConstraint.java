package fr.ign.cogit.palettes.extrapalettor.constraints;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.semio.color.CIELchColorSpace;
import fr.ign.cogit.palettes.extrapalettor.ColorPoint;

public class ChromaProximityFuzzyBinaryConstraint implements GraphConstraint<ColorPoint> {

	private Integer c1Id;
	private Integer c2Id;
	private double Lthreshold;
	private CIELchColorSpace cslch = new CIELchColorSpace(false);
	
	public ChromaProximityFuzzyBinaryConstraint(ColorPoint _p1, ColorPoint _p2, double _Lthreshold) {
		this.c1Id = _p1.getId();
		this.c2Id = _p2.getId();
		this.Lthreshold = _Lthreshold;
	}

	@Override
	public double evaluate(ColorPoint... c) {
		assert (c.length == 2);
		assert (c[0].getId() == c1Id && c[1].getId() == c2Id || c[0].getId() == c2Id && c[1].getId() == c1Id);																							// assertion
		float[] labA = c[0].getComponents();
		float[] labB = c[1].getComponents();
		float[] lchA = cslch.fromCIEXYZ(c[0].getColorSpace().toCIEXYZ(labA));
		float[] lchB = cslch.fromCIEXYZ(c[1].getColorSpace().toCIEXYZ(labB));
		double d = Math.abs(lchA[1] - lchB[1]);
		return d <= Lthreshold ? 1. : 0.;
	}

	@Override
	public List<Integer> getVerticesIndexes() {
		ArrayList<Integer> vid = new ArrayList<>();
		vid.add(c1Id);
		vid.add(c2Id);
		return vid;
	}
}
