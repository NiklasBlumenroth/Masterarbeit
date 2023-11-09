import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;
import Enums.LexPreferenzes;

import java.text.DecimalFormat;
import java.util.*;

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
    }

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
        if(show){
            showMatrixAndWeights(matrix, weights);
            //System.out.println("\nsawMatrix: ");
            //show2DArray(matrix);
            //System.out.println("\nsawWeights: ");
            //show1DArray(weights);
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        // check if matrix and weights fit
        if (cols != weights.length) {
            throw new IllegalArgumentException("ERROR: Matrix length:" + cols + " | Weights :" + weights.length );
        }
        double[] scores = new double[rows];
        String[] lexScores = new String[rows];

        double[][] sums = new double[matrix.length][matrix[0].length];

        double value;
        // create sum for columns
        if(lex){
            for (int i = 0; i < rows; i++) {
                double sum = 0.0;
                String lexSum = "";
                for (int j = 0; j < weights.length; j++) {
                    int value1 = weights[j];
                    int value2 = matrix[i][value1];
                    LexJudgements judgement = LexJudgements.getJudgement(value2);
                    int value3 = weights[j];
                    LexPreferenzes preferenze = LexPreferenzes.getLexValueById(value3);
                    lexSum = lexSum + preferenze + judgement;
                }
                scores[i] = sum;
                lexScores[i] = lexSum;
            }
        }else {
            for (int i = 0; i < rows; i++) {
                double sum = 0.0;
                for (int j = 0; j < cols; j++) {
                    FuzzyPreferenzes fuzzyPreferenzes =  FuzzyPreferenzes.getPreferenzes(weights[j]);
                    FuzzyJudgements fuzzyJudgements = FuzzyJudgements.getJudgement(matrix[i][j]);
                    value = (fuzzyJudgements.value1 * fuzzyPreferenzes.value1 + fuzzyJudgements.value2 * fuzzyPreferenzes.value2 + fuzzyJudgements.value3 * fuzzyPreferenzes.value3) / 3;
                    sum += value;
                    sums[i][j] = value;
                }
                scores[i] = sum;
            }
        }


        if (lex) {
            String[] temp = lexScores.clone();
            Arrays.sort(temp);
            for(int i = 0; i < lexScores.length; i++){
                scores[i] = lexScores.length / (getPlacement(temp, lexScores[i]) * 1.0 + 1);
            }
            if(show){
                show1DArray(scores);
            }
        }
        return scores;
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

    public static void show3DArray(int[][][] matrix) {
        int[][][] invert = invertArray3D(matrix);
        for (int[][] objects : invert) {
            for (int i = 0; i < objects.length - 1; i++) {
                System.out.print(Arrays.toString(objects[i]) + " : ");
            }
            System.out.print(Arrays.toString(objects[objects.length - 1]));
            System.out.println();
        }
    }

    public static void show3DArray(double[][][] matrix) {
        double[][][] invert = invertArray3D(matrix);
        for (double[][] objects : invert) {
            for (int i = 0; i < objects.length - 1; i++) {
                System.out.print(Arrays.toString(objects[i]) + " : ");
            }
            System.out.print(Arrays.toString(objects[objects.length - 1]));
            System.out.println();
        }
    }

    public static void show2DArray(int[][] matrix) {
        for (int[] objects : matrix) {
            System.out.print("[");
            for (int i = 0; i < objects.length - 1; i++) {
                System.out.print(objects[i] + ", ");
            }
            System.out.print(objects[objects.length - 1]);
            System.out.print("]");
        }
    }
    public static void showMatrixAndWeights(int[][] matrix, int[] weights) {
        int[][] invert = invertArray(matrix);
        System.out.println("\n");
        for (int j = 0; j < invert.length; j++) {
            for (int i = 0; i < invert[j].length - 1; i++) {
                System.out.print(invert[j][i] + " : ");
            }
            System.out.print(invert[j][invert[j].length - 1]);
            System.out.print(" | " + weights[j]);
            System.out.println();
        }
        System.out.println("\n");
    }

    public static void show2DArray(double[][] matrix) {
//        int[][] invert = invertArray(matrix);
        for (double[] objects : matrix) {
            for (int i = 0; i < objects.length - 1; i++) {
                System.out.print(objects[i] + " : ");
            }
            System.out.print(objects[objects.length - 1]);
            System.out.println();
        }
    }

    public static void showAcceptabilityIndices(double[][] matrix) {
        for (double[] objects : matrix) {
            for (double object : objects) {
                System.out.print(object + " : ");
            }
            System.out.println();
        }
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
        System.out.print(array[0]);

        for (int i = 1; i < array.length; i++) {
            System.out.print(" | " + array[i]);
        }
        System.out.println();
    }

    public static void show1DArray(double[] array) {
        System.out.print(array[0]);

        for (int i = 1; i < array.length; i++) {
            System.out.print(" | " + array[i]);
        }
        System.out.println();
    }

    public static void fill3dArrayWithNegOne(int[][][] array){
        for(int[][] array2D : array){
            for (int[] array1D : array2D){
                Arrays.fill(array1D, -1);
            }
        }
    }

    public static void fill2dArrayWithNegOne(int[][] array){
        for(int[] array1D : array){
            Arrays.fill(array1D, -1);
        }
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
