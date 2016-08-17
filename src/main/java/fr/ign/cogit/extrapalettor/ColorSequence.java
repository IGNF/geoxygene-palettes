package fr.ign.cogit.extrapalettor;

import java.rmi.server.UID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColorSequence {
  int ncolors = 1;
  public float[][] colors = new float[1][3];
  String uid = new UID().toString();

  public ColorSequence() {
    //Seems mandatory for jaxb/jersey json...
  }
  
  public ColorSequence(int i) {
    this.ncolors = i;
    this.colors = new float[i][3];
  }
}
