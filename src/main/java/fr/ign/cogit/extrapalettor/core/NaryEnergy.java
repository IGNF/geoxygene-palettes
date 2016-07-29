package fr.ign.cogit.extrapalettor.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.ign.cogit.extrapalettor.core.constraints.GraphConstraint;
import fr.ign.rjmcmc.energy.CollectionEnergy;

public class NaryEnergy implements CollectionEnergy<ColorPoint> {

	public Map<Set<Integer>, List<GraphConstraint<ColorPoint>>> constraints;

	public NaryEnergy() {
		this.constraints = new HashMap<>();
	}

	public void addConstraint(GraphConstraint<ColorPoint> c) {
		List<Integer> pts = c.getVerticesIndexes();
		Set<Integer> ptsset = new HashSet<>(pts);
		if (this.constraints.get(ptsset) == null) {
			this.constraints.put(ptsset, new ArrayList<GraphConstraint<ColorPoint>>(1));
		}
		this.constraints.get(ptsset).add(c);
	}

	@Override
	public double getValue(Collection<ColorPoint> t) {
		Set<Integer> ptsset = new HashSet<>();
		Map<Integer, ColorPoint> mapIndex = new HashMap<>(); // TODO Do
																// something
																// less
																// ugly...
		for (ColorPoint c : t) {
			ptsset.add(c.getId());
			mapIndex.put(c.getId(), c);
		}
		double e = 0.;
		for (Entry<Set<Integer>, List<GraphConstraint<ColorPoint>>> en : constraints.entrySet()) {
			if (ptsset.containsAll(en.getKey())) {
				ColorPoint[] pts = new ColorPoint[en.getKey().size()];
				int i = 0;
				for (Integer id : en.getKey()) {
					pts[i++] = mapIndex.get(id);
				}
				for (GraphConstraint<ColorPoint> c : en.getValue()) {
					e += c.evaluate(pts);
				}
			}
		}
		return e;
	}

}
