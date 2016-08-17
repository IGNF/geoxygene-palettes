package fr.ign.cogit.extrapalettor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.ign.cogit.extrapalettor.core.ColorPoint;
import fr.ign.cogit.extrapalettor.util.ColorUtil;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.parameters.Parameters;
import fr.ign.simulatedannealing.visitor.OutputStreamVisitor;
import fr.ign.simulatedannealing.visitor.Visitor;

public class ColorSequenceBuilder {

  private static final Comparator<ColorPoint> ColorRampSorter = new Comparator<ColorPoint>() {
    @Override
    public int compare(ColorPoint a, ColorPoint b) {
      return Integer.compare(a.getId(), b.getId());
    }
  };

  ColorSequence s;

  public ColorSequence buildColorSequence(int ncolors) {

    Parameters p = new Parameters();
    p.set("nbColours", ncolors);

    Extrapalettor palettor = new Extrapalettor(p);

    Visitor vConsole = new OutputStreamVisitor<>(System.out);
    palettor.addVisitor(vConsole);

    s = new ColorSequence(ncolors);

    Thread t = new Thread(palettor);
    t.run();
    try {
      t.join();
      GraphConfiguration<ColorPoint> conf = palettor.getConfiguration();

      List<ColorPoint> palette = new ArrayList<>();
      for (ColorPoint c : conf) {
        palette.add(c);
      }

      palette.sort(ColorRampSorter);

      int i = 0;
      for (ColorPoint c : palette) {
        float[] rgb = c.getColorSpace().toRGB(c.getComponents());
        rgb[0] = ColorUtil.rgbClamp(rgb[0]) * 255;
        rgb[1] = ColorUtil.rgbClamp(rgb[1]) * 255;
        rgb[2] = ColorUtil.rgbClamp(rgb[2]) * 255;
        this.s.colors[i] = rgb;
        i++;
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
    return s;
  }

}
