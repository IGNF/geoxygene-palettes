package fr.ign.cogit.extrapalettor.core.constraints;

import java.util.List;

public interface GraphConstraint <T> {
    
	double evaluate(T... vertices);

	List<Integer> getVerticesIndexes();

}
