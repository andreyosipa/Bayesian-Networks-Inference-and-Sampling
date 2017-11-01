package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;

import java.util.List;
import java.util.Objects;

/**
 * Created by Andrey on 31.10.17.
 */
public class RejectionSampling implements Sampler{
    @Override
    public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e, int n) {
        Distribution resultDistribution = new Distribution(X);
        for (Object d : X.getDomain()) {
            resultDistribution.put(d, 0);
        }

        Assignment randomAssignment = new Assignment();
        List<RandomVariable> variables = bn.getVariableListTopologicallySorted();
        for (int trial = 0; trial < n; trial ++) {
            randomAssignment = priorSample(bn);
            boolean consistent = true;
            for (Object key : e.keySet()) {
                consistent = consistent && (e.get(key) == randomAssignment.get(key));
            }
            if (consistent) {
                resultDistribution.put(randomAssignment.get(X), resultDistribution.get(randomAssignment.get(X)) + 1);
            }
        }

        resultDistribution.normalize();
        return resultDistribution;
    }

    protected Assignment priorSample(BayesianNetwork bn) {
        Assignment assignment = new Assignment();
        List<RandomVariable> variables = bn.getVariableListTopologicallySorted();

        Distribution temporaryDistribution;
        for (RandomVariable v : variables) {
            //find distribution for variable v given current assignment
            temporaryDistribution = new Distribution(v);
            for (Object d : v.getDomain()) {
                assignment.set(v, d);
                temporaryDistribution.put(d, bn.getProb(v, assignment));
                assignment.remove(v);
            }
            //sample random value from the last distribution
            assignment.set(v, temporaryDistribution.randomSample());
        }

        return assignment;
    }

}
