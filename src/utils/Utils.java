package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Utils {

    static class ValueComparator implements Comparator<Integer> {
        HashMap<Integer, Double> base;
        public ValueComparator(HashMap<Integer, Double> base) {
            this.base = base;
        }

        public int compare(Integer a, Integer b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static List<Integer> getTopCases(HashMap<Integer, Double> values, int budget) {
        ValueComparator comparator = new ValueComparator(values);
        TreeMap<Integer,Double> sortedValues = new TreeMap<Integer, Double>(comparator);
        sortedValues.putAll(values);
        List<Integer> results = new ArrayList<Integer>();
        int count = 0;
        for (int key : sortedValues.keySet()) {
            if (count >= budget) {
                break;
            }
            results.add(key);
            count++;
        }
        return results;
    }

    public static void writeStringListToFile(List<String> lines, String outFileName) {
        System.out.println("Writing " + outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName));
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error writing " + outFileName);
            System.out.println(e.getMessage());
        }
    }

    public static void writeDoubleMatrixToFile(List<List<Double>> matrix, String outFileName) {
        System.out.println("Writing " + outFileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName));
            for (List<Double> row : matrix) {
                String rowStr = "";
                for (Double element : row) {
                    rowStr += element.toString() + " ";
                }
                bw.write(rowStr.trim());
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Error writing " + outFileName);
            System.out.println(e.getMessage());
        }
    }

    public static void readDoubleMatrix(List<List<Double>> matrix, String inFileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inFileName));
            String line;
            while ((line = br.readLine()) != null) {
                List<Double> row = new ArrayList<Double>();
                matrix.add(row);
                String[] rowStr = line.split(" ");
                for (String element : rowStr) {
                    row.add(Double.parseDouble(element));
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading " + inFileName);
            System.out.println(e.getMessage());
        }
    }

    public static void readIntegerList(List<Integer> caseClasses, String inFileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inFileName));
            String line;
            while ((line = br.readLine()) != null) {
                caseClasses.add(Integer.parseInt(line));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading " + inFileName);
            System.out.println(e.getMessage());
        }
    }

    public static void readStringList(List<String> array, String inFileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inFileName));
            String line;
            while ((line = br.readLine()) != null) {
                array.add(line);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading " + inFileName);
            System.out.println(e.getMessage());
        }
    }

    public static void readFirstRow(HashMap<Integer, Double> map, String inFileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(inFileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                map.put(Integer.parseInt(parts[0]), 1.0);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error reading " + inFileName);
            System.out.println(e.getMessage());
        }
    }

    public static void matrixTranspose(List<List<Double>> matrix, List<List<Double>> transpose) {
        int numRows = matrix.size();
        int numColumns = matrix.get(0).size();
        for (int i = 0; i < numColumns; ++i) {
            System.out.println(i);
            List<Double> newRow = new ArrayList<Double>();
            transpose.add(newRow);
            for (int j = 0; j < numRows; ++j) {
                newRow.add(matrix.get(j).get(i));
            }
        }
        System.out.println("###################################################");
    }
}
