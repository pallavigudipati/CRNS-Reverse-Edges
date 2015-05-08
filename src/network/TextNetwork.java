package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.GlobalVariables;
import utils.Utils;

public class TextNetwork {
    Configuration config;
    // Entity entities;

    // public int numElements;
    public List<List<Double>> relevanceMatrix; // ie x cases
    public List<List<Double>> similarityMatrix;
    public List<List<Double>> caseCaseMatrix;
    public List<List<Double>> reverseMatrix; // cases x ie
    // public List<String> caseActivation;
    public List<String> caseLookup; // For visualization.
    public List<String> entityLookup; // For visualization
    public List<Integer> caseClasses;
    public List<Integer> caseWordCounts;

    public TextNetwork(Configuration config) {
        this.config = config;
        // this.entities = new Entity(config.entityLookupFileName);

        relevanceMatrix = new ArrayList<List<Double>>();
        similarityMatrix = new ArrayList<List<Double>>();
        caseCaseMatrix = new ArrayList<List<Double>>();
        reverseMatrix = new ArrayList<List<Double>>();
        entityLookup = new ArrayList<String>();
        caseLookup = new ArrayList<String>();
        caseClasses = new ArrayList<Integer>();
        caseWordCounts = new ArrayList<Integer>();
    }

    public void populateLookupsAndMatrices() {
        // this.numElements = similarityMatrix.size();
        // TODO Add everything
        // Utils.readDoubleMatrix(similarityMatrix, config.similarityFileName);
        System.out.println("Populating relevance matrix");
        Utils.readDoubleMatrix(relevanceMatrix, config.relevanceFileName);
        // TODO case-case
        // TODO reverse
        // TODO case and entity lookup
        // TODO case classes
        System.out.println("Populating case classes");
        Utils.readIntegerList(caseClasses, config.caseClassFileName);
        if (config.caseWordCountsFileName != null) {
            System.out.println("Populating case word counts");
            Utils.readIntegerList(caseWordCounts, config.caseWordCountsFileName);
        }
        // SIMPLE REVERSE
        if (config.spreadingMode == GlobalVariables.SpreadingMode.SIMPLEREVERSE ||
                config.spreadingMode == GlobalVariables.SpreadingMode.CUTOFFREVERSE ||
                config.spreadingMode == GlobalVariables.SpreadingMode.ADDITIVESIMPLEREVERSE ||
                config.spreadingMode == GlobalVariables.SpreadingMode.ADDITIVECUTOFFREVERSE ||
                config.spreadingMode == GlobalVariables.SpreadingMode.DISCCUTOFFREVERSE ||
                config.spreadingMode == GlobalVariables.SpreadingMode.DISCADDCUTOFFREVERSE ||
                config.spreadingMode == GlobalVariables.SpreadingMode.CONTADCREVERSE) {
            Utils.matrixTranspose(relevanceMatrix, reverseMatrix);
        }
    }

    public double evaluate(String inFileName, int inputClass) {
        HashMap<Integer, Double> inititalActivations = new HashMap<Integer, Double>();
        Utils.readFirstRow(inititalActivations, inFileName);
        List<Integer> results = retrieveIds(inititalActivations);
        System.out.println(results.toString());
        double numCorrect = 0;
        for (int result : results) {
            numCorrect += inputClass == caseClasses.get(result) ? 1 : 0;
        }
        if (numCorrect > config.budget / 2) {
            return 1.0;
        }
        // return numCorrect;
        return 0;
    }

    public double evaluateImages(List<Double> testCase, int inputClass) {
        HashMap<Integer, Double> inititalActivations = new HashMap<Integer, Double>();
        for (int feature = 0; feature < testCase.size(); ++feature) {
            if (testCase.get(feature) > 0) {
                inititalActivations.put(feature, 1.0);
            }
        }
        List<Integer> results = retrieveIds(inititalActivations);
        System.out.println(results.toString());
        double numCorrect = 0;
        for (int result : results) {
            numCorrect += inputClass == caseClasses.get(result) ? 1 : 0;
        }
        if (numCorrect > config.budget / 2) {
            return 1.0;
        }
        // return numCorrect;
        return 0;
    }

    public List<Integer> retrieveIds(HashMap<Integer, Double> inititalActivations) {
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        if (config.spreadingMode == GlobalVariables.SpreadingMode.BASIC) {
            basicActivationSpread(inititalActivations, caseActivations);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.SIMPLEREVERSE) {
            simpleReverseSpread(inititalActivations, caseActivations);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.CUTOFFREVERSE) {
            double cutoff = inititalActivations.size() * 0.5;
            cutoffReverseSpread(inititalActivations, caseActivations, cutoff);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.ADDITIVESIMPLEREVERSE) {
            additiveSimpleReverseSpread(inititalActivations, caseActivations);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.ADDITIVECUTOFFREVERSE) {
            double cutoff = inititalActivations.size() * 0.7;
            additiveCutoffReverseSpread(inititalActivations, caseActivations, cutoff);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.DISCCUTOFFREVERSE) {
            double cutoff = inititalActivations.size() * 0.5;
            double disc = 0.5;
            discCutoffReverseSpread(inititalActivations, caseActivations, cutoff, disc);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.DISCADDCUTOFFREVERSE) {
            double cutoff = inititalActivations.size() * 0.6;
            double disc = 0.6;
            addDiscCutoffReverseSpread(inititalActivations, caseActivations, cutoff, disc);
        } else if (config.spreadingMode == GlobalVariables.SpreadingMode.CONTADCREVERSE) {
            double cutoff = inititalActivations.size() * 0.6;
            double disc = 0.5;
            double threshold = 0.2;
            ContADCReverseSpread(inititalActivations, caseActivations, cutoff, disc, threshold);
        }
        return Utils.getTopCases(caseActivations, config.budget);
    }

    public void basicActivationSpread(HashMap<Integer, Double> activations,
            HashMap<Integer,Double> caseActivations) {
        /*
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
        }*/
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
        if (config.disadvLongFiles) {
            for (int caseId : caseActivations.keySet()) {
                caseActivations.put(caseId, caseActivations.get(caseId) / caseWordCounts.get(caseId));
            }
        }
        normalizeCaseActivations(caseActivations);
    }

    public void simpleReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivationsFinal) {
        // First e->c activation
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        // Reverse Activation c-> e
        for (int caseNum : caseActivations.keySet()) {
            List<Double> reverseRelevances = reverseMatrix.get(caseNum);
            for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                if (reverseRelevances.get(eId) == 1) {
                    if (activations.get(eId) == null) {
                        activations.put(eId, 1.0);
                    }
                }
            }
        }
        // Second e->c activation
        basicActivationSpread(activations, caseActivationsFinal);
    }

    // Adds to the existing activation of the entities.
    public void additiveSimpleReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivationsFinal) {
        // First e->c activation
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        // Reverse Activation c-> e
        for (int caseNum : caseActivations.keySet()) {
            List<Double> reverseRelevances = reverseMatrix.get(caseNum);
            for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                if (reverseRelevances.get(eId) == 1) {
                    if (activations.get(eId) == null) {
                        activations.put(eId, 1.0);
                    } else {
                        activations.put(eId, activations.get(eId) + 1);
                    }
                }
            }
        }
        // Second e->c activation
        basicActivationSpread(activations, caseActivationsFinal);
    }

    public void cutoffReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivationsFinal, double cutoff) {
        // First e->c activation
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        // Reverse Activation c-> e
        for (int caseNum : caseActivations.keySet()) {
            if (caseActivations.get(caseNum) > cutoff) {
                List<Double> reverseRelevances = reverseMatrix.get(caseNum);
                for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                    if (reverseRelevances.get(eId) == 1) {
                        if (activations.get(eId) == null) {
                            activations.put(eId, 1.0);
                        }
                    }
                }
            }
        }
        // Second e->c activation
        basicActivationSpread(activations, caseActivationsFinal);
    }

    public void discCutoffReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivationsFinal, double cutoff, double discount) {
        // First e->c activation
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        // Reverse Activation c-> e
        for (int caseNum : caseActivations.keySet()) {
            if (caseActivations.get(caseNum) > cutoff) {
                List<Double> reverseRelevances = reverseMatrix.get(caseNum);
                for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                    if (reverseRelevances.get(eId) == 1) {
                        if (activations.get(eId) == null) {
                            activations.put(eId, discount);
                        }
                    }
                }
            }
        }
        // Second e->c activation
        basicActivationSpread(activations, caseActivationsFinal);
    }

    public void additiveCutoffReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivationsFinal, double cutoff) {
        // First e->c activation
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        // Reverse Activation c-> e
        for (int caseNum : caseActivations.keySet()) {
            if (caseActivations.get(caseNum) > cutoff) {
                List<Double> reverseRelevances = reverseMatrix.get(caseNum);
                for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                    if (reverseRelevances.get(eId) == 1) {
                        if (activations.get(eId) == null) {
                            activations.put(eId, 1.0);
                        } else {
                            activations.put(eId, activations.get(eId) + 1);
                        }
                    }
                }
            }
        }
        // Second e->c activation
        basicActivationSpread(activations, caseActivationsFinal);
    }
    public void addDiscCutoffReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivationsFinal, double cutoff, double discount) {
        // First e->c activation
        HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        // Reverse Activation c-> e
        for (int caseNum : caseActivations.keySet()) {
            if (caseActivations.get(caseNum) > cutoff) {
                List<Double> reverseRelevances = reverseMatrix.get(caseNum);
                for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                    if (reverseRelevances.get(eId) == 1) {
                        if (activations.get(eId) == null) {
                            activations.put(eId, discount);
                        } else {
                            activations.put(eId, activations.get(eId) + discount);
                        }
                    }
                }
            }
        }
        // Second e->c activation
        basicActivationSpread(activations, caseActivationsFinal);
    }

    public void ContADCReverseSpread(HashMap<Integer, Double> activations,
            HashMap<Integer, Double> caseActivations, double cutoff, double discount,
            double threshold) {
        // First e->c activation
        // HashMap<Integer, Double> caseActivations = new HashMap<Integer, Double>();
        basicActivationSpread(activations, caseActivations);
        while (discount >= threshold) {
            // Reverse Activation c-> e
            for (int caseNum : caseActivations.keySet()) {
                if (caseActivations.get(caseNum) > cutoff) {
                    List<Double> reverseRelevances = reverseMatrix.get(caseNum);
                    for (int eId = 0; eId < reverseRelevances.size(); ++eId) {
                        if (reverseRelevances.get(eId) == 1) {
                            if (activations.get(eId) == null) {
                                activations.put(eId, discount);
                            } else {
                                activations.put(eId, activations.get(eId) + discount);
                            }
                        }
                    }
                }
            }
            caseActivations.clear();
            // Second e->c activation
            basicActivationSpread(activations, caseActivations);
            // Change discount
            discount = discount * discount;
        }
    }

    private void normalizeCaseActivations(HashMap<Integer, Double> activations) {
        double vectorNorm = 0;
        for (int caseId : activations.keySet()) {
            vectorNorm += activations.get(caseId) * activations.get(caseId);
        }
        vectorNorm = Math.sqrt(vectorNorm);
        for (int caseId : activations.keySet()) {
            activations.put(caseId, activations.get(caseId) / vectorNorm);
        }
    }
}
