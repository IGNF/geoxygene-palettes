package fr.ign.cogit.extrapalettor.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.extrapalettor.core.constraints.GraphConstraint;
import fr.ign.rjmcmc.energy.BinaryEnergy;

public class BinaryFuzzyConstraintsEnergy implements BinaryEnergy<ColorPoint, ColorPoint> {

	static final double alpha = 100d;

	public Map<Set<Integer>, List<GraphConstraint<ColorPoint>>> constraints;

	public BinaryFuzzyConstraintsEnergy() {
		this.constraints = new HashMap<>();
	}

	@Override
	public double getValue(ColorPoint c1, ColorPoint c2) {
		Set<Integer> ptsset = new HashSet<>();
		ptsset.add(c1.getId());
		ptsset.add(c2.getId());
		if (constraints.get(ptsset) == null) {
			return 0;
		}
		double e = 0;
		for (GraphConstraint<ColorPoint> c : constraints.get(ptsset)) {
			e += alpha * (1. - c.evaluate(c1, c2));
		}
		return e;
	}

	// TODO change to only accept Binary Energies
	public void addConstraint(GraphConstraint<ColorPoint> c) {
		List<Integer> pts = c.getVerticesIndexes();
		Set<Integer> ptsset = new HashSet<>(pts);
		if (this.constraints.get(ptsset) == null) {
			this.constraints.put(ptsset, new ArrayList<GraphConstraint<ColorPoint>>(1));
		}
		this.constraints.get(ptsset).add(c);
	}
}
