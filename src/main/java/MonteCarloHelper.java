import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MonteCarloHelper {
    public static final int monteCarloIterations = 10_000;
    public static final int row = 3;
    public static final int col = 3;
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    public static void main(String[] args) {
        Map<Integer, Integer> list = new HashMap<>();
        for(int i = 0; i < 100; i++){
            list.put(i, 0);
        }
        Random random = new Random();
        int number = 0;
        Integer sum;
        int totals = 1000000;
        for(int i = 0; i < totals; i++){
            number = random.nextInt(100);
            sum = list.get(number);
            list.put(number, sum + 1);
        }

        for(int i = 0; i < 100; i++){
            System.out.println(list.get(i));
        }
        System.out.println(list);
    }

    public static void showMonteCarloSaw(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights){
        System.out.println("\nAggregated Matrix");
        Helper.show2DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        Helper.show1DArray(aggregatedWeights);

        Object[] monteCarloRanking = null;
        ArrayList<Object[]> monteCarloRankings = new ArrayList<>();

        System.out.println("\nAggregated K: " + getMatrixK(aggregatedMatrix, aggregatedWeights));

        Object[][] sawMatrix;
        Object[] sawWeights;
        Object[] rankingTotalPoints;
        Object[] rankingPosition;
        Object[][] rankAcceptabilityIndices = new Object[row][col];

        //fill ranking counter with 0
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                rankAcceptabilityIndices[i][j] = 0;
            }
        }

        // generate map for counting
        Map<Object, Map<Integer, Double>>[][] currentJudgementAcceptabilityIndices = new Map[row][col];
        Map<Object, Map<Integer, Double>>[][] potentialJudgementAcceptabilityIndices = new Map[row][col];
        Map<Object, Map<Integer, Double>>[] currentPreferenceAcceptabilityIndices = new Map[row];
        Map<Object, Map<Integer, Double>>[] potentialPreferencesAcceptabilityIndices = new Map[row];
        //fill matrix map with 0
        fillMatrixMapWithZero(aggregatedMatrix, currentJudgementAcceptabilityIndices);
        fillMatrixMapWithZero(aggregatedMatrix, potentialJudgementAcceptabilityIndices);
        //fill weights map with 0
        fillWeightsMapWithZero(aggregatedWeights, currentPreferenceAcceptabilityIndices);
        fillWeightsMapWithZero(aggregatedWeights, potentialPreferencesAcceptabilityIndices);

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
            addRanking(rankAcceptabilityIndices, rankingPosition);
            System.out.println("\n new rankAcceptabilityIndices");
            Helper.show2DArray(rankAcceptabilityIndices);
            //sawMatrix + ranking = countingMatrixRankingMap
            countByRankingAndDecision(rankingPosition, currentJudgementAcceptabilityIndices, sawMatrix);
            //sawWeights + ranking = countingWeightsRankingMap
            countByRankingAndWeights(rankingPosition, currentPreferenceAcceptabilityIndices, sawWeights);
        }

        System.out.println("\nfinal rankAcceptabilityIndices");
        Helper.show2DArray(rankAcceptabilityIndices);
        System.out.println("\nfinal total ranking positions normalized");

        normalizeTotalRankingPositions(rankAcceptabilityIndices);
        Helper.show2DArray(rankAcceptabilityIndices);
        System.out.println("\ncurrentJudgementAcceptabilityIndices");
        Helper.show2DArray(currentJudgementAcceptabilityIndices);
        System.out.println("\nnormalized currentJudgementAcceptabilityIndices");
//        normalizeAggregatedMatrixMap(currentJudgementAcceptabilityIndices, nomalizeArray);
        Helper.show2DArray(currentJudgementAcceptabilityIndices);
        System.out.println("\ncurrentPreferenceAcceptabilityIndices");
        Helper.show1DArray(currentPreferenceAcceptabilityIndices);
        fillPotentialAggregatedMatrixRankingMap(currentJudgementAcceptabilityIndices, potentialJudgementAcceptabilityIndices);


        System.out.println("\npotentialJudgementAcceptabilityIndices");
        Helper.show2DArray(potentialJudgementAcceptabilityIndices);
        fillPotentialAggregatedWeightsRangingMap(currentPreferenceAcceptabilityIndices, potentialPreferencesAcceptabilityIndices);
        System.out.println("\npotentialPreferencesAcceptabilityIndices");
        Helper.show1DArray(potentialPreferencesAcceptabilityIndices);
        Map<Object, Double>[][] judgementEntropyMatrix = getEntropyMatrix(potentialJudgementAcceptabilityIndices);
        System.out.println("\njudgementEntropyMatrix rank 1");
        Helper.show2DArray(judgementEntropyMatrix);

    }

    public static  Map<Object, Double>[][] getEntropyMatrix(Map<Object, Map<Integer, Double>>[][] potentialAggregatedMatrixRankingMap){
        Map<Object, Double>[][] entropyMatrix = new Map[potentialAggregatedMatrixRankingMap.length][potentialAggregatedMatrixRankingMap.length];
        Double[] vektor;
        for(int i = 0; i < potentialAggregatedMatrixRankingMap.length; i++){
            for(int j = 0; j < potentialAggregatedMatrixRankingMap[i].length; j++){
                entropyMatrix[i][j] = new HashMap<>();
                for (Map.Entry<Object, Map<Integer, Double>> entry : potentialAggregatedMatrixRankingMap[i][j].entrySet()) {
                    vektor = new Double[potentialAggregatedMatrixRankingMap[i][j].get(entry.getKey()).size()];
                    int counter = 0;
                    for (Map.Entry<Integer, Double> rankingEntry : potentialAggregatedMatrixRankingMap[i][j].get(entry.getKey()).entrySet()) {
                        vektor[counter] = rankingEntry.getValue();
                        counter++;
                    }
                    entropyMatrix[i][j].put(entry.getKey(), calculateEntropy(vektor));
                }
            }
        }
        return entropyMatrix;
    }

    public static Double calculateEntropy(Double[] vector) {
        Double entropy = 0.0;
        Double sum = 0.0;

        for (Double value : vector) {
            sum += value;
        }

        for (Double value : vector) {
            if (value != 0.0) {
                Double probability = value / sum;
                entropy -= probability * Math.log(probability);
            }
        }

        return entropy;
    }

    public static HashMap getNormalizeArray(Object[][] totalRankingPositions){
        //add values in a col to get integer /= ...
        HashMap<Integer, Integer> map = new HashMap<>();
        Integer sum = 0;
        for(int i = 0; i < totalRankingPositions.length; i++){
            for(int j = 0; j < totalRankingPositions[i].length; j++){
                sum += (Integer) totalRankingPositions[j][i];
            }
            map.put(i, sum);
            sum = 0;
        }
        return map;
    }


        public static void normalizeAggregatedMatrixMap(Map<Object, Map<Integer, Double>>[][] aggregatedMatrixRankingMap){
            Double integer;
            for(int i = 0; i < aggregatedMatrixRankingMap.length; i++){
                for(int j = 0; j < aggregatedMatrixRankingMap[i].length; j++){
                    for (Map.Entry<Object, Map<Integer, Double>> entry : aggregatedMatrixRankingMap[i][j].entrySet()) {
                        for (Map.Entry<Integer, Double> rankingEntry : entry.getValue().entrySet()) {
                            integer = rankingEntry.getValue();
                            integer /= (Integer) monteCarloIterations;
                            aggregatedMatrixRankingMap[i][j].get(entry.getKey()).put(rankingEntry.getKey(), integer);
                        }
                    }
                }
            }
        }

    public static void normalizeTotalRankingPositions(Object[][] totalRankingPositions){
        //add values in a col to get integer /= ...
        Double integer;
        for(int i = 0; i < totalRankingPositions.length; i++){
            for(int j = 0; j < totalRankingPositions[i].length; j++){
                integer = (Integer) totalRankingPositions[j][i] * 1.0;
                integer /= (Integer) monteCarloIterations;
                totalRankingPositions[j][i] = df.format(integer);
            }
        }
    }

    public static void fillPotentialAggregatedWeightsRangingMap(Map<Object, Map<Integer, Double>>[] aggregatedWeightsRankingMap,
                                                                Map<Object, Map<Integer, Double>>[] potentialAggregatedWeightsRankingMap){
        for(int j = 0; j < aggregatedWeightsRankingMap.length; j++){
            Integer multiplicator = aggregatedWeightsRankingMap[j].size();
            for(int k = 0; k < multiplicator; k++){
                for (Map.Entry<Object, Map<Integer, Double>> entry : potentialAggregatedWeightsRankingMap[j].entrySet()) {
                    for (Map.Entry<Integer, Double> rankingEntry : potentialAggregatedWeightsRankingMap[j].get(entry.getKey()).entrySet()) {
                        potentialAggregatedWeightsRankingMap[j].get(entry.getKey()).put(
                                rankingEntry.getKey(),
                                aggregatedWeightsRankingMap[j].get(entry.getKey()).get(rankingEntry.getKey()) * multiplicator);
                    }
                }
            }
        }
    }
    public static void fillPotentialAggregatedMatrixRankingMap(Map<Object, Map<Integer, Double>>[][] aggregatedMatrixRankingMap,
                                                               Map<Object, Map<Integer, Double>>[][] potentialAggregatedMatrixRankingMap){
        for(int i = 0; i < aggregatedMatrixRankingMap.length; i++){
            for(int j = 0; j < aggregatedMatrixRankingMap[i].length; j++){
                Integer multiplicator = aggregatedMatrixRankingMap[i][j].size();
                for(int k = 0; k < multiplicator; k++){
                    for (Map.Entry<Object, Map<Integer, Double>> entry : potentialAggregatedMatrixRankingMap[i][j].entrySet()) {
                        for (Map.Entry<Integer, Double> rankingEntry : potentialAggregatedMatrixRankingMap[i][j].get(entry.getKey()).entrySet()) {
                            potentialAggregatedMatrixRankingMap[i][j].get(entry.getKey()).put(
                                    rankingEntry.getKey(),
                                    aggregatedMatrixRankingMap[i][j].get(entry.getKey()).get(rankingEntry.getKey()) * multiplicator);
                        }
                    }
                }
            }
        }
    }

    public static void countByRankingAndDecision(Object[] rankingPosition, Map<Object, Map<Integer, Double>>[][] aggregatedMap, Object[][] sawMatrix){
        for(int i = 0; i < sawMatrix.length; i++){
            for(int j = 0; j < sawMatrix[i].length; j++){
                Double value = aggregatedMap[i][j].get(sawMatrix[i][j]).get(rankingPosition[i]);
                value += 1;
                aggregatedMap[i][j].get(sawMatrix[i][j]).put((Integer)rankingPosition[i], value);
            }
        }
    }

    public static void countByRankingAndWeights(Object[] rankingPosition, Map<Object, Map<Integer, Double>>[] aggregatedWeightsMap, Object[] sawWeights){
        for(int i = 0; i < sawWeights.length; i++){
            Double value = aggregatedWeightsMap[i].get(sawWeights[i]).get(rankingPosition[i]);
            value += 1;
            aggregatedWeightsMap[i].get(sawWeights[i]).put((Integer)rankingPosition[i], value);
        }
    }

    public static void fillWeightsMapWithZero(ArrayList<Object>[] aggregatedWeights, Map<Object, Map<Integer, Double>>[] aggregatedWeightsRankingMap){
        for(int i = 0; i < aggregatedWeights.length; i++){
            //create cell
            Map<Object, Map<Integer, Double>> newMap = new HashMap<>();
            for(int k = 0; k < aggregatedWeights[i].size(); k++){
                //create counting map
                Map<Integer, Double> rankingCounter = new HashMap<>();
                for(int l = 0; l < row; l++){
                    rankingCounter.put(l + 1, 0.0);
                }
                //set countingMap to value
                newMap.put(aggregatedWeights[i].get(k), rankingCounter);
            }
            aggregatedWeightsRankingMap[i] = newMap;
        }
    }

    public static void fillMatrixMapWithZero(ArrayList<Object>[][] aggregatedMatrix, Map<Object, Map<Integer, Double>>[][] aggregatedMatrixRankingMap){
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                //create cell
                Map<Object, Map<Integer, Double>> newMap = new HashMap<>();
                for(int k = 0; k < aggregatedMatrix[i][j].size(); k++){
                    //create counting map
                    Map<Integer, Double> rankingCounter = new HashMap<>();
                    for(int l = 0; l < row; l++){
                        rankingCounter.put(l + 1, 0.0);
                    }
                    //set countingMap to value
                    newMap.put(aggregatedMatrix[i][j].get(k), rankingCounter);
                }
                aggregatedMatrixRankingMap[i][j] = newMap;
            }
        }
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
