package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import java.util.List;
import java.util.Random;

public class GibbsSampling implements Sampler {
    @Override
    public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e, int n) {
        Distribution resultDistribution = new Distribution(X);
        for (Object d : X.getDomain()) {
            resultDistribution.put(d, 0);
        }

        Assignment assignment = e.copy();
        List<RandomVariable> variables = bn.getVariableListTopologicallySorted();
        Random random = new Random();
        //init random assignment
        for (RandomVariable v : variables) {
            if (!assignment.keySet().contains(v)) {
                assignment.set(v, v.getDomain().get(random.nextInt(v.getDomain().size())));
            }
        }

        //make variables contain only non-evidence variables
        variables.removeAll(e.keySet());

        Distribution temporaryDistribution;
        for (int trial = 0; trial < n; trial ++) {
            for (RandomVariable v : variables) {
                //find distribution P(v|mb(v)) and set new value for v from that distribution
                assignment.set(v, markovBlanketDistribution(bn, v, assignment).randomSample());
                resultDistribution.put(assignment.get(X), resultDistribution.get(assignment.get(X)) + 1);
            }
        }

        resultDistribution.normalize();
        return  resultDistribution;
    }

    protected Distribution markovBlanketDistribution(BayesianNetwork bn, RandomVariable X, Assignment e) {
        Distribution resultDistribution = new Distribution(X);

        Assignment assignment = e.copy();

        for (Object d : X.getDomain()) {
            assignment.set(X, d);
            double result = bn.getProb(X, assignment);
            for (RandomVariable child : bn.getChildren(X)) {
                result *= bn.getProb(child, assignment);
            }
            resultDistribution.put(d, result);
        }

        resultDistribution.normalize();
        return resultDistribution;
    }

}
