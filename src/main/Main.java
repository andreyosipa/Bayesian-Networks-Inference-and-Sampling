package main;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.XMLBIFParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by Andrey on 31.10.17.
 */
public class Main {
    public static void main(String[] args) {
        BayesianNetwork bayesianNetwork;
        try {
             bayesianNetwork = new XMLBIFParser().readNetworkFromFile("src/bn/examples/aima-alarm.xml");
        } catch (Exception e) {
            e.printStackTrace();
            bayesianNetwork = new BayesianNetwork();
        }

        System.out.println(bayesianNetwork.getChildren(bayesianNetwork.getVariableByName("B")));

        List<RandomVariable> vars = bayesianNetwork.getVariableList();
        Distribution distribution = new InferenceByEnum().ask(bayesianNetwork, vars.get(0), new Assignment());
        distribution.normalize();
        System.out.print(vars.get(0));
        System.out.println(distribution.toString());
        distribution = new InferenceByEnum().ask(bayesianNetwork, vars.get(1), new Assignment());
        distribution.normalize();
        System.out.print(vars.get(1));
        System.out.println(distribution.toString());
        distribution = new InferenceByEnum().ask(bayesianNetwork, vars.get(2), new Assignment());
        distribution.normalize();
        System.out.print(vars.get(2));
        System.out.println(distribution.toString());
        distribution = new InferenceByEnum().ask(bayesianNetwork, vars.get(3), new Assignment());
        distribution.normalize();
        System.out.print(vars.get(3));
        System.out.println(distribution.toString());
        distribution = new InferenceByEnum().ask(bayesianNetwork, vars.get(4), new Assignment());
        distribution.normalize();
        System.out.print(vars.get(4));
        System.out.println(distribution.toString());
    }
}
