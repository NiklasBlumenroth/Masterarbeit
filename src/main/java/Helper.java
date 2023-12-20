import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;
import Enums.LexPreferenzes;

import java.text.DecimalFormat;
import java.util.*;

import static Enums.FuzzyJudgements.*;

public class Helper {
    private static final DecimalFormat df = new DecimalFormat("0.00");


    public static int[] getSortingVectorAndSortWeights(int[] weights){
        List <Integer> unSortedWeightsList = new ArrayList<>();
        for(int integer : weights){
            unSortedWeightsList.add(integer);
        }
        Arrays.sort(weights);

        Map<Object, Integer> indexMapping = new HashMap<>();
        for (int i = 0; i < weights.length; i++) {
            indexMapping.put(unSortedWeightsList.get(i), i);
        }

        int[] sortingVector = new int[weights.length];
        for (int i = 0; i < weights.length; i++) {
            sortingVector[i] = indexMapping.get(weights[i]);
        }

        return sortingVector;
    }

    public static double[] decisionMethod(int[][] matrix, int[] weights, boolean show, boolean lex) {
        if(show && lex){
            showMatrixAndWeights(matrix, weights);
        }else if (show){
            String line = "\nMatrix";
            show2DArray(matrix);
            line += "\nWeigths";
            show1DArray(weights);
        }

        int alt = matrix.length;
        int crit = matrix[0].length;
        // check if matrix and weights fit
        if (crit != weights.length) {
            throw new IllegalArgumentException("ERROR: Matrix length:" + crit + " | Weights :" + weights.length );
        }
        double[] scores = new double[alt];
        String[] lexScores = new String[alt];

        double[][] sums = new double[matrix.length][matrix[0].length];
        double value;

//         create sum for columns
        for (int i = 0; i < alt; i++) {
            Double sum = 0.0;
            for (int j = 0; j < crit; j++) {
                sum = sum + (matrix[i][j] * weights[j]);
                sums[i][j] = matrix[i][j] * weights[j];
            }
            scores[i] = sum;
        }


//        if(lex){
//            int[] weightsOrder = MonteCarloHelper.getOrder(MonteCarloHelper.getOrder(weights));
//            for (int i = 0; i < alt; i++) {
//                double sum = 0.0;
//                String lexSum = "";
//                for (int j = 0; j < weights.length; j++) {
//                    int index = getIndex(weightsOrder, j);
//                    int value2 = matrix[i][index];
//                    LexJudgements judgement = LexJudgements.getJudgement(value2);
//                    LexPreferenzes preferenze = LexPreferenzes.getLexValueById(weights[index]);
//                    lexSum = lexSum + preferenze + judgement;
//                }
//                scores[i] = sum;
//                lexScores[i] = lexSum;
//            }
//        }else {
//            for (int i = 0; i < alt; i++) {
//                double sum = 0.0;
//                for (int j = 0; j < crit; j++) {
//                    FuzzyPreferenzes fuzzyPreferenzes =  FuzzyPreferenzes.getPreferenzes(weights[j]);
//                    FuzzyJudgements fuzzyJudgements = FuzzyJudgements.getJudgement(matrix[i][j]);
//                    value = (fuzzyJudgements.value1 * fuzzyPreferenzes.value1 + fuzzyJudgements.value2 * fuzzyPreferenzes.value2 + fuzzyJudgements.value3 * fuzzyPreferenzes.value3) / 3;
//
//                    sum += value;
//                    sums[i][j] = value;
//                }
//                scores[i] = sum;
//            }
//        }


        if (lex) {
            String[] temp = lexScores.clone();
            Arrays.sort(temp);
            for(int i = 0; i < lexScores.length; i++){
                scores[i] = lexScores.length / (getPlacement(temp, lexScores[i]) * 1.0 + 1);
            }
        }
        if(show){
            Helper.show1DArray(scores);
        }
        return scores;
    }

    public static int getIndex(int[] array, int number){
        for(int i = 0; i < array.length; i++){
            if(array[i]==number) {
                return i;
            }
        }
        return -1;
    }

    public static int getPlacement(String[] temp, String object){
        for(int i = 0; i < temp.length; i++){
            if(object.equals(temp[i])){
                return i;
            }
        }
        return -1;
    }

    public static int[][] generate2DArray(int rows, int columns, int min, int max){
        int[][] matrix = new int[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = random.nextInt(max - min + 1);
            }
        }

        return matrix;
    }

    public static Map<Object, Double>[][] invertMatrix(Map<Object, Double>[][] matrix){
        Map<Object, Double>[][] invertedMatrix = new Map[matrix[0].length][matrix.length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                invertedMatrix[j][i] = matrix[i][j];
            }
        }
        return invertedMatrix;
    }
    public static void showEntropyMatrix(Map<Object, Double>[][] matrix){
        String line = "";
        Map<Object, Double>[][] invertedMatrix = invertMatrix(matrix);
        for (Map<Object, Double>[] maps : invertedMatrix) {
            for (Map<Object, Double> map : maps) {
                line += map.toString();
            }
            line += "\n";
        }

        Nutzwertanalyse.writeTxt(line);
    }

    public static void showEntropyWeights(Map<Object, Double>[] weights){
        String line = "";
        for(int j = 0; j < weights.length; j++){
            line += weights[j].toString();
        }
        line += "\n";
        Nutzwertanalyse.writeTxt(line);
    }
    public static void show3DArray(int[][][] matrix) {
        String line = "";
        int[][][] invert = invertArray3D(matrix);
        for (int[][] objects : invert) {
            for (int i = 0; i < objects.length - 1; i++) {
                line += Arrays.toString(objects[i]) + " : ";
            }
            line += Arrays.toString(objects[objects.length - 1]) + "\n";
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static void show3DArray(double[][][] matrix) {
        String line = "";
        double[][][] invert = invertArray3D(matrix);
        for (double[][] objects : invert) {
            for (int i = 0; i < objects.length - 1; i++) {
                line += Arrays.toString(objects[i]) + " ";
            }
            line += Arrays.toString(objects[objects.length - 1]) + "\n";
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static void show2DArray(int[][] matrix) {
        int[][] invert = invertArray(matrix);
        String line = "";
        for (int[] objects : invert) {
            line += "[";
            for (int i = 0; i < objects.length - 1; i++) {
                line += objects[i] + ", ";
            }
            line += String.valueOf(objects[objects.length - 1]);
            line += "]\n";
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static void showAggregatedWeightsArray(int[][] matrix) {
        String line = "";
        for (int[] objects : matrix) {
            line += "[";
            for (int i = 0; i < objects.length - 1; i++) {
                line += objects[i] + ", ";
            }
            line += String.valueOf(objects[objects.length - 1]);
            line += "]\n";
        }
        Nutzwertanalyse.writeTxt(line);
    }
    public static void showMatrixAndWeights(int[][] matrix, int[] weights) {
        int[][] invert = invertArray(matrix);
        String line = "Matrix | Weights\n";
        for (int j = 0; j < invert.length; j++) {
            for (int i = 0; i < invert[j].length - 1; i++) {
                line += invert[j][i] + " : ";
            }
            line += String.valueOf(invert[j][invert[j].length - 1]);
            line += " | " + weights[j] + "\n";
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static void show2DArray(double[][] matrix) {
        String line = "";
//        int[][] invert = invertArray(matrix);
        for (double[] objects : matrix) {
            line += Arrays.toString(objects);
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static void showAcceptabilityIndices(double[][] matrix) {
        String line = "";
        for (double[] objects : matrix) {
            for (double object : objects) {
                line += object + " : ";
            }
            line += "\n";
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static int[][] invertArray(int[][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        int[][] invertedArray = new int[numCols][numRows];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (j < array[i].length) {
                    invertedArray[j][i] = (int)array[i][j];
                }
            }
        }

        return invertedArray;
    }

    public static double[][][] invertArray3D(double[][][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        double[][][] invertedArray = new double[numCols][numRows][];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (j < array[i].length) {
                    invertedArray[j][i] = array[i][j];
                }
            }
        }

        return invertedArray;
    }

    public static int[][][] invertArray3D(int[][][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        int[][][] invertedArray = new int[numCols][numRows][];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (j < array[i].length) {
                    invertedArray[j][i] = array[i][j];
                }
            }
        }

        return invertedArray;
    }

    public static void show1DArray(int[] array) {
        String line = String.valueOf(array[0]);

        for (int i = 1; i < array.length; i++) {
            line += " | " + array[i];
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static void show1DArray(double[] array) {
        String line = String.valueOf(array[0]);

        for (int i = 1; i < array.length; i++) {
            line += " | " + array[i];
        }
        Nutzwertanalyse.writeTxt(line);
    }

    public static int[] generate1DArray( int size, int minValue, int maxValue) {
        int[] randomArray = new int[size];
        Random random = new Random();
        for (int j = 0; j < size; j++) {
            randomArray[j] = random.nextInt(maxValue - minValue + 1);
        }

        return randomArray;
    }
}
