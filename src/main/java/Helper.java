import java.util.Random;

public class Helper {
    public static double[] saw(int[][] matrix, double[] weights) {
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
                sum += matrix[i][j] * weights[j];
            }
            scores[i] = sum;
        }
        return scores;
    }

    public static int[][] generateInteger2DArray(int rows, int columns, int x, int y) {
        int[][] matrix = new int[rows][columns];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = random.nextInt(y - x + 1) + x;
            }
        }

        return matrix;
    }

    public static void show2DArray(int[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(matrix[j][i] + " ");;
            }
            System.out.println();
        }
    }

    public static void show1DArray(double[] array) {
        for (double elem : array) {
            System.out.print(elem + " | ");
        }
        System.out.println();
    }


    public static double[] generateWeigths(int size, double minValue, double maxValue) {

        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue > maxValue");
        }

        double[] randomArray = new double[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            double randomValue = Math.round((random.nextDouble() * (maxValue - minValue) + minValue) * 10.0) / 10.0;
            randomArray[i] = randomValue;
        }

        return randomArray;
    }
}
