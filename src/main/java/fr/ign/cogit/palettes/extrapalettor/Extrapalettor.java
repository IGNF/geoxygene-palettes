package fr.ign.cogit.palettes.extrapalettor;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.cogit.geoxygene.semio.color.CIELchColorSpace;
import fr.ign.cogit.palettes.extrapalettor.constraints.ChromaProximityFuzzyBinaryConstraint;
import fr.ign.cogit.palettes.extrapalettor.constraints.ColourSequenceConstraint;
import fr.ign.cogit.palettes.extrapalettor.constraints.HueProximityFuzzyBinaryConstraint;
import fr.ign.cogit.palettes.extrapalettor.constraints.LightnessDistanceFuzzyBinaryConstraint;
import fr.ign.cogit.palettes.extrapalettor.gui.GUIPaletteVisitor;
import fr.ign.cogit.palettes.extrapalettor.gui.PaletteViewer;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.configuration.GraphVertex;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.parameters.Parameter;
import fr.ign.parameters.ParameterComponent;
import fr.ign.parameters.Parameters;
import fr.ign.rjmcmc.acceptance.Acceptance;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.UniformDistribution;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.sampler.GreenSampler;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.SimulatedAnnealing;
import fr.ign.simulatedannealing.endtest.EndTest;
import fr.ign.simulatedannealing.endtest.StabilityEndTest;
import fr.ign.simulatedannealing.schedule.GeometricSchedule;
import fr.ign.simulatedannealing.schedule.Schedule;
import fr.ign.simulatedannealing.temperature.SimpleTemperature;
import fr.ign.simulatedannealing.visitor.CompositeVisitor;
import fr.ign.simulatedannealing.visitor.OutputStreamVisitor;
import fr.ign.simulatedannealing.visitor.Visitor;

/**
 * Color palettes extrapolation demo application
 * @author Bertrand Duménieu
 *
 */
public class Extrapalettor implements Runnable {

  // Global random generator
  private final RandomGenerator rndg;

  // Default parameters
  private static final Parameters DEF_PARAMS;

  private final Parameters p;

  /**
   * All the default parameters
   */
  static {
    DEF_PARAMS = new Parameters();
    DEF_PARAMS.set("p_birthdeath", 0f);
    DEF_PARAMS.set("e0", 0.);
    DEF_PARAMS.set("temp", 420);
    DEF_PARAMS.set("deccoef", 0.9999);
    DEF_PARAMS.set("nbiter", 1000000);
    DEF_PARAMS.set("nbdump", 100000);
    DEF_PARAMS.set("nbsave", 0);
    DEF_PARAMS.set("nbColours", 4);
  }

  private final List<Visitor<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>>> visitors;

  private GraphConfiguration<ColourPointLCH> final_configuration;

  public Extrapalettor() {
    this.p = new Parameters();
    for (ParameterComponent DEF_P : DEF_PARAMS.entry) {
      if (DEF_P instanceof Parameter) {
        String key = ((Parameter) DEF_P).getKey();
        Object value = ((Parameter) DEF_P).getValue();
        this.p.add(new Parameter(key, value));
      }
    }

    rndg = new MersenneTwister();

    this.visitors = new ArrayList<>();
  }

  @Override
  public void run() {
    GraphConfiguration<ColourPointLCH> conf = this.create_configuration((ColorSpace) p.get("colorspace"),
        p.getDouble("e0"), p.getInteger("nbColours"));

    DirectSampler<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> ds = this
        .create_BirthDeathSampler(p, this.rndg);
    Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> sampler = this.create_sampler(p,
        this.rndg, ds);

    Schedule<SimpleTemperature> sch = new GeometricSchedule<SimpleTemperature>(
        new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));

    EndTest end = new StabilityEndTest<>(p.getInteger("nbiter"), 0.1);
    CompositeVisitor<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> viz = new CompositeVisitor<>(
        visitors);
    viz.init(p.getInteger("nbdump"), p.getInteger("nsave"));

    SimulatedAnnealing.optimize(this.rndg, conf, sampler, sch, end, viz);
    this.final_configuration = conf;
  }

  private GraphConfiguration<ColourPointLCH> create_configuration(ColorSpace cs, double p_e0, int nbColours) {
    ColourPointLCH[] pts = new ColourPointLCH[nbColours];
    ColourPointLCHBuilder csb = new ColourPointLCHBuilder();
    for (int i = 0; i < nbColours; i++) {
      ColourPointLCH cp = csb
          .build(new double[] { rndg.nextFloat() * 100f, rndg.nextFloat() * 100f, rndg.nextFloat() * 360f, i });
      pts[i] = cp;
    }

    ConstantEnergy<ColourPointLCH, ColourPointLCH> e0 = new ConstantEnergy<>(p_e0);

    BinaryFuzzyConstraintsEnergy e1 = new BinaryFuzzyConstraintsEnergy();
    e1.addConstraint(new LightnessDistanceFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 50));
    e1.addConstraint(new ChromaProximityFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 1));
    e1.addConstraint(new HueProximityFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 1));

    NaryEnergy e2 = new NaryEnergy();
    List<ColourPointLCH> seq = new ArrayList<>();
    for (int i = 0; i < nbColours; i++) {
      seq.add(pts[i]);
    }
    e2.addConstraint(new ColourSequenceConstraint(seq));

    GraphConfiguration<ColourPointLCH> gc = new GraphConfiguration<>(e0, e1, e2);
    for (ColourPointLCH p : pts) {
      gc.insert(p);
    }

    return gc;
  }

  private DirectSampler<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> create_BirthDeathSampler(
      Parameters p, RandomGenerator rndg) {
    ColorSpace cs = new CIELchColorSpace(false);
    ColourPointLCH minValsColourPointLCH = new ColourPointLCH(0);
    minValsColourPointLCH.setComponents(cs.getMinValue(0), cs.getMinValue(1), cs.getMinValue(2));
    ColourPointLCH maxValsColourPointLCH = new ColourPointLCH(p.getInteger("nbColours") - 1);
    maxValsColourPointLCH.setComponents(cs.getMaxValue(0), cs.getMaxValue(1), cs.getMaxValue(2));
    UniformBirth<ColourPointLCH> birth = new UniformBirth<ColourPointLCH>(rndg, minValsColourPointLCH,
        maxValsColourPointLCH, new ColourPointLCHBuilder());
    DirectSampler<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> ds = new DirectSampler<>(
        new UniformDistribution(rndg, 0, p.getInteger("nbColours")), birth);

    return ds;
  }

  public Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> create_sampler(
      Parameters p, RandomGenerator rndg,
      DirectSampler<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> ds) {

    KernelFactory<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> kf = new KernelFactory<>();
    List<Kernel<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>>> kernels = new ArrayList<>();
    ColourPointLCHBuilder cpb = new ColourPointLCHBuilder();
    // TODO Put the components ranges in the parameters
    ColourPointLightnessShift mL = new ColourPointLightnessShift(100f);
    ColourPointChromaShift mC = new ColourPointChromaShift(100f);
    ColourPointHueShift mH = new ColourPointHueShift(360f);

    kernels.add(kf.make_uniform_modification_kernel(rndg, cpb, mL, 1., "Lightness shift"));
    kernels.add(kf.make_uniform_modification_kernel(rndg, cpb, mC, 1., "Chroma shift"));
    kernels.add(kf.make_uniform_modification_kernel(rndg, cpb, mH, 1., "Hue shift"));

    Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
    Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> sampler = new GreenSampler<>(
        rndg, ds, acceptance, kernels);
    return sampler;
  }

  private void addVisitor(Visitor v) {
    this.visitors.add(v);
  }

  private GraphConfiguration<ColourPointLCH> getFinalConfiguration() {
    return this.final_configuration;
  }

  public static void main(String[] argv) throws InterruptedException {

    Extrapalettor palettor = new Extrapalettor();

    Visitor vConsole = new OutputStreamVisitor<>(System.out);
    Visitor vGUI = new GUIPaletteVisitor();

    palettor.addVisitor(vConsole);
    palettor.addVisitor(vGUI);

    Thread t = new Thread(palettor);
    t.run();
    t.join();

    // GUI
    PaletteViewer pv = new PaletteViewer();
    pv.setSize(600, 500);

    GraphConfiguration<ColourPointLCH> palette = palettor.getFinalConfiguration();
    List<ColourPointLCH> listOfColours = new ArrayList<>();
    for (GraphVertex<ColourPointLCH> pelement : palette.getGraph().vertexSet()) {
      listOfColours.add(pelement.getValue());
    }
    listOfColours.sort(new Comparator<ColourPointLCH>() {
      @Override
      public int compare(ColourPointLCH o1, ColourPointLCH o2) {
        return Integer.compare(o1.getId(), o2.getId());
      }
    });
    Color[] pArray = new Color[listOfColours.size()];
    for (int i = 0; i < pArray.length; i++) {
      ColourPointLCH c = listOfColours.get(i);
      System.out.println("LCH=" + Arrays.toString(c.getComponents()));
      float[] rgb = c.getColorSpace().toRGB(c.getComponents());
      // TODO Clamp the LCH colours in the sRGB Gamut in the optimization, not
      // here.
      float clampedR = rgb[0] < 0 ? 0f : rgb[0] > 1 ? 1f : rgb[0];
      float clampedG = rgb[1] < 0 ? 0f : rgb[1] > 1 ? 1f : rgb[1];
      float clampedB = rgb[2] < 0 ? 0f : rgb[2] > 1 ? 1f : rgb[2];
      Color awtColor = new Color(clampedR, clampedG, clampedB);
      pArray[i] = awtColor;
    }
    pv.addPalette(pArray);
    pv.setVisible(true);
    return;
  }

}
