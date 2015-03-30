package network;

public class Configuration {
    public boolean includeReverseEdges = false;
    public boolean includeCaseCaseEdges = false;
    public String relevanceFileName;
    public String similarityFileName;
    public String caseCaseFileName;
    public String reverseFileName;
    public String entityLookupFileName;
    public String caseLookupFileName;

    public int spreadingMode;
    public int budget; // Number of cases to be retrieved.

    public Configuration(boolean reverseEdges, boolean caseCaseEdges, String relevanceFileName,
            String similarityFileName, String caseCaseFileName, String reverseFileName,
            String entityLookupFileName, String caseLookupFileName, int spreadingMode, int budget) {
        this.includeReverseEdges = reverseEdges;
        this.includeCaseCaseEdges = caseCaseEdges;
        this.relevanceFileName = relevanceFileName;
        this.similarityFileName = similarityFileName;
        this.caseCaseFileName = caseCaseFileName;
        this.reverseFileName = reverseFileName;
        this.entityLookupFileName = entityLookupFileName;
        this.caseLookupFileName = caseLookupFileName;
        this.spreadingMode = spreadingMode;
        this.budget = budget;
    }
}
