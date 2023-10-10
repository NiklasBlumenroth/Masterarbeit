public class test {
    public static void main(String[] args) {
        int[] a = {1,2};
        int[] b = {3,4,5};
        int[] c = {6,7,8,9};
        int[][] test = new int[3][];
        test[0] = a;
        test[1] = b;
        test[2] = c;

        int[][] arrays = {
                {1, 2},
                {3, 4},
                {5, 6, 7}
        };

        int[][] result = cartesianProduct(arrays);

        // Ausgabe des kartesischen Produkts
        for (int[] product : result) {
            for (int num : product) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    public static int[][] cartesianProduct(int[][] arrays) {
        int n = arrays.length;
        int totalProducts = 1;
        for (int i = 0; i < n; i++) {
            totalProducts *= arrays[i].length;
        }

        int[][] result = new int[totalProducts][n];

        int[] indices = new int[n];

        for (int i = 0; i < totalProducts; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = arrays[j][indices[j]];
            }

            // Inkrementiere die Indizes
            for (int j = n - 1; j >= 0; j--) {
                if (indices[j] < arrays[j].length - 1) {
                    indices[j]++;
                    break;
                } else {
                    indices[j] = 0;
                }
            }
        }

        return result;
    }
}
