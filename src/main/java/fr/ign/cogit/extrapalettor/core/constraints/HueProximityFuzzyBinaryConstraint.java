package fr.ign.cogit.extrapalettor.core.constraints;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.extrapalettor.core.ColorPoint;
import fr.ign.cogit.geoxygene.semio.color.CIELchColorSpace;
import fr.ign.cogit.geoxygene.semio.color.ColorDifference;

public class HueProximityFuzzyBinaryConstraint implements GraphConstraint<ColorPoint> {

	private Integer c1Id;
	private Integer c2Id;
	private double Hthreshold;
	private CIELchColorSpace cslch = new CIELchColorSpace(false);

	public HueProximityFuzzyBinaryConstraint(ColorPoint _p1, ColorPoint _p2, double _Hthreshold) {
		this.c1Id = _p1.getId();
		this.c2Id = _p2.getId();
		this.Hthreshold = _Hthreshold;
	}

	@Override
	public double evaluate(ColorPoint... c) {
		assert (c.length == 2);
		assert (c[0].getId() == c1Id && c[1].getId() == c2Id || c[0].getId() == c2Id && c[1].getId() == c1Id); // Maybe
		double d = ColorDifference.deltaH(cslch.toCIELab(c[0].getComponents()), cslch.toCIELab(c[1].getComponents()),
				ColorDifference.CIE76);
		return Math.abs(d) <= Hthreshold ? 1. : 0.;
	}

	@Override
	public List<Integer> getVerticesIndexes() {
		ArrayList<Integer> vid = new ArrayList<>();
		vid.add(c1Id);
		vid.add(c2Id);
		return vid;
	}
}
