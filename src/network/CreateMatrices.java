package network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import utils.Utils;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class CreateMatrices {
    public List<String> cases;
    public List<String> entities;
    public List<String> classes;
    // Case -> [Entity -> Count]
    public List<HashMap<Integer, Integer>> frequencyList;
    public List<List<Double>> relevanceMatrix;
    public List<List<Double>> similarityMatrix; // edu.cmu.lti.ws4j.impl.WuPalmer
    public List<Integer> classList; // case -> class
    public List<String> caseWordCounts;

    public CreateMatrices() {
        cases = new ArrayList<String>();
        entities = new ArrayList<String>();
        classes = new ArrayList<String>();
        frequencyList = new ArrayList<HashMap<Integer, Integer>>();
        relevanceMatrix = new ArrayList<List<Double>>();
        similarityMatrix = new ArrayList<List<Double>>();
        classList = new ArrayList<Integer>();
        caseWordCounts = new ArrayList<String>();
    }

    public static void main(String[] args) {
        /*
        List<String> folderPaths = new ArrayList<String>();
        folderPaths.add("/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/atheism_train");
        folderPaths.add("/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/religion_train");
        CreateMatrices creator = new CreateMatrices();
        creator.createTrainData(folderPaths, "/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/matrices");
        */
        
        // Test Data
        List<String> folderPaths = new ArrayList<String>();
        folderPaths.add("/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/atheism_test");
        folderPaths.add("/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/religion_test");
        String entityFile = "/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/matrices/entities.txt";
        CreateMatrices creator = new CreateMatrices();
        creator.createTestData(folderPaths, entityFile, "/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relAtheism/matrices_test");
    }

    public void createTrainData(List<String> folderPaths, String outFolder) {
        createClasses(folderPaths);
        System.out.println("Created Classes");
        createCaseAndClassLists();
        System.out.println("Created case list");
        createFrequencyList();
        System.out.println("Created frequency list");
        // createSimilarityMatrix();
        // System.out.println("Created similarity matrix");
        createRelevanceMatrix();
        System.out.println("Created relevance matrix");
        writeToFiles(outFolder);
    }

    public void createClasses(List<String> folderPaths) {
        for (String folderPath : folderPaths) {
            classes.add(folderPath);
        }
    }

    public void createCaseAndClassLists() {
        int classNum = 0;
        for (String folderPath : classes) {
            File[] files = new File(folderPath).listFiles();
            for (File file : files) {
                cases.add(file.getAbsolutePath());
                classList.add(classNum);
            }
            classNum += 1;
        }
    }

    public void createFrequencyList() {
        for (String file : cases) {
            HashMap<Integer, Integer> frequencyMap = new HashMap<Integer, Integer>();
            frequencyList.add(frequencyMap);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                int wordCount = 0;
                // Assuming each line contains a word.
                while ((line = br.readLine()) != null) {
                    if (!entities.contains(line)) {
                        entities.add(line);
                    }
                    int entityIndex = entities.indexOf(line);
                    if (!frequencyMap.containsKey(entityIndex)) {
                        frequencyMap.put(entityIndex, 0);
                    }
                    frequencyMap.put(entityIndex, frequencyMap.get(entityIndex) + 1);
                    wordCount++;
                }
                caseWordCounts.add(Integer.toString(wordCount));
                br.close();
            } catch (Exception e) {
                System.out.println("Problem while reading " + file);
                System.out.println(e.getMessage());
            }
        }
    }

    // entity x case
    public void createRelevanceMatrix() {
        for (int entityIndex = 0; entityIndex < entities.size(); ++entityIndex) {
            List<Double> relevances = new ArrayList<Double>();
            relevanceMatrix.add(relevances);
            for (HashMap<Integer, Integer> frequencies : frequencyList) {
                // Frequencies are positive.
                double relevance = frequencies.containsKey(entityIndex) ? 1 : 0;
                relevances.add(relevance);
            }
        }
    }

    public void createSimilarityMatrix() {
        ILexicalDatabase db = new NictWordNet();
        RelatednessCalculator rc = new WuPalmer(db);
        WS4JConfiguration.getInstance().setMFS(true);
        for (int entity1 = 0; entity1 < entities.size(); ++entity1) {
            List<Double> similarities = new ArrayList<Double>();
            similarityMatrix.add(similarities);
            for (int entity2 = 0; entity2 < entities.size(); ++entity2) {
                similarities.add(rc.calcRelatednessOfWords(
                        entities.get(entity1), entities.get(entity2)));
            }
        }
    }

    public void writeToFiles(String outFolder) {
        // WordCounts
        Utils.writeStringListToFile(caseWordCounts, outFolder + "/" + "caseWordCounts.txt");

        // Cases
        Utils.writeStringListToFile(cases, outFolder + "/" + "cases.txt");

        // Entities
        Utils.writeStringListToFile(entities, outFolder + "/" + "entities.txt");

        // Frequency List : Case Entity Frequency
        int caseNum = 0;
        List<String> frequencyStr = new ArrayList<String>();
        for (HashMap<Integer, Integer> caseMap : frequencyList) {
            for (Entry<Integer, Integer> entity : caseMap.entrySet()) {
                frequencyStr.add(caseNum + " " + entity.getKey() + " " + entity.getValue());
            }
            caseNum += 1;
        }
        Utils.writeStringListToFile(frequencyStr, outFolder + "/" + "frequencyList.txt");

        // Similarity Matrix
        // Utils.writeDoubleMatrixToFile(similarityMatrix, outFolder + "/" + "similarityMatrix.txt");

        // Relevance Matrix
        Utils.writeDoubleMatrixToFile(relevanceMatrix, outFolder + "/" + "relevanceMatrix.txt");

        // Classes
        Utils.writeStringListToFile(classes, outFolder + "/" + "classes.txt");

        // Class List
        List<String> classStrList = new ArrayList<String>();
        for (Integer classNum : classList) {
            classStrList.add(classNum.toString());
        }
        Utils.writeStringListToFile(classStrList, outFolder + "/" + "classList.txt");
    }

    // TESTING
    public void createTestData(List<String> folderPaths, String entityFile, String outFolder) {
        List<String> entitiesList = new ArrayList<String>();
        Utils.readStringList(entitiesList, entityFile);
        for (String folderPath : folderPaths) {
            File[] files = new File(folderPath).listFiles();
            for (File file : files) {
                HashMap<Integer, Integer> frequencies = new HashMap<Integer, Integer>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    // Assuming each line contains a word.
                    while ((line = br.readLine()) != null) {
                        int entityIndex = entitiesList.indexOf(line);
                        if (entityIndex != -1) {
                            if (!frequencies.containsKey(entityIndex)) {
                                frequencies.put(entityIndex, 0);
                            }
                            frequencies.put(entityIndex, frequencies.get(entityIndex) + 1);
                        }
                    }
                    br.close();
                } catch (Exception e) {
                    System.out.println("Error in reading " + file.getAbsolutePath());
                    System.out.println(e.getMessage());
                }
                // Write it to a file.
                List<String> frequencyStr = new ArrayList<String>();
                for (Entry<Integer, Integer> entity : frequencies.entrySet()) {
                        frequencyStr.add(entity.getKey() + " " + entity.getValue());
                }
                Utils.writeStringListToFile(frequencyStr, outFolder + "/" 
                        + new File(folderPath).getName() + "/" + file.getName());
            }
        }
    }
}
