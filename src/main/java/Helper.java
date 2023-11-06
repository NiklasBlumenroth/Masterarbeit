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
        show2DArray(aggregatedMatrix);
    }
    public static void showSaw(){
        Integer[][] matrix = (Integer[][]) generate2DArray(Integer.class, 5, 5, 1, 10);
        Double[] weights = (Double[]) generate1DArray(Double.class, 5, 0, 1);

        System.out.println("Matrix:");
        Helper.show2DArray(matrix);
        System.out.println("\nWichtung:");
        Helper.show1DArray(weights);
        Double[] scores = saw(matrix, weights, true);
        System.out.println("\nErgebnisse der Nutzwertanalyse:");
        Helper.show1DArray(scores);
    }

    public static int[] getSortingVectorAndSortWeights(Object[] weights){
        List <Object> unSortedWeightsList = new ArrayList<>();
        unSortedWeightsList.addAll(Arrays.asList(weights));
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

    public static Double[] saw(Object[][] matrix, Object[] weights, boolean show) {
        int[] sortingVector = null;
        if(show){
            System.out.println("\nsawMatrix: ");
            show2DArray(matrix);
            System.out.println("\nsawWeights: ");
            show1DArray(weights);
        }

        Class<?> clazz = matrix[0][0].getClass();
        if (LexJudgements.class.equals(clazz)) {
            //sort weights, matrix and generate sortingVector
            sortingVector = getSortingVectorAndSortWeights(weights);
            sortJudgementsByPreferences(matrix, sortingVector, true);
        }
        if(show){
        System.out.println("\nsortedMatrix: ");
        show2DArray(matrix);
        System.out.println("\nsortedWeights: ");
        show1DArray(weights);
        }

        int rows = matrix.length;
        int cols = matrix[0].length;
        // check if matrix and weights fit
        if (cols != weights.length) {
            throw new IllegalArgumentException("ERROR: Matrix length:" + cols + " | Weights :" + weights.length );
        }
        Double[] scores = new Double[rows];
        String[] lexScores = new String[rows];

        Double[][] sums = new Double[matrix.length][weights.length];
        String[][] lexSums = new String[matrix.length][weights.length];

        Double value;
        // create sum for columns
        for (int i = 0; i < rows; i++) {
            Double sum = 0.0;
            String lexSum = "";
            for (int j = 0; j < cols; j++) {
                if (FuzzyJudgements.class.equals(clazz)) {
                    FuzzyPreferenzes fuzzyPreferenzes = (FuzzyPreferenzes) weights[j];
                    FuzzyJudgements fuzzyJudgements = (FuzzyJudgements) matrix[i][j];
                    value = (fuzzyJudgements.value1 * fuzzyPreferenzes.value1 + fuzzyJudgements.value2 * fuzzyPreferenzes.value2 + fuzzyJudgements.value3 * fuzzyPreferenzes.value3) / 3;
                    sum += value;
                    sums[i][j] = value;
                } else if (Integer.class.equals(clazz)) {
                    sum = sum + (Integer)matrix[i][j] * (Double)weights[j];
                    sums[i][j] = (Integer)matrix[i][j] * (Double)weights[j];
                } else if (LexJudgements.class.equals(clazz)) {
                    lexSum = lexSum + weights[j] + matrix[i][j];
                    lexSums[i][j] = lexSum;
                }
            }
            scores[i] = sum;
            lexScores[i] = lexSum;
        }
        if (LexJudgements.class.equals(clazz)) {
            String[] temp = new String[rows];
            for(int i = 0; i < temp.length; i++){
                temp[i] = lexScores[i];
            }
            Arrays.sort(temp);
            for(int i = 0; i < temp.length; i++){
                scores[i] = temp.length / (getPlacement(temp, lexScores[i]) * 1.0 + 1);
            }
            //unsort matrix, weights, scores
            sortJudgementsByPreferences(matrix, sortingVector, false);
            Object[] newWeights = sort(weights, sortingVector, false);
            for(int i = 0; i < newWeights.length; i++){
                weights[i] = newWeights[i];
            }

        }
//        System.out.println("\nshow SAW");
//        Helper.show2DArray(lexSums);
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

    public static void sortJudgementsByPreferences(Object[][] matrix, int[] sortingVector, boolean sort){
        for(int i = 0; i < matrix.length; i++){
            matrix[i] = sort(matrix[i], sortingVector, sort);
        }
    }

    public static Object[] sort(Object[] array, int[] sortingVector, boolean sort){
        Object[] newArray = new Object[array.length];

        for(int i = 0; i < array.length; i++){
            if(sort){
                //sortedArray[i] = arrayToSort[sortingVector[i]];
                newArray[i] = array[sortingVector[i]];
            }else{
                //restoredArray[sortingVector[i]] = sortedArray[i];
                newArray[sortingVector[i]] = array[i];
            }
        }
        return newArray;
    }

    public static Object[][] generate2DArray(Class<?> clazz, int rows, int columns, int min, int max){
        Object[][] matrix = new Object[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Integer.class.equals(clazz)) {
                    matrix[i][j] = random.nextInt(max - min + 1);

                } else if (FuzzyJudgements.class.equals(clazz)) {
                    matrix[i][j] = FuzzyJudgements.getJudgement(random.nextInt(FuzzyJudgements.values().length));
                } else if (LexJudgements.class.equals(clazz)) {
                    matrix[i][j] = LexJudgements.getJudgement(random.nextInt(LexJudgements.values().length));
                }
            }
        }

        return matrix;
    }

    public static void show2DArray(Object[][] matrix) {
        Object[][] invert = invertArray(matrix);
        for (Object[] objects : invert) {
            for (int i = 0; i < objects.length - 1; i++) {
                System.out.print(objects[i] + " : ");
            }
            System.out.print(objects[objects.length - 1]);
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
        Object[] randomArray = new Object[size];
        Random random = new Random();
        //set values to right array
        for (int j = 0; j < size; j++) {
            if (Double.class.equals(clazz)) {
                randomArray[j] = Math.round((random.nextDouble() * (maxValue - minValue) + minValue) * 10.0) / 10.0;
            } else if (FuzzyPreferenzes.class.equals(clazz)) {
                randomArray[j] = FuzzyPreferenzes.getPreferenzes(random.nextInt(FuzzyPreferenzes.values().length));
            } else if (Integer.class.equals(clazz)) {
                randomArray[j] = random.nextInt(maxValue - minValue + 1) + maxValue;
            } else if (LexPreferenzes.class.equals(clazz)) {
                randomArray[j] = LexPreferenzes.getLexValueById(random.nextInt(LexPreferenzes.values().length));
            }
        }

        return randomArray;
    }
}
