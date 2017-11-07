package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.BIFLexer;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Andrey on 01.11.17.
 */
public class BNApproximateInferencer {
    public static void main(String[] args) {
        int n_trials = Integer.parseInt(args[0]);
        String fileName = args[1];
        String goalVarName = args[2];

        BayesianNetwork bayesianNetwork;
        try {
            if (fileName.endsWith(".xml"))
                bayesianNetwork = new XMLBIFParser().readNetworkFromFile(fileName);
            else
                bayesianNetwork = new BIFParser(new BIFLexer(new FileInputStream(fileName))).parseNetwork();
        } catch (Exception exception) {
            exception.printStackTrace();
            bayesianNetwork = new BayesianNetwork();
        }

        List<RandomVariable> variables = bayesianNetwork.getVariableList();

        int var_index = 0;
        while (!variables.get(var_index).getName().equals(goalVarName))
            var_index++;
        RandomVariable goalVariable = variables.get(var_index);

        //read assignment
        Assignment e = new Assignment();
        if (args[3].equals("-ra")) {
            //this command gives a chance not to specify assignment and generate random one.
            variables.remove(var_index);
            Collections.shuffle(variables);
            variables = variables.subList(0, Integer.parseInt(args[4]));
            Random rn = new Random();
            for (RandomVariable v : variables) {
                e.set(v, v.getDomain().get(rn.nextInt(v.getDomain().size())));
            }
            System.out.println("Randomly generated variable assignment: " + e.toString());
        }
        else {
            var_index = 0;
            for (int index = 3; index < args.length - ((args.length + 1) % 2); index += 2) {
                var_index = 0;
                while (!variables.get(var_index).getName().equals(args[index]))
                    var_index++;
                e.put(variables.get(var_index), args[index + 1]);
            }
        }

        Distribution distribution = null;
        long startTime, endTime;
        if (args.length % 2 == 0) {
            startTime = System.currentTimeMillis();
            if (args[args.length - 1].equals("rs")) {
                distribution = new RejectionSampling().ask(bayesianNetwork, goalVariable, e, n_trials);
            }
            else if (args[args.length - 1].equals("lw")) {
                distribution = new LikelihoodWeighting().ask(bayesianNetwork, goalVariable, e, n_trials);
            }
            else if (args[args.length - 1].equals("gs")) {
                distribution = new GibbsSampling().ask(bayesianNetwork, goalVariable, e, n_trials);
            }
            endTime = System.currentTimeMillis();
            System.out.println("Computation done in " + (endTime - startTime) + " milliseconds");
            System.out.println(distribution.toString());
        }
        else {
            startTime = System.currentTimeMillis();
            distribution = new RejectionSampling().ask(bayesianNetwork, goalVariable, e, n_trials);
            endTime = System.currentTimeMillis();
            System.out.println("Computation done in " + (endTime - startTime) + " milliseconds");
            System.out.println("Rejection Sampling result: " + distribution.toString());
            startTime = System.currentTimeMillis();
            distribution = new LikelihoodWeighting().ask(bayesianNetwork, goalVariable, e, n_trials);
            endTime = System.currentTimeMillis();
            System.out.println("Computation done in " + (endTime - startTime) + " milliseconds");
            System.out.println("Likelihood Weighting result: " + distribution.toString());
            startTime = System.currentTimeMillis();
            distribution = new GibbsSampling().ask(bayesianNetwork, goalVariable, e, n_trials);
            endTime = System.currentTimeMillis();
            System.out.println("Computation done in " + (endTime - startTime) + " milliseconds");
            System.out.println("Gibbs Sampling result: " + distribution.toString());
        }
    }
}
