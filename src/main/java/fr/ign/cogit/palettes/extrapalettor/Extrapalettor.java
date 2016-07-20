package fr.ign.cogit.palettes.extrapalettor;

import java.awt.color.ColorSpace;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.cogit.geoxygene.semio.color.CIELabColorSpace;
import fr.ign.cogit.geoxygene.semio.color.CIELchColorSpace;
import fr.ign.cogit.palettes.extrapalettor.constraints.ChromaProximityFuzzyBinaryConstraint;
import fr.ign.cogit.palettes.extrapalettor.constraints.ColourSequenceConstraint;
import fr.ign.cogit.palettes.extrapalettor.constraints.HueProximityFuzzyBinaryConstraint;
import fr.ign.cogit.palettes.extrapalettor.constraints.LightnessDistanceFuzzyBinaryConstraint;
import fr.ign.cogit.palettes.extrapalettor.gui.GUIPaletteVisitor;
import fr.ign.cogit.palettes.extrapalettor.gui.GUIPalettesViewer;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
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
 * 
 * @author Bertrand Duménieu
 *
 */
public class Extrapalettor implements Runnable {

	// Global random generator
	private final RandomGenerator rndg;

	// Default parameters
	private static final Parameters DEF_PARAMS;

	private final Parameters p;

	private final List<Visitor<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>>> visitors;

	private GraphConfiguration<ColorPoint> final_configuration;

	/**
	 * All the default parameters
	 */
	static {
		DEF_PARAMS = new Parameters();
		DEF_PARAMS.set("p_birthdeath", 0f);
		DEF_PARAMS.set("e0", 0.);
		DEF_PARAMS.set("temp", 1000);
		DEF_PARAMS.set("deccoef", 0.9999);
		DEF_PARAMS.set("nbiter", 10000);
		DEF_PARAMS.set("nbdump", 1000);
		DEF_PARAMS.set("nbsave", 0);
		DEF_PARAMS.set("nbColours", 40);
	}

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

	public Extrapalettor(Parameters _p) {
		this();
		for (ParameterComponent param : _p.entry) {
			Parameter pp = (Parameter) param;
			this.p.set(pp.getKey(), pp.getValue());
		}
	}

	@Override
	public void run() {
		GraphConfiguration<ColorPoint> conf = this.create_configuration((ColorSpace) p.get("colorspace"),
				p.getDouble("e0"), p.getInteger("nbColours"));

		DirectSampler<ColorPoint, GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> ds = this
				.create_BirthDeathSampler(p, this.rndg);
		Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> sampler = this.create_sampler(p,
				this.rndg, ds);

		Schedule<SimpleTemperature> sch = new GeometricSchedule<SimpleTemperature>(
				new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));

		EndTest end = new StabilityEndTest<>(p.getInteger("nbiter"), 0.1);
		CompositeVisitor<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> viz = new CompositeVisitor<>(
				visitors);
		viz.init(p.getInteger("nbdump"), p.getInteger("nsave"));

		SimulatedAnnealing.optimize(this.rndg, conf, sampler, sch, end, viz);
		this.final_configuration = conf;
	}

	private GraphConfiguration<ColorPoint> create_configuration(ColorSpace cs, double p_e0, int nbColours) {
		ColorPoint[] pts = new ColorPoint[nbColours];
		ColorPointBuilder csb = new ColorPointBuilder();
		for (int i = 0; i < nbColours; i++) {
			ColorPoint cp = csb.build(
					// new double[] { rndg.nextFloat() * 100f,
					// rndg.nextFloat()*100f, rndg.nextFloat() * 360f, i });
					new double[] { rndg.nextFloat() * 100f, rndg.nextFloat() * 100f, rndg.nextFloat() * 360f, i });
			pts[i] = cp;
		}

		ConstantEnergy<ColorPoint, ColorPoint> e0 = new ConstantEnergy<>(p_e0);

		BinaryFuzzyConstraintsEnergy e1 = new BinaryFuzzyConstraintsEnergy();
		e1.addConstraint(new LightnessDistanceFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 50));
		e1.addConstraint(new ChromaProximityFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 1));
		e1.addConstraint(new HueProximityFuzzyBinaryConstraint(pts[0], pts[nbColours - 1], 20));

		NaryEnergy e2 = new NaryEnergy();
		List<ColorPoint> seq = new ArrayList<>();
		for (int i = 0; i < nbColours; i++) {
			seq.add(pts[i]);
		}
		e2.addConstraint(new ColourSequenceConstraint(seq));

		GraphConfiguration<ColorPoint> gc = new GraphConfiguration<>(e0, e1, e2);
		for (ColorPoint p : pts) {
			gc.insert(p);
		}

		return gc;
	}

	private DirectSampler<ColorPoint, GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> create_BirthDeathSampler(
			Parameters p, RandomGenerator rndg) {
		CIELchColorSpace cs = new CIELchColorSpace(false);
		ColorPoint minValsColorPoint = new ColorPoint(0);
		minValsColorPoint.setComponents(cs.getMinValue(0), cs.getMinValue(1), cs.getMinValue(2));
		ColorPoint maxValsColorPoint = new ColorPoint(p.getInteger("nbColours") - 1);
		maxValsColorPoint.setComponents(cs.getMaxValue(0), cs.getMaxValue(1), cs.getMaxValue(2));
		UniformBirth<ColorPoint> birth = new UniformBirth<ColorPoint>(rndg, minValsColorPoint, maxValsColorPoint,
				new ColorPointBuilder());
		DirectSampler<ColorPoint, GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> ds = new DirectSampler<>(
				new UniformDistribution(rndg, 0, p.getInteger("nbColours")), birth);
		return ds;
	}

	public Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> create_sampler(Parameters p,
			RandomGenerator rndg,
			DirectSampler<ColorPoint, GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> ds) {

		KernelFactory<ColorPoint, GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> kf = new KernelFactory<>();
		List<Kernel<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>>> kernels = new ArrayList<>();
		ColorPointBuilder cpb = new ColorPointBuilder();
		// TODO Put the components ranges in the parameters
		ColourPointLightnessShift mL = new ColourPointLightnessShift(100d);
		ColourPointChromaShift mc = new ColourPointChromaShift(100d);
		ColourPointHueShift mh = new ColourPointHueShift(360d);

		kernels.add(kf.make_uniform_modification_kernel(rndg, cpb, mL, 1., "L"));
		kernels.add(kf.make_uniform_modification_kernel(rndg, cpb, mc, 1., "C"));
		kernels.add(kf.make_uniform_modification_kernel(rndg, cpb, mh, 1., "H"));

		Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
		Sampler<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> sampler = new GreenSampler<>(rndg,
				ds, acceptance, kernels);
		return sampler;
	}

	private void addVisitor(Visitor<GraphConfiguration<ColorPoint>, BirthDeathModification<ColorPoint>> v) {
		this.visitors.add(v);
	}

	private GraphConfiguration<ColorPoint> getFinalConfiguration() {
		return this.final_configuration;
	}

	// #############################################
	// Main - for test purpose
	// #############################################
	public static void main(String[] argv) throws InterruptedException, IOException {

		// TODO Write an input options parser...
		boolean GUI_LOGGING = true;
		boolean GUI_OUTLINE_OUTOFSRGBGAMUT = false;
		boolean GUI_PLOT_ITERATIONS = true;

		Parameters p = new Parameters();
		p.set("nbColours", 10);

		Extrapalettor palettor = new Extrapalettor(p);

		Visitor vConsole = new OutputStreamVisitor<>(System.out);

		palettor.addVisitor(vConsole);

		if (GUI_LOGGING) {
			Visitor vGUI = new GUIPaletteVisitor(GUI_OUTLINE_OUTOFSRGBGAMUT, GUI_PLOT_ITERATIONS);
			palettor.addVisitor(vGUI);
			GUIPalettesViewer pv = new GUIPalettesViewer();
			pv.setSize(600, 500);
			pv.add((JPanel) vGUI);
			pv.setVisible(true);
		}

		Thread t = new Thread(palettor);
		t.run();
		t.join();

		// JSON Logging
		// TODO : make something better...like a decent JSON logger/exporter.
//		GraphConfiguration<ColorPoint> fc = palettor.getFinalConfiguration();
//		FileWriter fw = new FileWriter("test.json");
//		fw.write("pdata='{");
//		CIELchColorSpace cslch = new CIELchColorSpace(false);
//		for (ColorPoint c : fc) {
//			float[] lab = cslch.toCIELab(c.getComponents());
//			float[] lch = c.getComponents();
//			System.out.println(c.getId() + " " + lch[0] + " " + lch[1] + " " + lch[2]);
//			float[] rgb = c.getColorSpace().toRGB(lab);
//			rgb[0] = rgbClamp(rgb[0]) * 255;
//			rgb[1] = rgbClamp(rgb[1]) * 255;
//			rgb[2] = rgbClamp(rgb[2]) * 255;
//			fw.write(Arrays.toString(rgb)+",");
//		}
//		fw.write("}'");
//		fw.flush();
//		fw.close();
	}

	private static float rgbClamp(float c) {
		return c > 1f ? 1f : c < 0f ? 0f : c;
	}

}
