import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.Random;

public class FuzzyHelper {
    public static double[] fuzzySaw(FuzzyJudgements[][] matrix, FuzzyPreferenzes[] weights) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // check if matrix and weights fit
        if (cols != weights.length) {
            throw new IllegalArgumentException("ERROR: Matrix length:" + cols + " | Weights :" + weights.length );
        }
        double[] scores = new double[rows];

        // create sum for columns
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += (matrix[i][j].value1 * weights[j].value1 + matrix[i][j].value2 * weights[j].value2 + matrix[i][j].value3 * weights[j].value3) / 3;
            }
            scores[i] = sum;
        }
        return scores;
    }

    public static FuzzyJudgements[][] generateFuzzyJudgementMatrix(int rows, int columns){
        FuzzyJudgements[][] matrix = new FuzzyJudgements[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = FuzzyJudgements.getJudgement(random.nextInt(7));
            }
        }
        return matrix;
    }

    public static FuzzyPreferenzes[] generateFuzzyPreferenzes(int size) {
        FuzzyPreferenzes[] randomArray = new FuzzyPreferenzes[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            int randomValue = random.nextInt(7);
            randomArray[i] = FuzzyPreferenzes.getPreferenzes(randomValue);
        }

        return randomArray;
    }

    public static void show1DArray(FuzzyPreferenzes[] array) {
        for (FuzzyPreferenzes elem : array) {
            System.out.print(elem + "(" + elem.value1 + "|" + elem.value2 + "|" + elem.value3 + ") ");
        }
        System.out.println();
    }

    public static void show2DArray(FuzzyJudgements[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(matrix[j][i] + "(" + matrix[j][i].value1 + "|" + matrix[j][i].value2 + "|" + matrix[j][i].value3 + ") ");;
            }
            System.out.println();
        }
    }
}
