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
import fr.ign.cogit.palettes.extrapalettor.gui.PaletteViewer;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.configuration.GraphVertex;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.UniformBirth;
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

public class Extrapalettor {

  RandomGenerator rndg = new MersenneTwister();

  public GraphConfiguration<ColourPointLCH> create_configuration(ColorSpace cs, double p_e0, int nbColours) {
    ColourPointLCH[] pts = new ColourPointLCH[nbColours];
    ColourPointLCHBuilder csb = new ColourPointLCHBuilder();
    for (int i = 0; i < nbColours; i++) {
      ColourPointLCH cp = csb
          .build(new double[] { rndg.nextFloat() * 100f, rndg.nextFloat() * 100f, rndg.nextFloat() * 360f, i });
      pts[i] = cp;
    }

    ConstantEnergy<ColourPointLCH, ColourPointLCH> e0 = new ConstantEnergy<>(p_e0);

    BinaryFuzzyConstraintsEnergy e1 = new BinaryFuzzyConstraintsEnergy();
    e1.addConstraint(new LightnessDistanceFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 1));
    e1.addConstraint(new ChromaProximityFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 1));
    e1.addConstraint(new HueProximityFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 1));

    NaryEnergy e2 = new NaryEnergy();
    List<ColourPointLCH> seq = new ArrayList<>();
    for (int i = 0; i < nbColours; i++) {
      seq.add(pts[i]);
    }
    e2.addConstraint(new ColourSequenceConstraint(seq));

    GraphConfiguration<ColourPointLCH> gc = new GraphConfiguration<>(e0, e1);
    for (ColourPointLCH p : pts) {
      gc.insert(p);
    }

    return gc;
  }

  public DirectSampler<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> create_BirthDeathSampler(
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

  public static void main(String[] argv) {
    PaletteViewer pv = new PaletteViewer();
    pv.setSize(600, 500);

    for (int N = 0; N < 1; N++) {
      Parameters p = new Parameters();
      p.set("p_birthdeath", 0f);
      p.set("e0", 0.);
      p.set("temp", 420);
      p.set("deccoef", 0.9999);
      p.set("nbiter", 1000000);
      p.set("nbdump", 100000);
      p.set("nbsave", 0);
      p.set("nbColours", 2);

      Extrapalettor palettor = new Extrapalettor();

      GraphConfiguration<ColourPointLCH> conf = palettor.create_configuration((ColorSpace) p.get("colorspace"),
          p.getDouble("e0"), p.getInteger("nbColours"));

      DirectSampler<ColourPointLCH, GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> ds = palettor
          .create_BirthDeathSampler(p, palettor.rndg);
      Sampler<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> sampler = palettor
          .create_sampler(p, palettor.rndg, ds);

      Schedule<SimpleTemperature> sch = new GeometricSchedule<SimpleTemperature>(
          new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));

      EndTest end = new StabilityEndTest<>(p.getInteger("nbiter"), 0.1);

      List<Visitor<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>>> visitors = new ArrayList<>();
      visitors.add(new OutputStreamVisitor<>(System.out));
      CompositeVisitor<GraphConfiguration<ColourPointLCH>, BirthDeathModification<ColourPointLCH>> viz = new CompositeVisitor<>(
          visitors);

      viz.init(p.getInteger("nbdump"), p.getInteger("nsave"));

      SimulatedAnnealing.optimize(palettor.rndg, conf, sampler, sch, end, viz);

      Color[] palette = new Color[(int) p.get("nbColours")];
      int i = 0;
      List<ColourPointLCH> listOfColours = new ArrayList<>();
      for (GraphVertex<ColourPointLCH> v : conf.getGraph().vertexSet()) {
        listOfColours.add(v.getValue());
      }
      listOfColours.sort(new Comparator<ColourPointLCH>() {
        @Override
        public int compare(ColourPointLCH o1, ColourPointLCH o2) {
          return Integer.compare(o1.getId(), o2.getId());
        }
      });
      for (ColourPointLCH v : listOfColours) {
        System.out.println("LCH=" + Arrays.toString(v.getComponents()));
        float[] rgb = v.getColorSpace().toRGB(v.getComponents());
        // TODO Clamp the LCH colours in the sRGB Gamut in the optimization, not
        // here.
        float clampedR = rgb[0] < 0 ? 0f : rgb[0] > 1 ? 1f : rgb[0];
        float clampedG = rgb[1] < 0 ? 0f : rgb[1] > 1 ? 1f : rgb[1];
        float clampedB = rgb[2] < 0 ? 0f : rgb[2] > 1 ? 1f : rgb[2];
        Color c = new Color(clampedR, clampedG, clampedB);
        palette[i++] = c;
      }

      pv.addPalette(palette);
    }
    pv.setVisible(true);
    return;
  }

}
