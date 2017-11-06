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
public class LikelihoodWeighting implements Sampler {
    @Override
    public Distribution ask(BayesianNetwork bn, RandomVariable X, Assignment e, int n) {
        Distribution resultDistribution = new Distribution(X);
        for (Object d : X.getDomain()) {
            resultDistribution.put(d, 0);
        }

        for (int trial = 0; trial < n; trial ++) {
            WeightedEvent weightedEvent = WeightedSample(bn, e);
            resultDistribution.put(weightedEvent.event.get(X), resultDistribution.get(weightedEvent.event.get(X)) + weightedEvent.weight);
        }

        resultDistribution.normalize();
        return resultDistribution;
    }

    protected WeightedEvent WeightedSample(BayesianNetwork bn, Assignment e) {
        WeightedEvent event = new WeightedEvent(e);

        List<RandomVariable> variables = bn.getVariableListTopologicallySorted();
        for (RandomVariable v : variables) {
            if (e.keySet().contains(v)) {
                event.weight *= bn.getProb(v, event.event);
            }
            else {
                //find conditional distribution for v
                Distribution temporaryDistribution = new Distribution(v);
                for (Object d : v.getDomain()) {
                    event.event.set(v, d);
                    temporaryDistribution.put(d, bn.getProb(v, event.event));
                    event.event.remove(v);
                }
                //sample random value for v from the latter distribution
                event.event.set(v, temporaryDistribution.randomSample());
            }
        }
        return event;
    }

    protected class WeightedEvent {
        public double weight;
        public Assignment event;

        WeightedEvent(Assignment e) {
            this.weight = 1.0;
            this.event = e.copy();
        }
    }
}
