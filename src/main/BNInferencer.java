package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.BIFLexer;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Andrey on 02.11.17.
 */
public class BNInferencer {
    public static void main(String[] args) {
        String fileName = args[0];
        String goalVarName = args[1];

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

        List<RandomVariable> variables = bayesianNetwork.getVariableListTopologicallySorted();
        //find RV that is corresponds to the goal one
        int var_index = 0;
        while (!variables.get(var_index).getName().equals(goalVarName))
            var_index++;
        RandomVariable goalVariable = variables.get(var_index);

        for (int index = 0; index < variables.size(); index ++) {
            System.out.println(variables.get(index).getName());
        }

        Assignment e = new Assignment();
        if (args[2].equals("-ra")) {
            //this command gives a chance not to specify assignment and generate random one.
            variables.remove(var_index);
            Collections.shuffle(variables);
            variables = variables.subList(0, Integer.parseInt(args[3]));
            Random rn = new Random();
            for (RandomVariable v : variables) {
                e.set(v, v.getDomain().get(rn.nextInt(v.getDomain().size())));
            }
            System.out.println("Randomly generated variable assignment: " + e.toString());
        }
        else {
            //read assignment
            var_index = 0;
            for (int index = 2; index < args.length; index += 2) {
                var_index = 0;
                while (!variables.get(var_index).getName().equals(args[index]))
                    var_index++;
                e.put(variables.get(var_index), args[index + 1]);
            }
        }

        long startTime = System.currentTimeMillis();
        Distribution distribution = new InferenceByEnum().ask(bayesianNetwork, goalVariable, e);
        long endTime = System.currentTimeMillis();
        System.out.println("Computation done in " + (endTime - startTime) + " milliseconds");
        System.out.println("Inference by Enumeration result: " + distribution.toString());
    }
}
