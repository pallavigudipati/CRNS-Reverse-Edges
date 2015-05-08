package network;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.GlobalVariables;
import utils.Utils;

public class Experiments {

    public static void main(String[] args) {
        /*
        String root = "/home/pallavi/Acads/sem-8/MBR/Project/CRNS-Reverse-Edges/datasets/relPol";
        String relevanceFileName = root + "/matrices/relevanceMatrix.txt";
        String caseClassFileName = root + "/matrices/classList.txt";
        String caseWordCounts = root + "/matrices/caseWordCounts.txt";
        Configuration config = new Configuration(true, false, false, relevanceFileName, null, null, null,
                caseClassFileName, caseWordCounts, null, null, GlobalVariables.SpreadingMode.CONTADCREVERSE, 11);
        TextNetwork basicNetwork = new TextNetwork(config);
        basicNetwork.populateLookupsAndMatrices();
        String hardware = root + "/matrices_test/politics_test";
        String religion = root + "/matrices_test/religion_test";
        // Hardware
        double hAccuracy = 0;
        File[] hFiles = new File(hardware).listFiles();
        for (File hFile : hFiles) {
            double acc = basicNetwork.evaluate(hFile.getAbsolutePath(), 0);
            System.out.println(hFile.getName() + " " + acc);
            hAccuracy += acc;
        }
        hAccuracy /= hFiles.length;

        // Religion
        double rAccuracy = 0;
        File[] rFiles = new File(religion).listFiles();
        for (File rFile : rFiles) {
            double acc = basicNetwork.evaluate(rFile.getAbsolutePath(), 1);
            System.out.println(rFile.getName() + " " + acc);
            rAccuracy += acc;
        }
        rAccuracy /= rFiles.length;
        System.out.println("Politics Acc " + hAccuracy);
        System.out.println("Religion Acc " + rAccuracy);
        */
        String root = "/home/pallavi/Acads/sem-8/MBR/Project/www.cs.washington.edu/research/imagedatabase/groundtruth/cherriesVsGreenLake";
        String relevanceFileName = root + "/train/relevance.txt";
        String caseClassFileName = root + "/train/classList.txt";
        Configuration config = new Configuration(true, false, false, relevanceFileName, null, null, null,
                caseClassFileName, null, null, null, GlobalVariables.SpreadingMode.DISCCUTOFFREVERSE, 11);
        TextNetwork basicNetwork = new TextNetwork(config);
        basicNetwork.populateLookupsAndMatrices();
        String testRelevanceFileName = root + "/test/relevance.txt";
        String testClassListFileName = root + "/test/classList.txt";
        double class1Acc = 0.0;
        double class2Acc = 0.0;
        int class1Images = 0;
        int class2Images = 0;
        List<List<Double>> testRelevance = new ArrayList<List<Double>>();
        Utils.readDoubleMatrix(testRelevance, testRelevanceFileName);
        List<Integer> testClassList = new ArrayList<Integer>();
        Utils.readIntegerList(testClassList, testClassListFileName);

        for (int testCase = 0; testCase < testClassList.size(); ++testCase) {
            int testClass = testClassList.get(testCase);
            double acc = basicNetwork.evaluateImages(testRelevance.get(testCase), testClass);
            // System.out.println(hFile.getName() + " " + acc);
            if (testClass == 1) {
                class1Acc += acc;
                class1Images += 1;
            } else {
                class2Acc += acc;
                class2Images += 1;
            }
        }
        class1Acc /= class1Images;
        class2Acc /= class2Images;

        System.out.println("Class1 Acc " + class1Acc);
        System.out.println("Class2 Acc " + class2Acc);
    }
}
