import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArraySortAndRestore {
    private static int[] sortArrayAndGetIndexMapping(int[] arr) {
        int[] sortedArr = arr.clone();
        Arrays.sort(sortedArr);

        Map<Integer, Integer> indexMapping = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            indexMapping.put(sortedArr[i], i);
        }

        int[] sortingVector = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            sortingVector[i] = indexMapping.get(arr[i]);
        }

        return sortingVector;
    }

    private static int[] restoreArray(int[] sortingVector, int[] sortedArray) {
        int[] restoredArray = new int[sortedArray.length];

        for (int i = 0; i < sortedArray.length; i++) {
            restoredArray[i] = sortedArray[sortingVector[i]];
        }

        return restoredArray;
    }

    private static int[] sortArrayUsingVector(int[] sortingVector, int[] arrayToSort) {
        int[] sortedArray = new int[arrayToSort.length];

        for (int i = 0; i < arrayToSort.length; i++) {
            sortedArray[i] = arrayToSort[sortingVector[i]];
        }

        return sortedArray;
    }

    private static int[] restoreSortedArray(int[] sortingVector, int[] sortedArray) {
        int[] restoredArray = new int[sortedArray.length];

        for (int i = 0; i < sortingVector.length; i++) {
            restoredArray[sortingVector[i]] = sortedArray[i];
        }

        return restoredArray;
    }

    public static void main(String[] args) {
        int[] originalArray = {5, 2, 8, 1, 3};

        // Sort the array and get the sorting vector
        int[] sortingVector = sortArrayAndGetIndexMapping(originalArray);
        System.out.println("Original Array: " + Arrays.toString(originalArray));
        System.out.println("Sorting Vector: " + Arrays.toString(sortingVector));

        // Restore the sorted array to its original state
        int[] sortedArray = restoreArray(sortingVector, originalArray);
        System.out.println("Sorted Array: " + Arrays.toString(sortedArray));

        // Sort another array using the sorting vector
        int[] anotherArray = {9, 4, 7, 1, 6};
        int[] sortedAnotherArray = sortArrayUsingVector(sortingVector, anotherArray);
        System.out.println("Another Array: " + Arrays.toString(anotherArray));
        System.out.println("Sorted Another Array: " + Arrays.toString(sortedAnotherArray));

        // Restore the sorted another array to its original state
        int[] restoredAnotherArray = restoreSortedArray(sortingVector, sortedAnotherArray);
        System.out.println("Restored Another Array: " + Arrays.toString(restoredAnotherArray));
    }
}
