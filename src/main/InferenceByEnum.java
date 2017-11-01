package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.inference.Inferencer;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Andrey on 31.10.17.
 */
public class InferenceByEnum implements Inferencer {
    @Override
    public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e) {
        Distribution resultDistribution = new Distribution(X);

        for (Object d : X.getDomain()) {
            e.set(X, d);
            resultDistribution.put(d, enumerateAll(bn, bn.getVariableListTopologicallySorted(), e));
        }

        resultDistribution.normalize();
        return resultDistribution;
    }

    protected double enumerateAll(BayesianNetwork bn, List<RandomVariable> variables, Assignment e) {
        if (variables.size() == 0)
            return 1.0;
        RandomVariable x = variables.get(0);
        if (e.keySet().contains(x)) {
            return bn.getProb(x, e) * enumerateAll(bn, variables.subList(1, variables.size()), e);
        }
        else {
            Assignment e_upd = e.copy();
            double result = 0;
            for (Object d : x.getDomain()) {
                e_upd.set(x, d);
                result += bn.getProb(x, e_upd) * enumerateAll(bn, variables.subList(1, variables.size()), e_upd);
            }
            return result;
        }
    }
}
