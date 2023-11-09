import Enums.FuzzyJudgements;
import Enums.LexJudgements;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MonteCarloHelper {
    public static int monteCarloIterations = 10_000;
    public static int iteration;
    public static int alternative;
    public static int criteria;
    public static int k;
    public static long size = 0;
    public static int iterationNumber = 0;

    public static void main(String[] args) {
        int[] A = {1, 2};
        int[] B = {3};
        int[] C = {5, 6};
        int[] D = {7, 8};
        int[][] AB = Arrays.stream(A).boxed()
                .flatMap(ai -> Arrays.stream(B).boxed()
                        .map(bi -> new int[]{ai, bi}))
                .toArray(int[][]::new);
        int[][] CD = Arrays.stream(C).boxed()
                .flatMap(ai -> Arrays.stream(D).boxed()
                        .map(bi -> new int[]{ai, bi}))
                .toArray(int[][]::new);
        System.out.println("Cartesian product:\n" + Arrays.deepToString(AB));
        System.out.println("Cartesian product:\n" + Arrays.deepToString(CD));

        int[][][] ABCD = cartesianProduct(AB, CD);

        System.out.println("Cartesian product:\n" + Arrays.deepToString(ABCD));
    }

    public static int[][][] cartesianProduct(int[][] s1, int[][] s2) {
        int size1 = s1.length;
        int size2 = s2.length;
        int[][][] result = new int[size1 * size2][2][2];
        for (int i = 0, d = 0; i < size1; ++i) {
            for (int j = 0; j < size2; ++j, ++d) {
                result[d][0] = s1[i];
                result[d][1] = s2[j];
            }
        }
        return result;
    }

    public static Map<String, Object> showMonteCarloSaw(int[][][] aggregatedMatrix, int[][] aggregatedWeights, boolean full, boolean lex){
        Date date = new Date();
        System.out.println("Start: " + date);
        alternative = aggregatedMatrix.length;
        criteria = aggregatedWeights.length;

        System.out.println("\nAggregated Matrix");
        Helper.show3DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        Helper.show2DArray(aggregatedWeights);

        int[][] judgementCombinationList = null;//getJudgementCombinations(aggregatedMatrix);
        int[][] preferenceCombinationList = getPreferenceCombinations(aggregatedWeights, lex);
        //k = judgementCombinationList.length * preferenceCombinationList.length;
        //System.out.println("\nAggregated K: " + k);

        int[][] sawMatrix = null;
        int[] sawWeights = null;
        double[] rankingTotalPoints;
        int[] rankingPosition;
        double[][] rankAcceptabilityIndices = new double[alternative][criteria];
        //fill ranking counter with 0
        for(int i = 0; i < alternative; i++){
            for(int j = 0; j < criteria; j++){
                rankAcceptabilityIndices[i][j] = 0;
            }
        }

        // generate map for counting
        //row, col, counter (index + counter)
        double[][][] currentJudgementAcceptabilityIndices = new double[alternative][][];
        double[][][] potentialJudgementAcceptabilityIndices = new double[alternative][][];
        double[][] currentPreferenceAcceptabilityIndices = new double[alternative][];
        double[][] potentialPreferencesAcceptabilityIndices = new double[alternative][];

        //fill matrix map with 0 or -1 if not existed
        initMatrixMap(aggregatedMatrix, currentJudgementAcceptabilityIndices);
        initMatrixMap(aggregatedMatrix, potentialJudgementAcceptabilityIndices);
        //fill weights map with 0
        initWeightsMap(aggregatedWeights, currentPreferenceAcceptabilityIndices);
        initWeightsMap(aggregatedWeights, potentialPreferencesAcceptabilityIndices);

        //create currentJudgementAcceptabilityIndices and currentPreferenceAcceptabilityIndices
        //create potentialJudgementAcceptabilityIndices and potentialPreferenceAcceptabilityIndices
        //alternativen, row, col, counter (index + counter)
        double[][][][] objectCurrentJudgementAcceptabilityIndices = new double[alternative][][][];
        double[][][][] objectPotentialJudgementAcceptabilityIndices = new double[alternative][][][];
        for(int j = 0; j < alternative; j++){
            //add one matrix for each possible winner
            objectCurrentJudgementAcceptabilityIndices[j] = currentJudgementAcceptabilityIndices.clone();
            objectPotentialJudgementAcceptabilityIndices[j] = currentJudgementAcceptabilityIndices.clone();
        }

        double[][][] objectCurrentPreferenceAcceptabilityIndices = new double[alternative][][];
        double[][][] objectPotentialPreferenceAcceptabilityIndices = new double[alternative][][];
        for(int j = 0; j < alternative; j++){
            objectCurrentPreferenceAcceptabilityIndices[j] = currentPreferenceAcceptabilityIndices.clone();
            objectPotentialPreferenceAcceptabilityIndices[j] = currentPreferenceAcceptabilityIndices.clone();
        }

        //monteCarloSimulation
        iteration = (full) ? k : monteCarloIterations;
        int prefCounter = 0;
        int jugCounter = 0;
        for(int i = 0; i < iteration - 1; i++){
            if(i % 100000 == 0){
                System.out.println(i);
            }
            if(full){
                if(jugCounter == judgementCombinationList.length){
                    jugCounter = 0;
                    prefCounter++;
                }
                sawMatrix = listToMatrix(judgementCombinationList[jugCounter], alternative);
                jugCounter++;
                sawWeights = preferenceCombinationList[prefCounter];
            }else{
                Random random = new Random();
                int rngNumberW = random.nextInt(preferenceCombinationList.length);
                int rngNumberM = random.nextInt(judgementCombinationList.length);
                sawMatrix = listToMatrix(judgementCombinationList[rngNumberM], alternative);
                sawWeights = preferenceCombinationList[rngNumberW];
            }

            if(i > 38928000){
                rankingTotalPoints = Helper.decisionMethod(sawMatrix, sawWeights, true, lex);

                rankingPosition = getRanksArray(rankingTotalPoints);
                addRanking(rankAcceptabilityIndices, rankingPosition);
                //System.out.println("\nranking ");
                //Helper.show1DArray(rankingPosition);

                //System.out.println("\nrankAcceptabilityIndices ");
                //Helper.showAcceptabilityIndices(rankAcceptabilityIndices);
            }else {
                rankingTotalPoints = Helper.decisionMethod(sawMatrix, sawWeights, false, lex);

                rankingPosition = getRanksArray(rankingTotalPoints);
                addRanking(rankAcceptabilityIndices, rankingPosition);
            }

            //sawMatrix + ranking = countingMatrixRankingMap
            countByRankingAndDecision(rankingPosition, objectCurrentJudgementAcceptabilityIndices, sawMatrix);
            //sawWeights + ranking = countingWeightsRankingMap
            countByRankingAndWeights(rankingPosition, objectCurrentPreferenceAcceptabilityIndices, sawWeights);
        }

        System.out.println("\nAggregated Matrix");
        Helper.show3DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        Helper.show2DArray(aggregatedWeights);

        System.out.println("\nAggregated K: " + k);

        System.out.println("\nfinal rankAcceptabilityIndices ");
        Helper.showAcceptabilityIndices(rankAcceptabilityIndices);

        System.out.println("\nfinal rankAcceptabilityIndices scaled");
        scaleTotalRankingPositions(rankAcceptabilityIndices);
        Helper.showAcceptabilityIndices(rankAcceptabilityIndices);

        scaleAggregatedMatrixMap(objectCurrentJudgementAcceptabilityIndices);
        for(int j = 0; j < alternative; j++){
            System.out.println("\ncurrentJudgementAcceptabilityIndex for a" + j);
            Helper.show3DArray(objectCurrentJudgementAcceptabilityIndices[j]);
        }

        scaleAggregatedWeightsMap(objectCurrentPreferenceAcceptabilityIndices);
        for (int i = 0; i < criteria; i++){
            System.out.println("\ncurrentPreferenceAcceptabilityIndex for a" + i);
            Helper.show2DArray(objectCurrentPreferenceAcceptabilityIndices[i]);
        }

        fillPotentialJudgementAcceptabilityIndices(objectCurrentJudgementAcceptabilityIndices, objectPotentialJudgementAcceptabilityIndices);
        for(int j = 0; j < alternative; j++){
            System.out.println("\npotentialJudgementAcceptabilityIndex for a" + j);
            Helper.show3DArray(objectPotentialJudgementAcceptabilityIndices[j]);
        }

        fillPotentialAggregatedWeightsRankingMap(objectCurrentPreferenceAcceptabilityIndices, objectPotentialPreferenceAcceptabilityIndices);
        for (int i = 0; i < criteria; i++){
            System.out.println("\npotentialPreferenceAcceptabilityIndex for a" + i);
            Helper.show2DArray(objectPotentialPreferenceAcceptabilityIndices[i]);
        }

        double[][][] judgementEntropyMatrix = getEntropyMatrix(objectPotentialJudgementAcceptabilityIndices);
        System.out.println("\njudgementEntropyMatrix");
        Helper.show3DArray(judgementEntropyMatrix);

        double[][] preferenceEntropy = getEntropyPreference(objectPotentialPreferenceAcceptabilityIndices);
        System.out.println("\npreferenceEntropy");
        Helper.show2DArray(preferenceEntropy);

        System.out.println("\ncurrent entropy");
        System.out.println(getCurrentEntropy(rankAcceptabilityIndices));
        date = new Date();
        System.out.println("Ende: " + date);
        return null;//getLowestValue(judgementEntropyMatrix, preferenceEntropy);
    }

    public static ArrayList<int[]> help(int[][] judgementCombinationList){
        ArrayList<int[]> list = new ArrayList<>();
        for(int[] matrix : judgementCombinationList){
            if(matrix[5] == 3){
                list.add(matrix);
            }
        }
        return list;
    }

    public static boolean doubleOne(Object[] ranking){
        int counter = 0;
        for(int i = 0; i < ranking.length; i++){
            if((Integer)ranking[i] == 1){
                counter++;
            }
        }
        if(counter == 2){
            return true;
        }
        return false;
    }

    public static Map<String, Object> getLowestValue(Map<Object, Double>[][] judgementEntropyMatrix, Map<Object, Double>[] preferenceEntropy){
        Map<String, Object> map = new HashMap<>();
        Double lowestValue = 1000.0;
        Object lowestKey = null;
        Integer lowestI = judgementEntropyMatrix.length;
        Integer lowestJ = judgementEntropyMatrix.length;
        Boolean lowestValueIsJudgement = null;

        for (int i = 0; i < judgementEntropyMatrix.length; i++) {
            for(int j = 0; j < judgementEntropyMatrix[i].length; j++){
                for (Object key: judgementEntropyMatrix[i][j].keySet()) {
                    if(judgementEntropyMatrix[i][j].get(key) < lowestValue){
                        lowestValue = judgementEntropyMatrix[i][j].get(key);
                        lowestKey = key;
                        lowestI = i;
                        lowestJ = j;
                        lowestValueIsJudgement = true;
                    }
                }
            }
        }

        for(int i = 0; i < preferenceEntropy.length; i++){
            for (Object key: preferenceEntropy[i].keySet()) {
                if(preferenceEntropy[i].get(key) < lowestValue){
                    lowestValue = preferenceEntropy[i].get(key);
                    lowestKey = key;
                    lowestI = i;
                    lowestValueIsJudgement = false;
                }
            }
        }
        map.put("lowestValue", lowestValue);
        map.put("lowestKey", lowestKey);
        map.put("lowestI", lowestI);
        map.put("lowestJ", lowestJ);
        map.put("lowestValueIsJudgement", lowestValueIsJudgement);
        return map;
    }

    private static int[][] getPreferenceCombinations(int[][] aggregatedWeights, boolean lex){
        int kCounter = 1;
        //countCombinations
        for(int j = 0; j < aggregatedWeights.length; j++){
            kCounter *= aggregatedWeights[j].length;
        }
        int[][] aggregatedWeightsListForm = new int[aggregatedWeights.length][];
        for(int i = 0; i < aggregatedWeights.length; i++){
            aggregatedWeightsListForm[i] = aggregatedWeights[i];
        }

        int[][] fullIterations = cartesianProduct(aggregatedWeightsListForm);
        if(lex){
            fullIterations = cutIfDouble(fullIterations);
        }
        return fullIterations;
    }

    public static int[][] cutIfDouble(int[][] fullIterations){
        //get indize of array with no double
        ArrayList <Integer> indize = new ArrayList<>();
        for(int i = 0; i < fullIterations.length; i++){
            if(!hasDouble(fullIterations[i])){
                indize.add(i);
            }
        }
        //create new array with no double
        int[][] newArray = new int[indize.size()][];
        for(int i = 0; i < newArray.length; i++){
            newArray[i] = fullIterations[indize.get(i)];
        }
        return newArray;
    }

    public static boolean hasDouble(int[] fullIterations){
        for(int j = 0; j < fullIterations.length; j++){
            int elem = fullIterations[j];
            for(int i = j + 1; i < fullIterations.length; i++){
                if(fullIterations[i] == elem){
                    return true;
                }
            }
        }
        return false;
    }

    private static int[][] getJudgementCombinations(int[][][] aggregatedMatrix){
        long kCounter = 1;
        //countCombinations
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                kCounter *= aggregatedMatrix[i][j].length;
            }
        }

        int[][] aggregatedMatrixListForm = new int[aggregatedMatrix.length * aggregatedMatrix[0].length][];
        int counter = 0;
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                aggregatedMatrixListForm[counter] = aggregatedMatrix[i][j];
                counter++;
            }
        }

        int[][] fullIterations = cartesianProduct(aggregatedMatrixListForm);
        return fullIterations;
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

    public static int[][] listToMatrix(int[] list, int matrixLength){
        int[][] matrix = new int[matrixLength][list.length / matrixLength];
        int counter = 0;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                matrix[i][j] = list[counter];
                counter++;
            }
        }
        return matrix;
    }

    public static double getCurrentEntropy(double[][] rankAcceptabilityIndices){
        double[] vector = new double[rankAcceptabilityIndices.length];
        for(int i = 0; i < rankAcceptabilityIndices.length; i++){
            vector[i] = rankAcceptabilityIndices[i][0];
        }
        return calculateEntropy(vector);
    }

    public static  double[][] getEntropyPreference(double[][][] objectPotentialPreferenceAcceptabilityIndices){
        double[][] entropyMatrixArray = new double[objectPotentialPreferenceAcceptabilityIndices[0].length][];
        double[] vectorArray = new double[objectPotentialPreferenceAcceptabilityIndices.length];
        for(int i = 0; i < objectPotentialPreferenceAcceptabilityIndices[0].length; i++){
            double[] cellArray = new double[objectPotentialPreferenceAcceptabilityIndices[0][i].length];
            for(int k = 0; k < objectPotentialPreferenceAcceptabilityIndices[0][i].length; k++){
                int entropyCounter = 0;
                for(int object = 0; object < vectorArray.length; object++){
                    if(objectPotentialPreferenceAcceptabilityIndices[object][i][k] != -1){
                        vectorArray[object] = objectPotentialPreferenceAcceptabilityIndices[object][i][k];
                        entropyCounter++;
                    }else {
                        vectorArray[object] = -1;
                    }
                }
                double[] entropyArray = new double[entropyCounter];
                entropyCounter = 0;
                for(int l = 0; l < vectorArray.length; l++){
                    if(vectorArray[entropyCounter] != -1){
                        entropyArray[entropyCounter] = vectorArray[l];
                        entropyCounter++;
                    }
                }
                cellArray[k] = calculateEntropy(entropyArray);
                entropyMatrixArray[i] = cellArray;
            }
        }
        return entropyMatrixArray;
    }

    public static double[][][] getEntropyMatrix(double[][][][] objectPotentialJudgementAcceptabilityIndices){
        double[][][] entropyMatrixArray = new double[objectPotentialJudgementAcceptabilityIndices[0].length][objectPotentialJudgementAcceptabilityIndices[0].length][];
        double[] vectorArray = new double[objectPotentialJudgementAcceptabilityIndices.length];
        for(int i = 0; i < objectPotentialJudgementAcceptabilityIndices[0].length; i++){
            for(int j = 0; j < objectPotentialJudgementAcceptabilityIndices[0][i].length; j++){
                double[] cellArray = new double[objectPotentialJudgementAcceptabilityIndices[0][i][j].length];
                for(int k = 0; k < objectPotentialJudgementAcceptabilityIndices[0][i][j].length; k++){
                    int entropyCounter = 0;
                    for(int object = 0; object < vectorArray.length; object++){
                        if(objectPotentialJudgementAcceptabilityIndices[object][i][j][k] != -1){
                            vectorArray[object] = objectPotentialJudgementAcceptabilityIndices[object][i][j][k];
                            entropyCounter++;
                        }else {
                            vectorArray[object] = -1;
                        }
                    }
                    double[] entropyArray = new double[entropyCounter];
                    entropyCounter = 0;
                    for(int l = 0; l < vectorArray.length; l++){
                        if(vectorArray[entropyCounter] != -1){
                            entropyArray[entropyCounter] = vectorArray[l];
                            entropyCounter++;
                        }
                    }
                    cellArray[k] = calculateEntropy(entropyArray);
                    entropyMatrixArray[i][j] = cellArray;
                }
            }
        }
        return entropyMatrixArray;
    }

    public static double calculateEntropy(double[] vector) {
        Double entropy = 0.0;
        Double sum = 0.0;

        for (Double value : vector) {
            sum += value;
        }

        for (Double value : vector) {
            if (value != 0.0) {
                Double probability = value / sum;
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }

        return entropy;
    }




    public static void scaleAggregatedMatrixMap(double[][][][] objectPotentialJudgementAcceptabilityIndices){
        for(int object = 0; object < objectPotentialJudgementAcceptabilityIndices.length; object++){
            for(int i = 0; i < objectPotentialJudgementAcceptabilityIndices[object].length; i++){
                for(int j = 0; j < objectPotentialJudgementAcceptabilityIndices[object][i].length; j++){
                    for (int k = 0; k < objectPotentialJudgementAcceptabilityIndices[object][i][j].length; k++) {
                        objectPotentialJudgementAcceptabilityIndices[object][i][j][k] /= iteration;
                    }
                }
            }
        }
    }

    public static void scaleAggregatedWeightsMap(double[][][] objectPotentialPreferenceAcceptabilityIndices){
        for(int object = 0; object < objectPotentialPreferenceAcceptabilityIndices.length; object++){
            for(int i = 0; i < objectPotentialPreferenceAcceptabilityIndices[object].length; i++){
                for (int k = 0; k < objectPotentialPreferenceAcceptabilityIndices[object][i].length; k++) {
                    objectPotentialPreferenceAcceptabilityIndices[object][i][k] /= iteration;
                }
            }
        }
    }

    public static void scaleTotalRankingPositions(double[][] totalRankingPositions){
        for(int i = 0; i < totalRankingPositions.length; i++){
            for(int j = 0; j < totalRankingPositions[i].length; j++){
                totalRankingPositions[j][i] = totalRankingPositions[j][i] / iteration;
            }
        }
    }

    public static void fillPotentialAggregatedWeightsRankingMap(double[][][] objectCurrentPreferenceAcceptabilityIndices,
                                                                double[][][] objectPotentialPreferenceAcceptabilityIndices){
        for(int object = 0; object < objectCurrentPreferenceAcceptabilityIndices.length; object++){
            for(int i = 0; i < objectCurrentPreferenceAcceptabilityIndices[object].length; i++) {
                int multiplicator = objectCurrentPreferenceAcceptabilityIndices[object][i].length;
                for (int k = 0; k < objectPotentialPreferenceAcceptabilityIndices[object][i].length; k++) {
                    objectPotentialPreferenceAcceptabilityIndices[object][i][k] *= multiplicator;
                }
            }
        }
    }

    public static void fillPotentialJudgementAcceptabilityIndices(double[][][][] objectCurrentJudgementAcceptabilityIndices,
                                                                  double[][][][] objectPotentialJudgementAcceptabilityIndices){
        for(int object = 0; object < objectCurrentJudgementAcceptabilityIndices.length; object++){
            for(int i = 0; i < objectCurrentJudgementAcceptabilityIndices[object].length; i++){
                for(int j = 0; j < objectCurrentJudgementAcceptabilityIndices[object][i].length; j++){
                    int multiplicator = objectCurrentJudgementAcceptabilityIndices[object][i][j].length;
                    for (int k = 0; k < objectCurrentJudgementAcceptabilityIndices[object][i][j].length; k++) {
                        objectPotentialJudgementAcceptabilityIndices[object][i][j][k] *= multiplicator;
                    }
                }
            }
        }
    }

    public static void countByRankingAndDecision(int[] rankingPosition,
                                                 double[][][][] objectCurrentJudgementAcceptabilityIndices,
                                                 int[][] sawMatrix){
        for(int i = 0; i < rankingPosition.length; i++){
            if(rankingPosition[i] == 1){
                //add for rank one the counter for Judgements from saw by one
                addRankOneToCJAI(i, objectCurrentJudgementAcceptabilityIndices, sawMatrix);
            }
        }


    }

    public static void addRankOneToCJAI(int rankOne,
                                        double[][][][] objectCurrentJudgementAcceptabilityIndices,
                                        int[][] sawMatrix){
        for(int i = 0; i < sawMatrix.length; i++){
            for(int j = 0; j < sawMatrix[i].length; j++){
                //get old value
                try{
                    objectCurrentJudgementAcceptabilityIndices[rankOne][i][j][sawMatrix[i][j]]++;
                }catch(Exception e){
                    System.out.println();
                }
            }
        }
    }

    public static void countByRankingAndWeights(int[] rankingPosition, double[][][] currentPreferenceAcceptabilityIndices, int[] sawWeights){
        int rankOne = -1;
        for(int i = 0; i < rankingPosition.length; i++){
            if(rankingPosition[i] == 1){
                rankOne = i;
            }
        }
        for(int i = 0; i < sawWeights.length; i++){
            currentPreferenceAcceptabilityIndices[rankOne][i][sawWeights[i]]++;
        }
    }

    public static void initWeightsMap(int[][] aggregatedWeights, double[][] aggregatedWeightsRankingMap){
        for(int j = 0; j < aggregatedWeights.length; j++){
            //get highest number
            int highest = 0;
            for(int k = 0; k < aggregatedWeights[j].length; k++){
                if(aggregatedWeights[j][k] > highest){
                    highest = aggregatedWeights[j][k];
                }
            }
            highest++;
            aggregatedWeightsRankingMap[j] = new double[highest];
            //fill array with 0 or -1
            for(int k = 0; k < highest; k++){
                if(elementContainsInArray(k, aggregatedWeights[j])){
                    aggregatedWeightsRankingMap[j][k] = 0;
                }else{
                    aggregatedWeightsRankingMap[j][k] = -1;
                }

            }
        }

    }

    public static void initMatrixMap(int[][][] aggregatedMatrix, double[][][] aggregatedMatrixRankingMap){
        for(int i = 0; i < aggregatedMatrix.length; i++){
            aggregatedMatrixRankingMap[i] = new double[aggregatedMatrix.length][];
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                aggregatedMatrixRankingMap[i][j] = new double[aggregatedMatrix[i].length];
                //get highest number
                int highest = 0;
                for(int k = 0; k < aggregatedMatrix[i][j].length; k++){
                    if(aggregatedMatrix[i][j][k] > highest){
                        highest = aggregatedMatrix[i][j][k];
                    }
                }
                highest++;
                aggregatedMatrixRankingMap[i][j] = new double[highest];
                //fill array with 0 or -1
                for(int k = 0; k < highest; k++){
                    if(elementContainsInArray(k, aggregatedMatrix[i][j])){
                        aggregatedMatrixRankingMap[i][j][k] = 0;
                    }else{
                        aggregatedMatrixRankingMap[i][j][k] = -1;
                    }

                }
            }
        }
    }



    @NotNull
    public static int[][][] generateAggregatedMatrix(@NotNull int[][][] dMList){
        int[][][] aggregatedMatrix = new int[dMList.length][dMList[0].length][];

        for (int alt = 0; alt < dMList[0].length; alt++) {
            for (int crit = 0; crit < dMList[0][0].length; crit++) {
                List<Integer> aggregatedList = new ArrayList<>();
                for (int decisionMaker = 0; decisionMaker < dMList.length; decisionMaker++) {
                    if(!aggregatedList.contains(dMList[decisionMaker][alt][crit])){
                        aggregatedList.add(dMList[decisionMaker][alt][crit]);
                    }
                }
                int[] aggregatedArray = new int[aggregatedList.size()];
                for(int s = 0; s < aggregatedList.size(); s++){
                    aggregatedArray[s] = aggregatedList.get(s);
                }
                aggregatedMatrix[alt][crit] = aggregatedArray;

            }

        }

        return aggregatedMatrix;
    }

    public static boolean elementContainsInArray(int elem, int[] array){
        for(int number : array){
            if(elem == number){
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static int[][] generateAggregatedWeights(@NotNull int[][] dMWList){
        int[][] aggregatedWeights = new int[dMWList.length][];

        for(int crit = 0; crit < dMWList[0].length; crit++){
            List<Integer> aggregatedList = new ArrayList<>();
            for (int[] ints : dMWList) {
                if (!aggregatedList.contains(ints[crit])) {
                    aggregatedList.add(ints[crit]);
                }
            }
            int[] aggregatedArray = new int[aggregatedList.size()];
            for(int s = 0; s < aggregatedList.size(); s++){
                aggregatedArray[s] = aggregatedList.get(s);
            }
            aggregatedWeights[crit] = aggregatedArray;
        }


        return aggregatedWeights;
    }

    @NotNull
    public static int[][] generateDecisionMakerWeightList(int number, int length, boolean lex){
        int[][] dMWList = new int[number][length];
        int min = 0;
        int max;
        if(lex){
            max = LexJudgements.values().length;
        }else{
            max = FuzzyJudgements.values().length;
        }
        for(int i = 0; i < number; i++){
            dMWList[i] = Helper.generate1DArray(length, min, max);
        }

        return dMWList;
    }

    @NotNull
    public static int[][][] generateDecisionMakerList(int number, int row, int col, boolean lex){
        int min = 0;
        int max;
        if(lex){
            max = LexJudgements.values().length;
        }else{
            max = FuzzyJudgements.values().length;
        }
        int[][][] dMList = new int[number][row][col];
        for(int i = 0; i < number; i++){
            dMList[i] = Helper.generate2DArray(row, col, min, max);
        }

        return dMList;
    }

    public static void addRanking(double[][] totalRanking, int[] newRanking){
        double i1;
        int x;
        for(int i = 0; i < newRanking.length; i++){
            x =  newRanking[i] - 1;
            i1 = totalRanking[i][x];
            i1 += 1;
            totalRanking[i][x] = i1;
        }


    }

    public static int[] getRanksArray(double[] array) {
        int[] result = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            int count = 0;
            for (double aDouble : array) {
                if ( aDouble >  array[i]) {
                    count++;
                }
            }
            result[i] = count + 1;
        }
        return result;
    }


}
