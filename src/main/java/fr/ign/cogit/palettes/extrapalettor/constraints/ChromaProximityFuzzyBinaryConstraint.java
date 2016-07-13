package fr.ign.cogit.palettes.extrapalettor.constraints;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.palettes.extrapalettor.ColourPointLCH;

public class ChromaProximityFuzzyBinaryConstraint implements
    GraphConstraint<ColourPointLCH> {

  private Integer c1Id;
  private Integer c2Id;
  private double Lthreshold;

  public ChromaProximityFuzzyBinaryConstraint(ColourPointLCH _p1,
      ColourPointLCH _p2, double _Lthreshold) {
    this.c1Id = _p1.getId();
    this.c2Id = _p2.getId();
    this.Lthreshold = _Lthreshold;
  }

  @Override
  public double evaluate(ColourPointLCH... c) {
    assert (c.length == 2);
    assert (c[0].getId() == c1Id && c[1].getId() == c2Id || c[0].getId() == c2Id
        && c[1].getId() == c1Id); // Maybe remove this assertion
    float[] lchA = c[0].getComponents();
    float[] lchB = c[1].getComponents();
    double d = Math.abs(lchA[1]-lchB[1]);
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
