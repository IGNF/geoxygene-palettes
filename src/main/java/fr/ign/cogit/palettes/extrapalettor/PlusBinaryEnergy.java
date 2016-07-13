package fr.ign.cogit.palettes.extrapalettor;

import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.BinaryEnergyOperator;

public class PlusBinaryEnergy<T, U> implements BinaryEnergyOperator<T, U> {

	BinaryEnergy<T, U> energy1;
	BinaryEnergy<T, U> energy2;

	public PlusBinaryEnergy(BinaryEnergy<T, U> e1, BinaryEnergy<T, U> e2) {
		this.energy1 = e1;
		this.energy2 = e2;
	}

	@Override
	public double getValue(T t, U u) {
		return this.energy1.getValue(t, u) + this.energy2.getValue(t, u);
	}
}
