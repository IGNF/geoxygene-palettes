package fr.ign.cogit.palettes.extrapalettor.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import fr.ign.cogit.palettes.extrapalettor.ColourPointLCH;

public class ColourSequenceConstraint implements GraphConstraint<ColourPointLCH> {

  Integer[] sequence;

  /**
   * Assume the first 2 points to be the extremities of the sequence. All other
   * points should be interpolated between these 2 extremities.
   */

  public ColourSequenceConstraint(List<ColourPointLCH> seq) {
    assert (seq.size() > 2);
    this.sequence = new Integer[seq.size()];
    for (int i = 0; i < seq.size(); i++) {
      sequence[i] = seq.get(i).getId();
    }
  }

  @Override
  public double evaluate(ColourPointLCH... vertices) {
    assert (vertices.length == this.sequence.length);

    List<ColourPointLCH> cPoints = Arrays.asList(vertices);
    cPoints.sort(new Comparator<ColourPointLCH>() {
      @Override
      public int compare(ColourPointLCH o1, ColourPointLCH o2) {
        return Integer.compare(o1.getId(), o2.getId());
      }
    });

    assert (cPoints.get(0).getId() == sequence[0] && cPoints.get(1).getId() == sequence[1]);

    ColourPointLCH start = cPoints.get(0);
    float[] lchS = start.getComponents();
    ColourPointLCH end = cPoints.get(cPoints.size() - 1);
    float[] lchE = end.getComponents();
    float[] v = new float[] { lchE[0] - lchS[0], lchE[1] - lchS[1], lchE[2] - lchS[2] };
    double e = 0.;
    int i = 0;
    for (ColourPointLCH c : cPoints) {
      float u = i / (cPoints.size() - 1f); // Goal curvilinear absisse for c.
      float[] d = new float[] { v[0] * u, v[1] * u, v[2] * u };
      float[] goal = new float[] { lchS[0] + d[0], lchS[1] + d[1], lchS[2] + d[2] };
      double error = this.deltaE(c.getComponents(), goal);
      e += error * error;
      i++;
    }
    return Math.sqrt(e);
  }

  @Override
  public List<Integer> getVerticesIndexes() {
    ArrayList<Integer> vid = new ArrayList<>();
    for (Integer i : this.sequence) {
      vid.add(i);
    }
    return vid;
  }

  private double deltaE(float[] a, float[] b) {
    return Math.sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]) + (a[2] - b[2]) * (a[2] - b[2]));

  }

}
