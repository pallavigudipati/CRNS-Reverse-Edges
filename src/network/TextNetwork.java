package network;

import java.util.HashMap;
import java.util.List;

import utils.GlobalVariables;
import utils.Utils;

public class TextNetwork {
    Configuration config;
    Entity entities;

    public int numElements;
    public List<List<Double>> relevanceMatrix;
    public List<List<Double>> similarityMatrix;
    public List<List<Double>> caseCaseMatrix;
    public List<List<Double>> reverseMatrix;
    public List<String> caseActivation;

    public TextNetwork(Configuration config) {
        this.config = config;
        this.entities = new Entity(config.entityLookupFileName);
    }

    public void populateLookupsAndMatrices() {
        // TODO
        this.numElements = similarityMatrix.size();
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
