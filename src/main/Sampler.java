package main;

/**
 * Created by Andrey on 31.10.17.
 */

import bn.core.*;

public interface Sampler {
    /*
	 * Returns the Distribution of the query RandomVariable X
	 * given evidence Assignment e and number of trials n using
	 * the distribution encoded by the BayesianNetwork bn.
	 */
    public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e, int n);
}
