package fr.ign.cogit.extrapalettor.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GUIPalettesViewer extends JFrame {

  private static final long serialVersionUID = 1L;

  public GUIPalettesViewer() {
  }

  public static void main(String[] argv) {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        GUIPalettesViewer p = new GUIPalettesViewer();
        p.setSize(500, 500);
        p.setVisible(true);
      }
    });
  }

}
