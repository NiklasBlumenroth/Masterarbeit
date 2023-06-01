import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.ArrayList;
import java.util.Random;

public class Nutzwertanalyse {

    public static void main(String[] args) {
        ArrayList<int[][]> decisionMakerList = generateDecisionMakerList(5, 5, 5, 1, 10);
        ArrayList<double[]> decisionMakerWeights = generateDecisionMakerWeightList(5, 0, 1);
        GetMonteCarloRanking(decisionMakerList, decisionMakerWeights);
        //TODO 1D array ebenfalls zufällig aus den bestehenden generieren lassen
        //TODO Aggregated Judgement Matrix
    }

    public static int[][] GetMonteCarloRanking(ArrayList<int[][]> dMList, ArrayList<double[]> dMWList){
        int[][] monteCarloMatrix = generateMoteCarloMatrix(dMList);
        double[] monteCarloWeights = getMonteCarloWeights(dMWList);

        Helper.saw(monteCarloMatrix, monteCarloWeights);

        int monteCarloIterations = 10_000;

        return monteCarloMatrix;
    }

    public static double[] getMonteCarloWeights(ArrayList<double[]> dMWList){
        Random random = new Random();
        double[] monteCarloWeight = new double[dMWList.get(0).length];
        int randomNumber;

        for(int i = 0; i < monteCarloWeight.length; i++){
            randomNumber = random.nextInt(dMWList.size());
            monteCarloWeight[i] = dMWList.get(randomNumber)[i];
        }
        return monteCarloWeight;
    }

    public static int[][] generateMoteCarloMatrix(ArrayList<int[][]> dMList){
        Random random = new Random();
        int[][] monteCarloMatrix = new int[dMList.get(0).length][dMList.get(0)[0].length];
        int randomNumber;

        for(int i = 0; i < monteCarloMatrix.length; i++){
            for(int j = 0; j < dMList.get(0)[0].length; j++){
                randomNumber = random.nextInt(dMList.size());
                monteCarloMatrix[i][j] = dMList.get(randomNumber)[i][j];
            }
        }
        return monteCarloMatrix;
    }

    public static ArrayList<double[]> generateDecisionMakerWeightList(int number, int min, int max){
        ArrayList<double[]> dMWList = new ArrayList<>();
        double[] matrix;

        for(int i = 0; i < number; i++){
            matrix = Helper.generateWeigths(number, min, max);
            dMWList.add(matrix);
        }

        return dMWList;
    }

    public static ArrayList<int[][]> generateDecisionMakerList(int number, int row, int col, int min, int max){
        ArrayList<int[][]> dMList = new ArrayList<>();
        int[][] matrix;

        for(int i = 0; i < number; i++){
            matrix = Helper.generateInteger2DArray(row, col, min, max);
            dMList.add(matrix);
        }

        return dMList;
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
