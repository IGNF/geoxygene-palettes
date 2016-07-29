package fr.ign.cogit.extrapalettor.util;

import java.awt.color.ColorSpace;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import fr.ign.cogit.geoxygene.semio.color.CIEXYZColorSpace;

public class ColorStats {

  public void sRGB_to_CIELAB_profile() throws FileNotFoundException {
    float[] c = new float[3];
    ColorSpace cslab_AWT = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
    ColorSpace cslab_GEOX = new CIEXYZColorSpace();
    StringBuilder sb_AWT = new StringBuilder();
    StringBuilder sb_Geox = new StringBuilder();
    StringBuilder sb_errors = new StringBuilder();
    StringBuilder sb_errors_bijection_awt = new StringBuilder();
    StringBuilder sb_errors_bijection_geox = new StringBuilder();
    StringBuilder colors = new StringBuilder();
    colors.append("c\n");
    sb_Geox.append("l,a,b\n");
    sb_AWT.append("l,a,b\n");
    sb_errors.append("l,a,b,e \n");
    sb_errors_bijection_awt.append("l,a,b,e \n");
    sb_errors_bijection_geox.append("l,a,b,e \n");

    float S = 0.03f;
    for (float i = 0f; i <= 1.0001f; i += S) {
      for (float j = 0f; j <= 1.0001f; j += S) {
        for (float k = 0f; k <= 1.0001f; k += S) {
          c[0] = i;
          c[1] = j;
          c[2] = k;
          float[] lab_AWT = cslab_AWT.fromRGB(c);
          float[] rbg_AWT = cslab_AWT.toRGB(lab_AWT);

          lab_AWT[0] = lab_AWT[0] * 100f;
          lab_AWT[1] = lab_AWT[1] * 100f;
          lab_AWT[2] = lab_AWT[2] * 100f;

          float[] lab_GEOX = cslab_GEOX.fromRGB(c);
          float[] rbg_GEOX = cslab_GEOX.toRGB(lab_GEOX);
          lab_GEOX[0] = lab_GEOX[0] * 100f;
          lab_GEOX[1] = lab_GEOX[1] * 100f;
          lab_GEOX[2] = lab_GEOX[2] * 100f;

          double e = Math.sqrt((lab_AWT[0] - lab_GEOX[0])
              * (lab_AWT[0] - lab_GEOX[0]) + (lab_AWT[1] - lab_GEOX[1])
              * (lab_AWT[1] - lab_GEOX[1]) + (lab_AWT[2] - lab_GEOX[2])
              * (lab_AWT[2] - lab_GEOX[2]));

          // THe edit distance is the number of HEx colors between A and B, i.e
          // the manhattan distance
          int[] a = new int[] { (int) (c[0] * 255), (int) (c[1] * 255),
              (int) (c[2] * 255) };

          int[] b = new int[] { (int) (rbg_AWT[0] * 255),
              (int) (rbg_AWT[1] * 255), (int) (rbg_AWT[2] * 255) };
          double e_bijectiontranform_AWT = Math.abs(a[0] - b[0])
              + Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2]);

          b = new int[] { (int) (rbg_GEOX[0] * 255), (int) (rbg_GEOX[1] * 255),
              (int) (rbg_GEOX[2] * 255) };
          double e_bijectiontranform_GEOX = Math.abs(a[0] - b[0])
              + Math.abs(a[1] - b[1]) + Math.abs(a[2] - b[2]);

          String hex = String.format("#%02x%02x%02x", (int) (c[0] * 255),
              (int) (c[1] * 255), (int) (c[2] * 255));
          colors.append("\"" + hex + "\"\n");
          sb_Geox.append(lab_GEOX[0] + "," + lab_GEOX[1] + "," + lab_GEOX[2]
              + "\n");
          sb_AWT
              .append(lab_AWT[0] + "," + lab_AWT[1] + "," + lab_AWT[2] + "\n");
          sb_errors.append(lab_AWT[0] + "," + lab_AWT[1] + "," + lab_AWT[2]
              + "," + e + "\n");

          sb_errors_bijection_awt.append(lab_AWT[0] + "," + lab_AWT[1] + ","
              + lab_AWT[2] + "," + e_bijectiontranform_AWT + "\n");
          sb_errors_bijection_geox.append(lab_GEOX[0] + "," + lab_GEOX[1] + ","
              + lab_GEOX[2] + "," + e_bijectiontranform_GEOX + "\n");

        }
      }
    }
    // colors.append(")");
    FileWriter fw;
    try {
      fw = new FileWriter(new File("/tmp/sRGB_to_Lab_GEOX.data"));
      fw.write(sb_Geox.toString());
      fw.flush();

      fw = new FileWriter(new File("/tmp/sRGB_to_Lab_AWT.data"));
      fw.write(sb_AWT.toString());
      fw.flush();

      fw = new FileWriter(new File("/tmp/colors.data"));
      fw.write(colors.toString());
      fw.flush();

      fw = new FileWriter(new File("/tmp/AWT_GEOX_ERRORS.data"));
      fw.write(sb_errors.toString());
      fw.flush();

      fw = new FileWriter(new File("/tmp/AWT_BIJ_ERRORS.data"));
      fw.write(sb_errors_bijection_awt.toString());
      fw.flush();

      fw = new FileWriter(new File("/tmp/GEOX_BIJ_ERRORS.data"));
      fw.write(sb_errors_bijection_geox.toString());
      fw.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
