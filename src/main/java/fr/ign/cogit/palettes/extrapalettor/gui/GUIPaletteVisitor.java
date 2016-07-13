package fr.ign.cogit.palettes.extrapalettor.gui;

import javax.swing.JPanel;

import fr.ign.cogit.palettes.extrapalettor.ColourPointLCH;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;
import fr.ign.simulatedannealing.visitor.Visitor;

public class GUIPaletteVisitor extends JPanel
    implements Visitor<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> {

  private static final long serialVersionUID = 5216229587205228652L;

  @Override
  public void begin(GraphConfiguration<ColourPointLCH> arg0,
      Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> arg1, Temperature arg2) {
    // TODO Auto-generated method stub

  }

  @Override
  public void end(GraphConfiguration<ColourPointLCH> arg0,
      Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> arg1, Temperature arg2) {
    // TODO Auto-generated method stub
  }

  @Override
  public void init(int arg0, int arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void visit(GraphConfiguration<ColourPointLCH> arg0,
      Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> arg1, Temperature arg2) {
    // TODO Auto-generated method stub

  }

}
