import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.ArrayList;
import java.util.Random;

public class Nutzwertanalyse {

    public static void main(String[] args) {
        showSaw();
        showFuzzySaw();
//        ArrayList<int[][]> dList = getDList(5, 5, 5, 1, 10);
//
//        double[] weights = Helper.generateWeigths(5, 0, 1);
//
//        GetMonteCarloRanking(dList, weights);
    }

    public static int[][] GetMonteCarloRanking(ArrayList<int[][]> dList, double[] weights){
        int[][] monteCarloMatrix = generateMoteCarloMatrix(dList);

        Helper.saw(monteCarloMatrix, weights);

        int monteCarloIterations = 10_000;

        return monteCarloMatrix;
    }

    public static int[][] generateMoteCarloMatrix(ArrayList<int[][]> dList){
        Random random = new Random();
        int[][] monteCarloMatrix = new int[dList.get(0).length][dList.get(0)[0].length];
        int randomNumber;

        for(int i = 0; i < monteCarloMatrix.length; i++){
            for(int j = 0; j < dList.get(0)[0].length; j++){
                randomNumber = random.nextInt(dList.size());
                monteCarloMatrix[i][j] = dList.get(randomNumber)[i][j];
            }
        }
        return monteCarloMatrix;
    }

    public static ArrayList<int[][]> getDList(int number, int row, int col, int min, int max){
        ArrayList<int[][]> dList = new ArrayList<>();
        int[][] matrix;

        for(int i = 0; i < number; i++){
            matrix = Helper.generateInteger2DArray(row, col, min, max);
            dList.add(matrix);
        }

        return dList;
    }

    public static void showSaw(){
        int[][] matrix = Helper.generateInteger2DArray(5, 5, 1, 10);
        double[] weights = Helper.generateWeigths(5, 0, 1);

        System.out.println("Matrix:");
        Helper.show2DArray(matrix);
        System.out.println("Wichtung:");
        Helper.show1DArray(weights);
        double[] scores = Helper.saw(matrix, weights);
        System.out.println("Ergebnisse der Nutzwertanalyse:");
        Helper.show1DArray(scores);
    }

    public static void showFuzzySaw(){
        FuzzyJudgements[][] fuzzyJudgements = FuzzyHelper.generateFuzzyJudgementMatrix(3, 3);
        FuzzyPreferenzes[] fuzzyPreferenzes = FuzzyHelper.generateFuzzyPreferenzes(3);

        System.out.println("Judgements: ");
        FuzzyHelper.show2DArray(fuzzyJudgements);
        System.out.println();
        System.out.println("Preferenzes: ");
        FuzzyHelper.show1DArray(fuzzyPreferenzes);
        System.out.println();
        double[] scores = FuzzyHelper.fuzzySaw(fuzzyJudgements, fuzzyPreferenzes);
        System.out.println("Ranking: ");
        Helper.show1DArray(scores);
    }
}
