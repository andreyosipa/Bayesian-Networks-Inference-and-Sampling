package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.XMLBIFParser;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BayesianNetwork bayesianNetwork;
        try {
             bayesianNetwork = new XMLBIFParser().readNetworkFromFile("src/bn/examples/dog-problem.xml");
        } catch (Exception e) {
            e.printStackTrace();
            bayesianNetwork = new BayesianNetwork();
        }

        List<RandomVariable> vars = bayesianNetwork.getVariableList();

        Distribution distribution;
        int num_trials = 10000;
        for (int idx = 0; idx < vars.size(); idx++) {
            distribution = new InferenceByEnum().ask(bayesianNetwork, vars.get(idx), new Assignment());
            System.out.print(vars.get(idx));
            System.out.println(distribution.toString());

            distribution = new RejectionSampling().ask(bayesianNetwork, vars.get(idx), new Assignment(), num_trials);
            System.out.print(vars.get(idx));
            System.out.println(distribution.toString());

            distribution = new LikelihoodWeighting().ask(bayesianNetwork, vars.get(idx), new Assignment(), num_trials);
            System.out.print(vars.get(idx));
            System.out.println(distribution.toString());

            distribution = new GibbsSampling().ask(bayesianNetwork, vars.get(idx), new Assignment(), num_trials);
            System.out.print(vars.get(idx));
            System.out.println(distribution.toString());
        }

    }
}
