package fr.ign.cogit.palettes.extrapalettor.constraints;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.palettes.extrapalettor.ColorPoint;

public class LightnessDistanceFuzzyBinaryConstraint implements GraphConstraint<ColorPoint> {

  private Integer c1Id;
  private Integer c2Id;
  private double Lthreshold;

  public LightnessDistanceFuzzyBinaryConstraint(ColorPoint _p1, ColorPoint _p2, double _Lthreshold) {
    this.c1Id = _p1.getId();
    this.c2Id = _p2.getId();
    this.Lthreshold = _Lthreshold;
  }

  @Override
  public double evaluate(ColorPoint... c) {
    assert (c.length == 2);
    assert (c[0].getId() == c1Id && c[1].getId() == c2Id || c[0].getId() == c2Id && c[1].getId() == c1Id); // Maybe
    float[] labA = c[0].getComponents();
    float[] labB = c[1].getComponents();
    double d = Math.abs(labA[0]-labB[0]);
    return d >= Lthreshold ? 1. : 0.;
  }

  @Override
  public List<Integer> getVerticesIndexes() {
    ArrayList<Integer> vid = new ArrayList<>();
    vid.add(c1Id);
    vid.add(c2Id);
    return vid;
  }

}
