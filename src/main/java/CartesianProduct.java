import java.util.ArrayList;
import java.util.List;

public class CartesianProduct {
    public static <T> List<List<T>> cartesianProduct(List<List<T>> arrays) {
        List<List<T>> result = new ArrayList<>();

        cartesianProductHelper(result, arrays, 0, new ArrayList<>());

        return result;
    }

    private static <T> void cartesianProductHelper(List<List<T>> result, List<List<T>> arrays, int currentIndex, List<T> currentProduct) {
        if (currentIndex == arrays.size()) {
            result.add(new ArrayList<>(currentProduct));
            return;
        }

        List<T> currentArray = arrays.get(currentIndex);

        for (T item : currentArray) {
            currentProduct.add(item);
            cartesianProductHelper(result, arrays, currentIndex + 1, currentProduct);
            currentProduct.remove(currentProduct.size() - 1);
        }
    }

    public static void main(String[] args) {
        List<Integer> array1 = List.of(1, 2);
        List<Integer> array2 = List.of(3, 4);
        List<Integer> array3 = List.of(5, 6);

        List<List<Integer>> arrays = List.of(array1, array2, array3);

        List<List<Integer>> cartesianProduct = cartesianProduct(arrays);

        for (List<Integer> product : cartesianProduct) {
            System.out.println(product);
        }
    }
}
