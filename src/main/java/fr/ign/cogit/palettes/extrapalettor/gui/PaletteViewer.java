package fr.ign.cogit.palettes.extrapalettor.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PaletteViewer extends JFrame {

  private static final long serialVersionUID = 1L;
  PalettePane palettespane;

  public PaletteViewer() {
    this.palettespane = new PalettePane();
    this.add(palettespane);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public void addPalette(Color[] palette) {
    this.palettespane.addPalette(palette);
  }

  private class PalettePane extends JPanel {

    List<Color[]> palettes = new ArrayList<Color[]>();

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (this.palettes.isEmpty())
        return;
      int cellHeight = this.getHeight() / palettes.size();
      for (int i = 0; i < this.palettes.size(); i++) {
        int cellwidth = this.getWidth() / palettes.get(i).length;
        for (int j = 0; j < palettes.get(i).length; j++) {
          g.setColor(Color.black);
          g.drawRect(j * cellwidth, i * cellHeight, cellwidth, cellHeight);
          g.setColor(palettes.get(i)[j]);
          g.fillRect(j * cellwidth, i * cellHeight, cellwidth, cellHeight);
        }
      }
    }

    public void addPalette(Color[] palette) {
      this.palettes.add(palette);
    }

  }

  public static void main(String[] argv) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        PaletteViewer p = new PaletteViewer();
        p.setSize(500, 500);

        p.addPalette(new Color[] { Color.black, Color.white, Color.gray });
        p.addPalette(new Color[] { Color.yellow, Color.magenta, Color.cyan });
        p.addPalette(new Color[] { Color.black, Color.white, Color.gray });
        p.setVisible(true);
      }
    });
  }

}
