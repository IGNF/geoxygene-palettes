package fr.ign.cogit.extrapalettor.visitors;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ign.cogit.geoxygene.semio.color.CIELchColorSpace;
import fr.ign.cogit.extrapalettor.core.ColorPoint;
import fr.ign.cogit.extrapalettor.util.ColorUtil;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;
import fr.ign.simulatedannealing.visitor.Visitor;

public class PaletteVisitor implements Visitor<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> {

  @Override
  public void begin(GraphConfiguration<ColorPoint> arg0,
      Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> arg1, Temperature arg2) {
    // TODO Auto-generated method stub

  }

  /**
   * Log to file.
   */
  @Override
  public void end(GraphConfiguration<ColorPoint> conf,
      Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> arg1, Temperature arg2) {

    // JSON Logging
    // TODO : make something better...like a decent JSON logger/exporter.
    GraphConfiguration<ColorPoint> fc = conf;
    FileWriter fw;
    StringBuilder sout = new StringBuilder(100);
    sout.append("pdata='[");
    try {
      fw = new FileWriter("./www/data/palette.json");
      int i = 0;
      for (ColorPoint c : fc) {
        float[] lch = c.getComponents();
        float[] rgb = c.getColorSpace().toRGB(lch);
        rgb[0] = ColorUtil.rgbClamp(rgb[0]) * 255;
        rgb[1] = ColorUtil.rgbClamp(rgb[1]) * 255;
        rgb[2] = ColorUtil.rgbClamp(rgb[2]) * 255;
        sout.append(Arrays.toString(rgb) + ((i++ == fc.size() - 1) ? "" : ","));
      }
      sout.append("]'");
      fw.write(sout.toString());
      fw.flush();
      fw.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void init(int arg0, int arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(GraphConfiguration<ColorPoint> arg0,
      Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> arg1, Temperature arg2) {
    // TODO Auto-generated method stub

  }

}
