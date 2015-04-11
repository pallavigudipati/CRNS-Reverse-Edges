package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.GlobalVariables;
import utils.Utils;

public class TextNetwork {
    Configuration config;
    // Entity entities;

    public int numElements;
    public List<List<Double>> relevanceMatrix; // ie x cases
    public List<List<Double>> similarityMatrix;
    public List<List<Double>> caseCaseMatrix;
    public List<List<Double>> reverseMatrix;
    // public List<String> caseActivation;
    public List<String> caseLookup; // For visualization.
    public List<String> entityLookup; // For visualization
    public List<Integer> caseClasses;

    public TextNetwork(Configuration config) {
        this.config = config;
        // this.entities = new Entity(config.entityLookupFileName);

        relevanceMatrix = new ArrayList<List<Double>>();
        similarityMatrix = new ArrayList<List<Double>>();
        caseCaseMatrix = new ArrayList<List<Double>>();
        reverseMatrix = new ArrayList<List<Double>>();
        entityLookup = new ArrayList<String>();
        caseLookup = new ArrayList<String>();
    }

    public void populateLookupsAndMatrices() {
        this.numElements = similarityMatrix.size();
        // TODO Add everything
        Utils.readDoubleMatrix(similarityMatrix, config.similarityFileName);
        Utils.readDoubleMatrix(relevanceMatrix, config.relevanceFileName);
        // TODO case-case
        // TODO reverse
        // TODO case and entity lookup
        // TODO case classes
        Utils.readIntegerList(caseClasses, config.caseClassFileName);
    }

    public double evaluate(HashMap<Integer, Double> inititalActivations, int inputClass) {
        List<Integer> results = retrieveIds(inititalActivations);
        double numCorrect = 0;
        for (int result : results) {
            numCorrect += inputClass == caseClasses.get(result) ? 1 : 0;
        }
        return numCorrect / config.budget; 
    }

    public List<Integer> retrieveIds(HashMap<Integer, Double> inititalActivations) {
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        if (config.spreadingMode == GlobalVariables.SpreadingMode.BASIC) {
            basicAcitivationSpread(inititalActivations, caseActivations);
        }
        return Utils.getTopCases(caseActivations, config.budget);
    }

    public void basicAcitivationSpread(HashMap<Integer, Double> activations,
            HashMap<Integer,Double> caseActivations) {
        // Similarity Spread.
        for (int initialIe : activations.keySet()) {
            double initialIeActivation = activations.get(initialIe);
            List<Double> similarities = similarityMatrix.get(initialIe);
            for (int targetIe = 0; targetIe < similarities.size(); ++targetIe) {
                if (similarities.get(targetIe) != 0) {
                    double extraActivation = initialIeActivation * similarities.get(targetIe);
                    if (activations.get(targetIe) != null) {
                        activations.put(targetIe,Math.max(1, activations.get(targetIe)
                                + extraActivation));
                    } else {
                        activations.put(targetIe, extraActivation);
                    }
                }
            }
        }
        // Relevance Spread.
        for (int ie : activations.keySet()) {
            double ieActivation = activations.get(ie);
            List<Double> relevances = relevanceMatrix.get(ie);
            for (int caseId = 0; caseId < relevances.size(); ++caseId) {
                if (relevances.get(caseId) != 0) {
                    double extraActivation = ieActivation * relevances.get(caseId);
                    if (caseActivations.get(caseId) != null) {
                        caseActivations.put(caseId, caseActivations.get(caseId) + extraActivation);
                    } else {
                        caseActivations.put(caseId, extraActivation);
                    }
                }
            }
        }
    }
}
