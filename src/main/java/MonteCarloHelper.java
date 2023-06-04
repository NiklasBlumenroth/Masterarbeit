import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.ArrayList;
import java.util.Random;

public class MonteCarloHelper {
    private static final int monteCarloIterations = 10_000;

    public static void main(String[] args) {
        showMonteCarloSaw();
    }

    public static void showMonteCarloFuzzySaw(){
        ArrayList<Object[][]> decisionMakerList = generateDecisionMakerList(FuzzyJudgements.class, 5, 5, 5, 1, 10);
        ArrayList<Object[]> decisionMakerWeights = generateDecisionMakerWeightList(FuzzyPreferenzes.class, 5, 5, 0, 1);
        Object[] monteCarloRanking = null;
        ArrayList<Object[]> monteCarloRankings = new ArrayList<>();

        System.out.println("\nGet Monte Carlo Rankings: ");
        for(int i = 0; i < monteCarloIterations; i++){
            monteCarloRanking = getMonteCarloRanking(decisionMakerList, decisionMakerWeights);
            monteCarloRankings.add(monteCarloRanking);
            System.out.println("\nMonte Carlo ranking total value");
            Helper.show1DArray(monteCarloRanking);
        }

        System.out.println("\nGet Monte Carlo Total Ranking: ");
        Object[][] totalRanking = new Integer[monteCarloRankings.get(0).length][monteCarloRankings.get(0).length];

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

    public static void showMonteCarloSaw(){
        ArrayList<Object[][]> decisionMakerList = generateDecisionMakerList(FuzzyJudgements.class, 3, 2, 2, 1, 10);
        ArrayList<Object[]> decisionMakerWeightsList = generateDecisionMakerWeightList(FuzzyPreferenzes.class, 2, 2, 0, 1);
        Object[] monteCarloRanking = null;
        ArrayList<Object[]> monteCarloRankings = new ArrayList<>();

        System.out.println("\nAggregated Matrix");
        ArrayList<Object>[][] aggregatedMatrix = generateAggregatedMatrix(decisionMakerList);
        Helper.show2DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        ArrayList<Object>[] aggregatedWeights = generateAggregatedWeights(decisionMakerWeightsList);
        Helper.show1DArray(aggregatedWeights);

        System.out.println("\nAggregated K: " + getMatrixK(aggregatedMatrix, aggregatedWeights));

        System.out.println("\nGet Monte Carlo Rankings: ");
        for(int i = 0; i < monteCarloIterations; i++){
            monteCarloRanking = getMonteCarloRanking(decisionMakerList, decisionMakerWeightsList);
            monteCarloRankings.add(monteCarloRanking);
            System.out.println("\nMonte Carlo ranking total value");
            assert monteCarloRanking != null;
            Helper.show1DArray(monteCarloRanking);
        }

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

    public static ArrayList<Object>[][] generateAggregatedMatrix(ArrayList<Object[][]> dMList){
        ArrayList<Object>[][] aggregatedMatrix = new ArrayList[dMList.get(0).length][dMList.get(0)[0].length];

        //fill aggregated with empty lists
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                aggregatedMatrix[i][j] = new ArrayList<>();
            }
        }

        //fill empty lists
        for(int k = 0; k < dMList.size(); k++){
            for(int i = 0; i < aggregatedMatrix.length; i++){
                for(int j = 0; j < dMList.get(0)[0].length; j++){
                    if(!aggregatedMatrix[i][j].contains(dMList.get(k)[i][j])){
                        aggregatedMatrix[i][j].add(dMList.get(k)[i][j]);
                    }
                }
            }
        }
        return aggregatedMatrix;
    }

    public static int getMatrixK(ArrayList<Object>[][] matrix, ArrayList<Object>[] weights){
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

    public static ArrayList<Object>[] generateAggregatedWeights(ArrayList<Object[]> dMWList){
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



    /*-------------------------------*/

//    public static void addRanking(Object[][] totalRanking, Object[] newRanking){
//        for(int i = 0; i < totalRanking.length; i++){
//            totalRanking[i][(Integer) newRanking[i] - 1] += 1;
//        }
//    }

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

    public static Object[] getMonteCarloRanking(ArrayList<Object[][]> dMList, ArrayList<Object[]> dMWList){
//        Integer[][] monteCarloMatrix = (Integer[][]) generateAggregatedMatrix(dMList);
//        Object[] monteCarloWeights = generateAggregatedWeights(dMWList);
//
//        System.out.println("\nGet Monte Carlo Matrix");
//        Helper.show2DArray(monteCarloMatrix);
//
//        System.out.println("\nGet Monte Carlo Weight");
//        Helper.show1DArray(monteCarloWeights);
//
//        return Helper.saw(Integer.class, monteCarloMatrix, monteCarloWeights);
        return null;
    }

}
