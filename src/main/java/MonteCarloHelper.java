import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MonteCarloHelper {
    private static final int monteCarloIterations = 10_000;
    private static final int row = 3;
    private static final int col = 3;
    private static final int numberOfDecisionMaker = 10;

    public static void main(String[] args) {
//        showMonteCarloSaw();

        //double entropy = calculateEntropy(array);
    }

    public static void showMonteCarloSaw(){
        ArrayList<Object[][]> decisionMakerList = generateDecisionMakerList(FuzzyJudgements.class, numberOfDecisionMaker, row, col, 1, 10);
        ArrayList<Object[]> decisionMakerWeightsList = generateDecisionMakerWeightList(FuzzyPreferenzes.class, numberOfDecisionMaker, col, 0, 1);
        Object[] monteCarloRanking = null;
        ArrayList<Object[]> monteCarloRankings = new ArrayList<>();

        System.out.println("\nAggregated Matrix");
        ArrayList<Object>[][] aggregatedMatrix = generateAggregatedMatrix(decisionMakerList);
        Helper.show2DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        ArrayList<Object>[] aggregatedWeights = generateAggregatedWeights(decisionMakerWeightsList);
        Helper.show1DArray(aggregatedWeights);

        System.out.println("\nAggregated K: " + getMatrixK(aggregatedMatrix, aggregatedWeights));


        Object[][] sawMatrix;
        Object[] sawWeights;
        Object[] rankingTotalPoints;
        Object[] rankingPosition;
        Object[][] totalRankingPositions = new Object[row][col];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                totalRankingPositions[i][j] = 0;
            }

        }

        System.out.println("\nSaw Weights:");
        for(int i = 0; i < monteCarloIterations; i++){

            sawMatrix = getSawMatrix(aggregatedMatrix, col, row);
            sawWeights = getSawWeights(aggregatedWeights, col);
            rankingTotalPoints = Helper.saw(sawMatrix, sawWeights);
            System.out.println("\n Ranking total points");
            Helper.show1DArray(rankingTotalPoints);
            rankingPosition = getRanksArray(rankingTotalPoints);
            System.out.println("\n Ranking positions");
            Helper.show1DArray(rankingPosition);
            addRanking(totalRankingPositions, rankingPosition);
        }

        System.out.println("\n total ranking positions");

        Helper.show2DArray(totalRankingPositions);



        /*
        aggregated matrix
        array mit rng elementen
        saw * 10000
        get ranked
         */


        System.out.println("\nGet Monte Carlo Total Ranking: ");
        Object[][] totalRanking = new Object[monteCarloRankings.get(0).length][monteCarloRankings.get(0).length];

        //fill with 0
        for(int i = 0; i < totalRanking.length; i++){
            for(int j = 0; j < totalRanking[0].length; j++){
                totalRanking[i][j] = 0;
            }
        }

        //add ranking by ranking
        for(Object[] ranking : monteCarloRankings){
//            addRanking(totalRanking, getRanksArray(ranking));
        }

        System.out.println("\nPlacement");
        Helper.show2DArray(totalRanking);
    }


    public static double calculateEntropy(Object[][] array) {
        int totalElements = array.length * array[0].length;
        Map<Integer, Integer> elementCounts = new HashMap<>();

        // Zählen der Häufigkeit jedes Elements im 2D-Array
        for (Object[] row : array) {
            for (Object element : row) {
                elementCounts.put((Integer)element, elementCounts.getOrDefault(element, 0) + 1);
            }
        }

        double entropy = 0.0;
        for (int count : elementCounts.values()) {
            double probability = (double) count / totalElements;
            entropy -= probability * log2(probability);
        }

        return entropy;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public static double calculateEntropy(double[] vector) {
        double entropy = 0.0;
        double sum = 0.0;

        // Summiere die Werte im Vektor
        for (double value : vector) {
            sum += value;
        }

        // Berechne die Entropie
        for (double value : vector) {
            if (value != 0.0) {
                double probability = value / sum;
                entropy -= probability * Math.log(probability);
            }
        }

        return entropy;
    }

    public static Object[][] getSawMatrix(ArrayList<Object>[][] aggregatedMatrix, int col, int row){
        Object[][] sawMatrix = new Object[row][col];
        Random random = new Random();

        int rngNumber;
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                rngNumber = random.nextInt(aggregatedMatrix[i][j].size());
                sawMatrix[i][j] = aggregatedMatrix[i][j].get(rngNumber);
            }
        }

        return sawMatrix;
    }

    public static Object[] getSawWeights(ArrayList<Object>[] aggregatedWeights, int col){
        Object[] sawWeights = new Object[col];
        Random random = new Random();
        int rngNumber;
        for(int i = 0; i < col; i++){
            rngNumber = random.nextInt(aggregatedWeights[i].size());
            sawWeights[i] = aggregatedWeights[i].get(rngNumber);
        }

        return sawWeights;
    }


    @NotNull
    public static ArrayList<Object>[][] generateAggregatedMatrix(@NotNull ArrayList<Object[][]> dMList){
        ArrayList<Object>[][] aggregatedMatrix = new ArrayList[dMList.get(0).length][dMList.get(0)[0].length];

        //fill aggregated with empty lists
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                aggregatedMatrix[i][j] = new ArrayList<>();
            }
        }

        //fill empty lists
        for (Object[][] objects : dMList) {
            for (int i = 0; i < aggregatedMatrix.length; i++) {
                for (int j = 0; j < dMList.get(0)[0].length; j++) {
                    if (!aggregatedMatrix[i][j].contains(objects[i][j])) {
                        aggregatedMatrix[i][j].add(objects[i][j]);
                    }
                }
            }
        }
        return aggregatedMatrix;
    }

    public static int getMatrixK(@NotNull ArrayList<Object>[][] matrix, ArrayList<Object>[] weights){
        int k = 1;

        for (ArrayList<Object>[] arrayLists : matrix) {
            for (ArrayList<Object> arrayList : arrayLists) {
                k *= arrayList.size();
            }
        }
        for(ArrayList<Object> arrayList : weights){
            k *= arrayList.size();
        }

        return k;
    }

    @NotNull
    public static ArrayList<Object>[] generateAggregatedWeights(@NotNull ArrayList<Object[]> dMWList){
        ArrayList<Object>[] aggregatedWeights = new ArrayList[dMWList.get(0).length];

        //fill aggregated with empty lists
        for(int j = 0; j < aggregatedWeights.length; j++){
            aggregatedWeights[j] = new ArrayList<>();
        }

        //fill empty lists
        for(int k = 0; k < dMWList.size(); k++){
            for(int j = 0; j < dMWList.get(0).length; j++){
                if(!aggregatedWeights[j].contains(dMWList.get(k)[j])){
                    aggregatedWeights[j].add(dMWList.get(k)[j]);
                }
            }
        }

        return aggregatedWeights;
    }

    @NotNull
    public static ArrayList<Object[]> generateDecisionMakerWeightList(Class<?> clazz, int number, int length, int min, int max){
        ArrayList<Object[]> dMWList = new ArrayList<>();
        Object[] matrix;
        System.out.println("Generates decisionMakerWeightList: ");
        for(int i = 0; i < number; i++){
            matrix = Helper.generate1DArray(clazz, length, min, max);
            Helper.show1DArray(matrix);
            dMWList.add(matrix);
        }

        return dMWList;
    }

    @NotNull
    public static ArrayList<Object[][]> generateDecisionMakerList(Class<?> clazz, int number, int row, int col, int min, int max){
        ArrayList<Object[][]> dMList = new ArrayList<>();
        Object[][] matrix;
        System.out.println("Generates decisionMakerWeightsList: ");

        for(int i = 0; i < number; i++){
            matrix = Helper.generate2DArray(clazz, row, col, min, max);
            Helper.show2DArray(matrix);
            System.out.println(" ");
            dMList.add(matrix);
        }

        return dMList;
    }

    public static void addRanking(Object[][] totalRanking, Object[] newRanking){
        Integer i1;
        int x;
        for(int i = 0; i < newRanking.length; i++){
            x = (Integer) newRanking[i] - 1;
            i1 = (Integer)totalRanking[i][x];
            i1 += 1;
            totalRanking[i][x] = i1;
        }


    }

    public static Object[] getRanksArray(Object[] array) {
        Object[] result = new Object[array.length];

        for (int i = 0; i < array.length; i++) {
            int count = 0;
            for (Object aDouble : array) {
                if ((Double) aDouble > (Double) array[i]) {
                    count++;
                }
            }
            result[i] = count + 1;
        }
        return result;
    }


}
