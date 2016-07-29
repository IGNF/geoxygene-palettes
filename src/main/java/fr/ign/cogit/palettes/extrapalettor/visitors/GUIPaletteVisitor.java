package fr.ign.cogit.palettes.extrapalettor.visitors;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.ign.cogit.palettes.extrapalettor.ColorPoint;
import fr.ign.cogit.palettes.extrapalettor.util.colorimetry.ColorUtil;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;
import fr.ign.simulatedannealing.visitor.Visitor;

public class GUIPaletteVisitor extends JPanel
    implements Visitor<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> {

  private static final long serialVersionUID = 5216229587205228652L;

  private final List<List<ColorPoint>> palettes = new LinkedList<>();
  private final Comparator<ColorPoint> paletteColorsComp;
  private int nVisit = 0;

  private int nIterTrigger = -1;

  private final boolean OUTLINE_OUT_OF_GAMUT_SRGB;
  private final boolean PLOT_ITERATION_NUMBER;

  public GUIPaletteVisitor(boolean show_out_of_gamut, boolean plot_niter) {
    this.paletteColorsComp = new Comparator<ColorPoint>() {
      @Override
      public int compare(ColorPoint a, ColorPoint b) {
        return Integer.compare(a.getId(), b.getId());
      }
    };
    this.OUTLINE_OUT_OF_GAMUT_SRGB = show_out_of_gamut;
    this.PLOT_ITERATION_NUMBER = plot_niter;
  }

  @Override
  public void begin(GraphConfiguration<ColorPoint> conf,
      Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> sampler, Temperature temp) {
    // TODO Auto-generated method stub

  }

  @Override
  public void end(GraphConfiguration<ColorPoint> conf,
      Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> sampler, Temperature temp) {
    visit(conf,sampler,temp);
  }

  @Override
  public void init(int arg0, int arg1) {
    nIterTrigger = arg0;

  }

  @Override
  public void visit(GraphConfiguration<ColorPoint> conf,
      Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> sampler, Temperature temp) {
    if (nVisit % nIterTrigger != 0) {
      nVisit++;
      return;
    }

    List<ColorPoint> palette = new ArrayList<>();
    for (ColorPoint c : conf) {
      palette.add(c);
    }
    palette.sort(this.paletteColorsComp);
    palettes.add(palette);
    nVisit++;
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });

  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (this.palettes.isEmpty())
      return;
    int cellHeight = this.getHeight() / palettes.size();
    for (int i = 0; i < this.palettes.size(); i++) {
      int cellwidth = this.getWidth() / palettes.get(i).size();
      List<ColorPoint> palette = this.palettes.get(i);
      for (int j = 0; j < palette.size(); j++) {
        ColorPoint cp = palette.get(j);
        float[] rgb = cp.getColorSpace().toRGB(cp.getComponents());
        Color cRGB = new Color(ColorUtil.rgbClamp(rgb[0]), ColorUtil.rgbClamp(rgb[1]), ColorUtil.rgbClamp(rgb[2]));
        g.setColor(cRGB);
        g.fillRect(j * cellwidth, i * cellHeight, cellwidth, cellHeight);
        if (OUTLINE_OUT_OF_GAMUT_SRGB && outOfsRGBGamut(rgb)) {
          g.setColor(Color.RED);
          g.drawRect(j * cellwidth, i * cellHeight, cellwidth, cellHeight);
        }
      }
      if (PLOT_ITERATION_NUMBER) {
        g.setColor(Color.black);
        g.drawString(Integer.toString(i * this.nIterTrigger), 5, (int) (cellHeight * i + cellHeight / 2));
      }
    }
  }

  private boolean outOfsRGBGamut(float[] rgb) {
    return (rgb[0] < 0f || rgb[0] > 1f || rgb[1] < 0f || rgb[1] > 1f || rgb[2] < 0f || rgb[2] > 1f);
  }
}
