package fr.ign.cogit.palettes.extrapalettor.constraints;

import java.util.List;

public interface GraphConstraint <T> {
    
	double evaluate(T... vertices);

	List<Integer> getVerticesIndexes();

}
