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

//        System.out.println("\nAggregated Matrix");
//        Helper.show2DArray(aggregatedMatrix);

        int[][] judgementCombinationList = getJudgementCombinations(aggregatedMatrix);

//        System.out.println("\nAggregated Weight");
//        Helper.show1DArray(aggregatedWeights);
        int[][] preferenceCombinationList = getPreferenceCombinations(aggregatedWeights, lex);

        k = judgementCombinationList.length * preferenceCombinationList.length;
//        System.out.println("\nAggregated K: " + k);

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
        fillMatrixMapWithZero(aggregatedMatrix, currentJudgementAcceptabilityIndices);
        fillMatrixMapWithZero(aggregatedMatrix, potentialJudgementAcceptabilityIndices);
////        //fill weights map with 0
        fillWeightsMapWithZero(aggregatedWeights, currentPreferenceAcceptabilityIndices);
        fillWeightsMapWithZero(aggregatedWeights, potentialPreferencesAcceptabilityIndices);

        //create currentJudgementAcceptabilityIndices and currentPreferenceAcceptabilityIndices
        //create potentialJudgementAcceptabilityIndices and potentialPreferenceAcceptabilityIndices
        //alternativen, row, col, counter (index + counter)
        double[][][][] objectCurrentJudgementAcceptabilityIndices = new double[alternative][][][];
        double[][][][] objectPotentialJudgementAcceptabilityIndices = new double[alternative][][][];
        for(int j = 0; j < alternative; j++){
            //add one matrix for each possible winner
            objectCurrentJudgementAcceptabilityIndices[j] = currentJudgementAcceptabilityIndices.clone();
        }

        double[][][] objectCurrentPreferenceAcceptabilityIndices = new double[alternative][][];
        double[][][] objectPotentialPreferenceAcceptabilityIndices = new double[alternative][][];
        for(int j = 0; j < alternative; j++){
            objectCurrentPreferenceAcceptabilityIndices[j] = currentPreferenceAcceptabilityIndices.clone();
        }

        //monteCarloSimulation
        iteration = (full) ? k : monteCarloIterations;
        int prefCounter = 0;
        int jugCounter = 0;
        for(int i = 0; i < iteration - 1; i++){
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
                rankingTotalPoints = Helper.decisionMethod(sawMatrix, sawWeights, false, lex);
            }else {
                rankingTotalPoints = Helper.decisionMethod(sawMatrix, sawWeights, false, lex);
            }


            rankingPosition = getRanksArray(rankingTotalPoints);
            addRanking(rankAcceptabilityIndices, rankingPosition);

            //sawMatrix + ranking = countingMatrixRankingMap
            countByRankingAndDecision(rankingPosition, objectCurrentJudgementAcceptabilityIndices, sawMatrix);
            //sawWeights + ranking = countingWeightsRankingMap
            countByRankingAndWeights(rankingPosition, objectCurrentPreferenceAcceptabilityIndices, sawWeights);

            if(false){
//                System.out.println("\nMatrix");
//                Helper.show2DArray(sawMatrix);
//                System.out.println("\nWeight");
//                Helper.show1DArray(sawWeights);
//                System.out.println("\n rankingTotalPoints");
//                Helper.show1DArray(rankingTotalPoints);
//                System.out.println("\n rankingPosition");
//                Helper.show1DArray(rankingPosition);
//                System.out.println("\n new rankAcceptabilityIndices");
//                Helper.show2DArray(rankAcceptabilityIndices);
//                System.out.println("\n new objectCurrentJudgementAcceptabilityIndices");
//                Helper.show2DArray(objectCurrentJudgementAcceptabilityIndices.get(0));
            }

        }

//        System.out.println("\nAggregated Matrix");
//        Helper.show2DArray(aggregatedMatrix);
//
//        System.out.println("\nAggregated Weight");
//        Helper.show1DArray(aggregatedWeights);
//
//        System.out.println("\nAggregated K: " + k);
//
//        System.out.println("\nfinal rankAcceptabilityIndices ");
//        Helper.showAcceptabilityIndices(rankAcceptabilityIndices);
//
//        System.out.println("\nfinal rankAcceptabilityIndices scaled");
        scaleTotalRankingPositions(rankAcceptabilityIndices);
//        Helper.showAcceptabilityIndices(rankAcceptabilityIndices);

        scaleAggregatedMatrixMap(objectCurrentJudgementAcceptabilityIndices);
//        for(int j = 0; j < row; j++){
//            System.out.println("\ncurrentJudgementAcceptabilityIndex for a" + j);
//            Helper.show2DArray(objectCurrentJudgementAcceptabilityIndices.get(j));
//        }

        scaleAggregatedWeightsMap(objectCurrentPreferenceAcceptabilityIndices);
//        for (int i = 0; i < col; i++){
//            System.out.println("\ncurrentPreferenceAcceptabilityIndex for a" + i);
//            Helper.show1DArray(objectCurrentPreferenceAcceptabilityIndices.get(i));
//        }

//        fillPotentialJudgementAcceptabilityIndices(objectCurrentJudgementAcceptabilityIndices, objectPotentialJudgementAcceptabilityIndices);
//        scaleAggregatedMatrixMap(objectPotentialJudgementAcceptabilityIndices);
//        for(int j = 0; j < row; j++){
//            System.out.println("\npotentialJudgementAcceptabilityIndex for a" + j);
//            Helper.show2DArray(objectPotentialJudgementAcceptabilityIndices.get(j));
//        }

//        fillPotentialAggregatedWeightsRankingMap(objectCurrentPreferenceAcceptabilityIndices, objectPotentialPreferenceAcceptabilityIndices);
//        scaleAggregatedWeightsMap(objectPotentialPreferenceAcceptabilityIndices);
//        for (int i = 0; i < col; i++){
//            System.out.println("\npotentialPreferenceAcceptabilityIndex for a" + i);
//            Helper.show1DArray(objectPotentialPreferenceAcceptabilityIndices.get(i));
//        }

//        Map<Object, Double>[][] judgementEntropyMatrix = getEntropyMatrix(objectPotentialJudgementAcceptabilityIndices);
//        System.out.println("\njudgementEntropyMatrix");
//        Helper.show2DArray(judgementEntropyMatrix);

//        Map<Object, Double>[] preferenceEntropy = getEntropyPreference(objectPotentialPreferenceAcceptabilityIndices);
//        System.out.println("\npreferenceEntropy");
//        Helper.show1DArray(preferenceEntropy);

//        System.out.println("\ncurrent entropy");
//        System.out.println(getCurrentEntropy(rankAcceptabilityIndices));
//        Date date = new Date();
//        System.out.println("Date end: " + date);
        date = new Date();
        System.out.println("Ende: " + date);
        return null;//getLowestValue(judgementEntropyMatrix, preferenceEntropy);
    }

    public static double[][][] copyIntToDouble(int[][][] currentJudgementAcceptabilityIndices){
        return null;
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
        int kCounter = 1;
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

    public static List<List<Object>> cutInvalidIterationsForLex(List<List<Object>> cartesianProduct2){
        List<List<Object>> list = new ArrayList<>();
        for(int i = 0; i < cartesianProduct2.size(); i++){
            if(!hasDouble(cartesianProduct2.get(i))){
                list.add(cartesianProduct2.get(i));
            }
        }
        return list;
    }

    public static boolean hasDouble(List<Object> arr){
        for(int j = 0; j < arr.size(); j++){
            Object elem = arr.get(j);
            for(int i = j + 1; i < arr.size(); i++){
                if(arr.get(i).equals(elem)){
                    return true;
                }
            }
        }

        return false;
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

    public static Double getCurrentEntropy(Object[][] rankAcceptabilityIndices){
        Double[] vector = new Double[rankAcceptabilityIndices.length];
        for(int i = 0; i < rankAcceptabilityIndices.length; i++){
            vector[i] = (Double) rankAcceptabilityIndices[i][0];
        }
        return calculateEntropy(vector);
    }

    public static  Map<Object, Double>[] getEntropyPreference(ArrayList<Map<Object, Object>[]> objectPotentialPreferenceAcceptabilityIndices){
        Map<Object, Double>[] entropyMatrix = new Map[objectPotentialPreferenceAcceptabilityIndices.get(0).length];
        Double[] vector = new Double[objectPotentialPreferenceAcceptabilityIndices.size()];

        for(int i = 0; i < objectPotentialPreferenceAcceptabilityIndices.get(0).length; i++){
            Map<Object, Double> map = new HashMap<>();
            for (Map.Entry<Object, Object> entry : objectPotentialPreferenceAcceptabilityIndices.get(0)[i].entrySet()) {
                for(int object = 0; object < vector.length; object++){
                    vector[object] = (Double) objectPotentialPreferenceAcceptabilityIndices.get(object)[i].get(entry.getKey());
                }
                map.put(entry.getKey(), calculateEntropy(vector));
                entropyMatrix[i] = map;
            }
        }

        return entropyMatrix;
    }

    public static  Map<Object, Double>[][] getEntropyMatrix(ArrayList<Map<Object, Object>[][]> objectPotentialJudgementAcceptabilityIndices){
        Map<Object, Double>[][] entropyMatrix = new Map[objectPotentialJudgementAcceptabilityIndices.get(0).length][objectPotentialJudgementAcceptabilityIndices.get(0).length];
        Double[] vektor = new Double[objectPotentialJudgementAcceptabilityIndices.size()];

            for(int i = 0; i < objectPotentialJudgementAcceptabilityIndices.get(0).length; i++){
                for(int j = 0; j < objectPotentialJudgementAcceptabilityIndices.get(0)[i].length; j++){
                    Map<Object, Double> map = new HashMap<>();
                    for (Map.Entry<Object, Object> entry : objectPotentialJudgementAcceptabilityIndices.get(0)[i][j].entrySet()) {
                        for(int object = 0; object < vektor.length; object++){
                            vektor[object] = (Double) objectPotentialJudgementAcceptabilityIndices.get(object)[i][j].get(entry.getKey());
                        }
                        map.put(entry.getKey(), calculateEntropy(vektor));
                        entropyMatrix[i][j] = map;
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

    public static void fillPotentialAggregatedWeightsRankingMap(ArrayList<Map<Object, Object>[]> objectCurrentPreferenceAcceptabilityIndices,
                                                                ArrayList<Map<Object, Object>[]> objectPotentialPreferenceAcceptabilityIndices){
        for(int object = 0; object < objectCurrentPreferenceAcceptabilityIndices.size(); object++){
            for(int i = 0; i < objectCurrentPreferenceAcceptabilityIndices.get(object).length; i++) {
                Integer multiplicator = objectCurrentPreferenceAcceptabilityIndices.get(object)[i].size();
                for (Map.Entry<Object, Object> entry : objectPotentialPreferenceAcceptabilityIndices.get(object)[i].entrySet()) {
                    entry.setValue((Double)objectCurrentPreferenceAcceptabilityIndices.get(object)[i].get(entry.getKey()) * multiplicator);
                }
            }
        }
    }

    public static void fillPotentialJudgementAcceptabilityIndices(ArrayList<Map<Object, Object>[][]> objectCurrentJudgementAcceptabilityIndices,
                                                                  ArrayList<Map<Object, Object>[][]> objectPotentialJudgementAcceptabilityIndices){
        for(int object = 0; object < objectCurrentJudgementAcceptabilityIndices.size(); object++){
            for(int i = 0; i < objectCurrentJudgementAcceptabilityIndices.get(object).length; i++){
                for(int j = 0; j < objectCurrentJudgementAcceptabilityIndices.get(object)[i].length; j++){
                    Integer multiplicator = objectCurrentJudgementAcceptabilityIndices.get(object)[i][j].size();
                    for (Map.Entry<Object, Object> entry : objectPotentialJudgementAcceptabilityIndices.get(object)[i][j].entrySet()) {
                        entry.setValue((Double)objectCurrentJudgementAcceptabilityIndices.get(object)[i][j].get(entry.getKey()) * multiplicator);
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
                objectCurrentJudgementAcceptabilityIndices[rankOne][i][j][sawMatrix[i][j]]++;
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

    public static void fillWeightsMapWithZero(int[][] aggregatedWeights, double[][] aggregatedWeightsRankingMap){
        for(int j = 0; j < aggregatedWeights.length; j++){
            //get highest number
            int highest = 0;
            for(int k = 0; k < aggregatedWeights[j].length; k++){
                if(aggregatedWeights[j][k] > highest){
                    highest = aggregatedWeights[j][k];
                }
            }
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

    public static void fillMatrixMapWithZero(int[][][] aggregatedMatrix, double[][][] aggregatedMatrixRankingMap){
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                //get highest number
                int highest = 0;
                for(int k = 0; k < aggregatedMatrix[i][j].length; k++){
                    if(aggregatedMatrix[i][j][k] > highest){
                        highest = aggregatedMatrix[i][j][k];
                    }
                }
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
        int[][][] aggregatedMatrix = new int[dMList.length][dMList[0].length][dMList[0][0].length];
        Helper.fill3dArrayWithNegOne(aggregatedMatrix);
        for (int k = 0; k < aggregatedMatrix.length; k++) {
            for (int i = 0; i < aggregatedMatrix[0].length; i++) {
                int counter = 0;
                for (int j = 0; j < aggregatedMatrix[0][0].length; j++) {
                    if (!elementContainsInArray(dMList[k][i][j], aggregatedMatrix[k][i])) {
                        aggregatedMatrix[k][i][counter] = dMList[k][i][j];
                        counter++;
                    }
                }
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
        int[][] aggregatedWeights = new int[dMWList.length][dMWList[0].length];
        Helper.fill2dArrayWithNegOne(aggregatedWeights);
        //fill empty lists
        for(int k = 0; k < aggregatedWeights.length; k++){
            int counter = 0;
            for(int j = 0; j < aggregatedWeights[k].length; j++){
                if(!elementContainsInArray(dMWList[k][j], aggregatedWeights[k])){
                    aggregatedWeights[k][counter] = dMWList[k][j];
                    counter++;
                }
            }
        }

        return aggregatedWeights;
    }

    @NotNull
    public static int[][] generateDecisionMakerWeightList(int number, int length, int min, int max){
        int[][] dMWList = new int[number][length];
        for(int i = 0; i < number; i++){
            dMWList[i] = Helper.generate1DArray(length, min, max);
        }

        return dMWList;
    }

    @NotNull
    public static int[][][] generateDecisionMakerList(int number, int row, int col, int min, int max){
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
