package fr.ign.cogit.palettes.extrapalettor.util.colorimetry;

public class ColorUtil {

  public static String rgbToHex(int r, int g, int b) {
    String sr = Integer.toHexString(r);
    String sg = Integer.toHexString(g);
    String sb = Integer.toHexString(b);
    if (sr.length() < 2)
      sr = "0" + sr;
    if (sg.length() < 2)
      sg = "0" + sg;
    if (sb.length() < 2)
      sb = "0" + sb;
    return "#" + sr + sg + sb;
  }

  public static float rgbClamp(float c) {
    return c > 1f ? 1f : c < 0f ? 0f : c;
  }

}
