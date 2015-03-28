package network;

import java.util.List;

public class TextNetwork {
    Configuration config;
    Entity entities;

    public List<List<Double>> relevanceMatrix;
    public List<List<Double>> similarityMatrix;
    public List<List<Double>> caseCaseMatrix;
    public List<List<Double>> reverseMatrix;
    public List<String> entityLookup;
    public List<String> caseLookup;

    public TextNetwork(Configuration config) {
        this.config = config;
        this.entities = new Entity(config.entityLookupFileName);
    }

    public void populateLookupsAndMatrices() {
        // TODO
    };

    public List<S>
}
