import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.Random;

public class FuzzyHelper {

    public static void showFuzzySaw(){
        FuzzyJudgements[][] fuzzyJudgements = (FuzzyJudgements[][]) Helper.generate2DArray(FuzzyJudgements.class, 3, 3, 0, 0);
        FuzzyPreferenzes[] fuzzyPreferenzes = FuzzyHelper.generateFuzzyPreferenzes(3);

        System.out.println("Judgements: ");
        Helper.show2DArray(fuzzyJudgements);
        System.out.println();
        System.out.println("Preferenzes: ");
        Helper.show1DArray(fuzzyPreferenzes);
        System.out.println();
        Double[] scores = FuzzyHelper.fuzzySaw(fuzzyJudgements, fuzzyPreferenzes);
        System.out.println("Ranking: ");
        Helper.show1DArray(scores);
    }

    public static Double[] fuzzySaw(FuzzyJudgements[][] matrix, FuzzyPreferenzes[] weights) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // check if matrix and weights fit
        if (cols != weights.length) {
            throw new IllegalArgumentException("ERROR: Matrix length:" + cols + " | Weights :" + weights.length );
        }
        Double[] scores = new Double[rows];

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

    public static FuzzyPreferenzes[] generateFuzzyPreferenzes(int size) {
        FuzzyPreferenzes[] randomArray = new FuzzyPreferenzes[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            int randomValue = random.nextInt(7);
            randomArray[i] = FuzzyPreferenzes.getPreferenzes(randomValue);
        }

        return randomArray;
    }
}
