import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;
import Enums.LexPreferenzes;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;

import static Enums.FuzzyJudgements.*;

public class Helper {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static void main(String[] args) {
        ArrayList<Object>[][] aggregatedMatrix = new ArrayList[][]{
                {
                        new ArrayList<>(){{add(VG);}},
                        new ArrayList<>(){{add(VP);}},
                        new ArrayList<>(){{add(P);}}
                },
                {
                        new ArrayList<>(){{add(G);}},
                        new ArrayList<>(){{add(VG);}},
                        new ArrayList<>(){{add(MP);}}
                },
                {
                        new ArrayList<>(){{add(VP);}},
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(F);}}
                },
                {
                        new ArrayList<>(){{add(VP);}},
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(F);}}
                }
        };
        show2DArray(aggregatedMatrix);
    }
    public static void showSaw(){
        Integer[][] matrix = (Integer[][]) generate2DArray(Integer.class, 5, 5, 1, 10);
        Double[] weights = (Double[]) generate1DArray(Double.class, 5, 0, 1);

        System.out.println("Matrix:");
        Helper.show2DArray(matrix);
        System.out.println("\nWichtung:");
        Helper.show1DArray(weights);
        Double[] scores = saw(matrix, weights);
        System.out.println("\nErgebnisse der Nutzwertanalyse:");
        Helper.show1DArray(scores);
    }

    public static Map<Integer, String> sortByValue(Map<Integer, String> unsortedMap) {
        // Konvertiere die Map in eine Liste von Einträgen
        List<Map.Entry<Integer, String>> entryList = new ArrayList<>(unsortedMap.entrySet());

        // Sortiere die Liste der Einträge nach dem Value (String)
        entryList.sort(Map.Entry.comparingByValue());

        // Erstelle eine neue LinkedHashMap, um die sortierten Einträge beizubehalten
        Map<Integer, String> sortedMap = new LinkedHashMap<>();

        // Füge die sortierten Einträge zur neuen Map hinzu
        for (Map.Entry<Integer, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static Double[] saw(Object[][] matrix, Object[] weights) {
        Map<Integer, String> weightsMap = new HashMap<>();
        Integer counter = 0;

        //put all values in map and use counter as key
        for(int i = 0; i < weights.length; i++){
            weightsMap.put(counter, String.valueOf(weights[i]));
            counter++;
        }

        weightsMap = sortByValue(weightsMap);
        counter = 0;
        for (Map.Entry<Integer, String> entry : weightsMap.entrySet()) {
            weights[counter] = entry.getValue();
            counter++;
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        Class<?> clazz = matrix[0][0].getClass();
        // check if matrix and weights fit
        if (cols != weights.length) {
            throw new IllegalArgumentException("ERROR: Matrix length:" + cols + " | Weights :" + weights.length );
        }
        Double[] scores = new Double[rows];
        String[] lexScores = new String[rows];
        Double[][] sums = new Double[matrix.length][matrix[0].length];
        Double value;
        // create sum for columns
        for (int i = 0; i < rows; i++) {
            Double sum = 0.0;
            String lexSum = "";
            for (int j = 0; j < cols; j++) {
                if (Double.class.equals(clazz)) {

                } else if (FuzzyJudgements.class.equals(clazz)) {
                    FuzzyPreferenzes fuzzyPreferenzes = (FuzzyPreferenzes) weights[j];
                    FuzzyJudgements fuzzyJudgements = (FuzzyJudgements) matrix[i][j];
                    value = (fuzzyJudgements.value1 * fuzzyPreferenzes.value1 + fuzzyJudgements.value2 * fuzzyPreferenzes.value2 + fuzzyJudgements.value3 * fuzzyPreferenzes.value3) / 3;
                    sum += value;
                    sums[i][j] = value;
                } else if (Integer.class.equals(clazz)) {
                    sum = sum + (Integer)matrix[i][j] * (Double)weights[j];
                    sums[i][j] = (Integer)matrix[i][j] * (Double)weights[j];
                } else if (LexJudgements.class.equals(clazz)) {
                    lexSum = lexSum + matrix[i][j] + weights[j];
            }

            }
            scores[i] = sum;
            lexScores[i] = lexSum;
        }
//        System.out.println("\nshow SAW");
//        Helper.show2DArray(sums);
        return scores;
    }

    public static void showRank(Map<Object, Map<Integer, Double>>[][] matrix, Integer rank){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(" {");
                for (Map.Entry<Object, Map<Integer, Double>> entry : matrix[j][i].entrySet()) {
                    System.out.print(" ");
                    for (Map.Entry<Integer, Double> rankingEntry : entry.getValue().entrySet()) {
                        if(Objects.equals(rankingEntry.getKey(), rank)){
                            System.out.print(entry.getKey() + "=" + rankingEntry.getValue());
                        }
                    }
                }
                System.out.print(" } ");
            }
            System.out.println();
        }
    }

    public static Object[][] generate2DArray(Class<?> clazz, int rows, int columns, int min, int max){
        Object[][] newObject = null;
        Integer[][] matrix = new Integer[rows][columns];
        Random random = new Random();
        FuzzyJudgements[][] fuzzyMatrix = new FuzzyJudgements[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Integer.class.equals(clazz)) {
                    matrix[i][j] = random.nextInt(max - min + 1);

                } else if (FuzzyJudgements.class.equals(clazz)) {
                    fuzzyMatrix[i][j] = FuzzyJudgements.getJudgement(random.nextInt(FuzzyJudgements.values().length));
                }
            }
        }

        if (Integer.class.equals(clazz)) {
            newObject = matrix;

        } else if (FuzzyJudgements.class.equals(clazz)) {
            newObject = fuzzyMatrix;
        }

        return newObject;
    }

    public static void show2DArray(Object[][] matrix) {
        Object[][] invert = invertArray(matrix);
        for (Object[] objects : invert) {
            for (Object object : objects) {
                System.out.print(object + " : ");
            }
            System.out.println();
        }
    }

    public static void showAcceptabilityIndices(Object[][] matrix) {
        for (Object[] objects : matrix) {
            for (Object object : objects) {
                System.out.print(object + " : ");
            }
            System.out.println();
        }
    }

    public static Object[][] invertArray(Object[][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        Object[][] invertedArray = new Object[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (j < array[i].length) {
                    invertedArray[j][i] = array[i][j];
                }
            }
        }

        return invertedArray;
    }

    public static void show1DArray(Object[] array) {
        System.out.print(array[0]);

        for (int i = 1; i < array.length; i++) {
            System.out.print(" | " + array[i]);
        }
        System.out.println();
    }


    public static Object[] generate1DArray(Class<?> clazz, int size, int minValue, int maxValue) {
        Double[] doubleMatrix = new Double[size];
        Integer[] integerMatrix = new Integer[size];
        Object[] randomArray = null;
        Random random = new Random();
        FuzzyPreferenzes[] fuzzyMatrix = new FuzzyPreferenzes[size];
        //set values to right array
        for (int j = 0; j < size; j++) {
            if (Double.class.equals(clazz)) {
                doubleMatrix[j] = Math.round((random.nextDouble() * (maxValue - minValue) + minValue) * 10.0) / 10.0;
            } else if (FuzzyPreferenzes.class.equals(clazz)) {
                fuzzyMatrix[j] = FuzzyPreferenzes.getPreferenzes(random.nextInt(FuzzyPreferenzes.values().length));
            } else if (Integer.class.equals(clazz)) {
                integerMatrix[j] = random.nextInt(maxValue - minValue + 1) + maxValue;
            }
        }
        //set right array for return
        for (int i = 0; i < size; i++) {
            if (Integer.class.equals(clazz)) {
                randomArray = integerMatrix;
            } else if (FuzzyPreferenzes.class.equals(clazz)) {
                randomArray = fuzzyMatrix;
            } else if (Double.class.equals(clazz)) {
                randomArray = doubleMatrix;
            }
        }

        return randomArray;
    }
}
