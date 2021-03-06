package network;

public class Configuration {
    public boolean includeReverseEdges = false;
    public boolean includeCaseCaseEdges = false;
    public boolean disadvLongFiles = false;
    public String relevanceFileName;
    public String similarityFileName;
    public String caseCaseFileName;
    public String reverseFileName;
    public String caseClassFileName;
    public String entityLookupFileName;
    public String caseLookupFileName;
    public String caseWordCountsFileName;

    public int spreadingMode;
    public int budget; // Number of cases to be retrieved.

    public Configuration(boolean reverseEdges, boolean caseCaseEdges, boolean longFiles, String relevanceFileName,
            String similarityFileName, String caseCaseFileName, String reverseFileName, 
            String caseClassFileName, String entityLookupFileName, String caseLookupFileName,
            String caseWordCounts, int spreadingMode, int budget) {
        this.includeReverseEdges = reverseEdges;
        this.includeCaseCaseEdges = caseCaseEdges;
        this.disadvLongFiles = longFiles;
        this.relevanceFileName = relevanceFileName;
        this.similarityFileName = similarityFileName;
        this.caseCaseFileName = caseCaseFileName;
        this.reverseFileName = reverseFileName;
        this.caseClassFileName = caseClassFileName;
        this.entityLookupFileName = entityLookupFileName;
        this.caseLookupFileName = caseLookupFileName;
        this.caseWordCountsFileName = caseWordCounts;
        this.spreadingMode = spreadingMode;
        this.budget = budget;
    }
}
