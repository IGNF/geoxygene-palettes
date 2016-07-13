package fr.ign.cogit.palettes.extrapalettor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.ign.cogit.palettes.extrapalettor.constraints.GraphConstraint;
import fr.ign.rjmcmc.energy.CollectionEnergy;

public class NaryEnergy implements CollectionEnergy<ColourPointLCH> {

  public Map<Set<Integer>, List<GraphConstraint<ColourPointLCH>>> constraints;

  public NaryEnergy() {
    this.constraints = new HashMap<>();
  }

  public void addConstraint(GraphConstraint<ColourPointLCH> c) {
    List<Integer> pts = c.getVerticesIndexes();
    Set<Integer> ptsset = new HashSet<>(pts);
    if (this.constraints.get(ptsset) == null) {
      this.constraints.put(ptsset, new ArrayList<GraphConstraint<ColourPointLCH>>(1));
    }
    this.constraints.get(ptsset).add(c);
  }

  @Override
  public double getValue(Collection<ColourPointLCH> t) {
    Set<Integer> ptsset = new HashSet<>();
    Map<Integer, ColourPointLCH> mapIndex = new HashMap<>(); // TODO Do
                                                             // something less
                                                             // ugly...
    for (ColourPointLCH c : t) {
      ptsset.add(c.getId());
      mapIndex.put(c.getId(), c);
    }
    double e = 0.;
    for (Entry<Set<Integer>, List<GraphConstraint<ColourPointLCH>>> en : constraints.entrySet()) {
      if (ptsset.containsAll(en.getKey())) {
        ColourPointLCH[] pts = new ColourPointLCH[en.getKey().size()];
        int i = 0;
        for (Integer id : en.getKey()) {
          pts[i++] = mapIndex.get(id);
        }
        for (GraphConstraint<ColourPointLCH> c : en.getValue()) {
          e += c.evaluate(pts);
        }
      }
    }
    return e;
  }

}
