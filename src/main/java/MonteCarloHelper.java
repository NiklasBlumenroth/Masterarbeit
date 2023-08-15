import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MonteCarloHelper {
    public static int monteCarloIterations = 10_000;
    public static int iteration;
    public static int row;
    public static int col;

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

    public static Map<String, Object> showMonteCarloSaw(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, boolean full){
        row = aggregatedMatrix.length;
        col = aggregatedWeights.length;

        System.out.println("\nAggregated Matrix");
        Helper.show2DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        Helper.show1DArray(aggregatedWeights);

        System.out.println("\nAggregated K: " + getMatrixK(aggregatedMatrix, aggregatedWeights));

        Object[][] sawMatrix = null;
        Object[] sawWeights = null;
        Object[] rankingTotalPoints;
        Object[] rankingPosition;
        Object[][] rankAcceptabilityIndices = new Object[row][col];
        ArrayList<SawFullIterationObject> fullIterations = null;

        //fill ranking counter with 0
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                rankAcceptabilityIndices[i][j] = 0.0;
            }
        }

        // generate map for counting
        Map<Object, Object>[][] currentJudgementAcceptabilityIndices = new Map[row][col];
        Map<Object, Object>[][] potentialJudgementAcceptabilityIndices = new Map[row][col];
        Map<Object, Object>[] currentPreferenceAcceptabilityIndices = new Map[row];
        Map<Object, Object>[] potentialPreferencesAcceptabilityIndices = new Map[row];

        //fill matrix map with 0
        fillMatrixMapWithZero(aggregatedMatrix, currentJudgementAcceptabilityIndices);
        fillMatrixMapWithZero(aggregatedMatrix, potentialJudgementAcceptabilityIndices);
        //fill weights map with 0
        fillWeightsMapWithZero(aggregatedWeights, currentPreferenceAcceptabilityIndices);
        fillWeightsMapWithZero(aggregatedWeights, potentialPreferencesAcceptabilityIndices);

        //create currentJudgementAcceptabilityIndices and currentPreferenceAcceptabilityIndices
        //create potentialJudgementAcceptabilityIndices and potentialPreferenceAcceptabilityIndices
        ArrayList<Map<Object, Object>[][]> objectCurrentJudgementAcceptabilityIndices = new ArrayList<>();
        //[index for ai is winning][row][col][map for possible decisions]
        ArrayList<Map<Object, Object>[][]> objectPotentialJudgementAcceptabilityIndices = new ArrayList<>();
        System.out.println("\nSaw Weights:");
        for(int j = 0; j < row; j++){
            objectCurrentJudgementAcceptabilityIndices.add(currentJudgementAcceptabilityIndices);
            currentJudgementAcceptabilityIndices = new Map[row][col];
            fillMatrixMapWithZero(aggregatedMatrix, currentJudgementAcceptabilityIndices);

            objectPotentialJudgementAcceptabilityIndices.add(potentialJudgementAcceptabilityIndices);
            potentialJudgementAcceptabilityIndices = new Map[row][col];
            fillMatrixMapWithZero(aggregatedMatrix, potentialJudgementAcceptabilityIndices);
        }

        ArrayList<Map<Object, Object>[]> objectCurrentPreferenceAcceptabilityIndices = new ArrayList<>();
        ArrayList<Map<Object, Object>[]> objectPotentialPreferenceAcceptabilityIndices = new ArrayList<>();
        for(int j = 0; j < row; j++){
            objectCurrentPreferenceAcceptabilityIndices.add(currentPreferenceAcceptabilityIndices);
            currentPreferenceAcceptabilityIndices = new Map[col];
            fillWeightsMapWithZero(aggregatedWeights, currentPreferenceAcceptabilityIndices);

            objectPotentialPreferenceAcceptabilityIndices.add(potentialPreferencesAcceptabilityIndices);
            potentialPreferencesAcceptabilityIndices = new Map[col];
            fillWeightsMapWithZero(aggregatedWeights, potentialPreferencesAcceptabilityIndices);
        }

        //monteCarloSimulation
        iteration = (full) ? getIteration(aggregatedMatrix, aggregatedWeights) : monteCarloIterations;

        if (full){
            fullIterations = getSawFullIterationObjects(aggregatedMatrix, aggregatedWeights);
        }

        for(int i = 0; i < iteration; i++){
            if(full){
                sawMatrix = fullIterations.get(i).getSawMatrix();
                sawWeights = fullIterations.get(i).getSawWeights();
            }else{
                sawMatrix = getSawMatrix(aggregatedMatrix, col, row);
                sawWeights = getSawWeights(aggregatedWeights, col);
            }

            rankingTotalPoints = Helper.saw(sawMatrix, sawWeights);

            System.out.println("\n Ranking total points");
            Helper.show1DArray(rankingTotalPoints);
            rankingPosition = getRanksArray(rankingTotalPoints);
            if(doubleOne(rankingPosition)){

            }
//            System.out.println("\n Ranking total points");
//            Helper.show1DArray(rankingTotalPoints);
//            System.out.println("\nMatrix");
//            Helper.show2DArray(sawMatrix);
//            System.out.println("\nWeight");
//            Helper.show1DArray(sawWeights);
//            System.out.println("\n Ranking positions");
//            Helper.show1DArray(rankingPosition);
            addRanking(rankAcceptabilityIndices, rankingPosition);
            System.out.println("\n new rankAcceptabilityIndices");
            Helper.show2DArray(rankAcceptabilityIndices);
            //sawMatrix + ranking = countingMatrixRankingMap
            countByRankingAndDecision(rankingPosition, objectCurrentJudgementAcceptabilityIndices, sawMatrix);
            //sawWeights + ranking = countingWeightsRankingMap
            countByRankingAndWeights(rankingPosition, objectCurrentPreferenceAcceptabilityIndices, sawWeights);
        }

        System.out.println("\nAggregated Matrix");
        Helper.show2DArray(aggregatedMatrix);

        System.out.println("\nAggregated Weight");
        Helper.show1DArray(aggregatedWeights);

        System.out.println("\nAggregated K: " + getMatrixK(aggregatedMatrix, aggregatedWeights));

        System.out.println("\nfinal rankAcceptabilityIndices normalized");
//        normalizeTotalRankingPositions(rankAcceptabilityIndices);
        Helper.showAcceptabilityIndices(rankAcceptabilityIndices);

        for(int j = 0; j < row; j++){
            System.out.println("\ncurrentJudgementAcceptabilityIndex for a" + j);
            Helper.show2DArray(objectCurrentJudgementAcceptabilityIndices.get(j));
        }

        for (int i = 0; i < col; i++){
            System.out.println("\ncurrentPreferenceAcceptabilityIndex for a" + i);
            Helper.show1DArray(objectCurrentPreferenceAcceptabilityIndices.get(i));
        }

        fillPotentialJudgementAcceptabilityIndices(objectCurrentJudgementAcceptabilityIndices, objectPotentialJudgementAcceptabilityIndices);
        normalizeAggregatedMatrixMap(objectPotentialJudgementAcceptabilityIndices);
        for(int j = 0; j < row; j++){
            System.out.println("\npotentialJudgementAcceptabilityIndex for a" + j);
            Helper.show2DArray(objectPotentialJudgementAcceptabilityIndices.get(j));
        }

        fillPotentialAggregatedWeightsRankingMap(objectCurrentPreferenceAcceptabilityIndices, objectPotentialPreferenceAcceptabilityIndices);
        normalizeAggregatedWeightsMap(objectPotentialPreferenceAcceptabilityIndices);
        for (int i = 0; i < col; i++){
            System.out.println("\npotentialPreferenceAcceptabilityIndex for a" + i);
            Helper.show1DArray(objectPotentialPreferenceAcceptabilityIndices.get(i));
        }

        Map<Object, Double>[][] judgementEntropyMatrix = getEntropyMatrix(objectPotentialJudgementAcceptabilityIndices);
        System.out.println("\njudgementEntropyMatrix");
        Helper.show2DArray(judgementEntropyMatrix);

        Map<Object, Double>[] preferenceEntropy = getEntropyPreference(objectPotentialPreferenceAcceptabilityIndices);
        System.out.println("\npreferenceEntropy");
        Helper.show1DArray(preferenceEntropy);

        System.out.println("\ncurrent entropy");
        System.out.println(getCurrentEntropy(rankAcceptabilityIndices));


        return getLowestValue(judgementEntropyMatrix, preferenceEntropy);
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

    private static ArrayList<SawFullIterationObject> getSawFullIterationObjects(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights) {
        //aggregatedMatrix
        List<List<Object>> fullIterationObjects = new ArrayList<>();
        for(int i = 0; i < aggregatedMatrix.length; i++){
            fullIterationObjects.addAll(Arrays.asList(aggregatedMatrix[i]));
        }
        List<List<Object>> cartesianProduct = CartesianProduct.cartesianProduct(fullIterationObjects);
//        for (List<Object> product : cartesianProduct) {
//            System.out.println(product);
//        }
        //aggregatedWeights
        List<List<Object>> fullIterationObjects2 = new ArrayList<>();
        fullIterationObjects2.addAll(Arrays.asList(aggregatedWeights));

        List<List<Object>> cartesianProduct2 = CartesianProduct.cartesianProduct(fullIterationObjects2);
//        for (List<Object> product : cartesianProduct2) {
//            System.out.println(product);
//        }
//        System.out.println();

        ArrayList<SawFullIterationObject> iterations = new ArrayList<>();
        for(List<Object> var : cartesianProduct){
            for(List<Object> var2 : cartesianProduct2){
                SawFullIterationObject sawFullIterationObject = new SawFullIterationObject();
                sawFullIterationObject.setSawMatrix(listToMatrix(var, aggregatedMatrix.length));
                sawFullIterationObject.setSawWeights(listToWeights(var2, aggregatedWeights.length));
                iterations.add(sawFullIterationObject);
            }
        }
//        System.out.println();

        return iterations;
    }

    public static Object[] listToWeights(List<Object> list, int matrixLength){
        Object[] weight = new Object[matrixLength];
        for(int i = 0; i < list.size(); i++){
            weight[i] = list.get(i);
        }
        return weight;
    }

    public static Object[][] listToMatrix(List<Object> list, int matrixLength){
        Object[][] matrix = new Object[matrixLength][list.size() / matrixLength];
        int counter = 0;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                matrix[i][j] = list.get(counter);
                counter++;
            }
        }
        return matrix;
    }

    private static int getIteration(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights) {
        int iteration = 1;
        for(ArrayList<Object>[] rows : aggregatedMatrix){
            for(ArrayList<Object> cols : rows){
                iteration *= cols.size();
            }
        }

        for(ArrayList<Object> cols : aggregatedWeights){
            iteration *= cols.size();
        }
        return iteration;
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

    public static void normalizeAggregatedMatrixMap(ArrayList<Map<Object, Object>[][]> objectPotentialJudgementAcceptabilityIndices){
        Double integer;
        for(int object = 0; object < objectPotentialJudgementAcceptabilityIndices.size(); object++){
            for(int i = 0; i < objectPotentialJudgementAcceptabilityIndices.get(object).length; i++){
                for(int j = 0; j < objectPotentialJudgementAcceptabilityIndices.get(object)[i].length; j++){
                    for (Map.Entry<Object, Object> entry : objectPotentialJudgementAcceptabilityIndices.get(object)[i][j].entrySet()) {
                        integer = (Double)entry.getValue();
                        integer /= (Integer) iteration;
                        entry.setValue(integer);
                    }
                }
            }
        }
    }

    public static void normalizeAggregatedWeightsMap(ArrayList<Map<Object, Object>[]> objectPotentialJudgementAcceptabilityIndices){
        Double integer;
        for(int object = 0; object < objectPotentialJudgementAcceptabilityIndices.size(); object++){
            for(int i = 0; i < objectPotentialJudgementAcceptabilityIndices.get(object).length; i++){
                for (Map.Entry<Object, Object> entry : objectPotentialJudgementAcceptabilityIndices.get(object)[i].entrySet()) {
                    integer = (Double)entry.getValue();
                    integer /= (Integer) iteration;
                    entry.setValue(integer);
                }
            }
        }
    }

    public static void normalizeTotalRankingPositions(Object[][] totalRankingPositions){
        //add values in a col to get integer /= ...
        Double integer;
        for(int i = 0; i < totalRankingPositions.length; i++){
            for(int j = 0; j < totalRankingPositions[i].length; j++){
                integer = (Double) totalRankingPositions[j][i] * 1.0;
                integer /= iteration;
                totalRankingPositions[j][i] = integer;
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

    public static void countByRankingAndDecision(Object[] rankingPosition,
                                                 ArrayList<Map<Object, Object>[][]> objectCurrentJudgementAcceptabilityIndices,
                                                 Object[][] sawMatrix){
        for(int i = 0; i < rankingPosition.length; i++){
            if(Objects.equals(rankingPosition[i], 1)){
                //add for rank one the counter for Judgements from saw by one
                addRankOneToCJAI(i, objectCurrentJudgementAcceptabilityIndices, sawMatrix);
            }
        }


    }

    public static void addRankOneToCJAI(Integer rankOne,
                                        ArrayList<Map<Object, Object>[][]> objectCurrentJudgementAcceptabilityIndices,
                                        Object[][] sawMatrix){
        for(int i = 0; i < sawMatrix.length; i++){
            for(int j = 0; j < sawMatrix[i].length; j++){
                //get old value
                Double value = (Double) objectCurrentJudgementAcceptabilityIndices.get(rankOne)[i][j].get(sawMatrix[i][j]);
                value += 1;
                //set new value
                objectCurrentJudgementAcceptabilityIndices.get(rankOne)[i][j].put(sawMatrix[i][j], value);
            }
        }
    }

    public static void countByRankingAndWeights(Object[] rankingPosition, ArrayList<Map<Object, Object>[]> currentPreferenceAcceptabilityIndices, Object[] sawWeights){
        Integer rankOne = null;
        for(int i = 0; i < rankingPosition.length; i++){
            if(Objects.equals(rankingPosition[i], 1)){
                rankOne = i;
            }
        }
        for(int i = 0; i < sawWeights.length; i++){
            //get old value
            Double value = (Double) currentPreferenceAcceptabilityIndices.get(rankOne)[i].get(sawWeights[i]);
            value += 1;
            //set new value
            currentPreferenceAcceptabilityIndices.get(rankOne)[i].put(sawWeights[i], value);
        }
    }

    public static void fillWeightsMapWithZero(ArrayList<Object>[] aggregatedWeights, Map<Object, Object>[] aggregatedWeightsRankingMap){
        for(int i = 0; i < aggregatedWeights.length; i++){
            //create cell
            Map<Object, Object> newMap = new HashMap<>();
            for(int k = 0; k < aggregatedWeights[i].size(); k++){
                //set countingMap to value
                newMap.put(aggregatedWeights[i].get(k), 0.0);
            }
            aggregatedWeightsRankingMap[i] = newMap;
        }
    }

    public static void fillMatrixMapWithZero(ArrayList<Object>[][] aggregatedMatrix, Map<Object, Object>[][] aggregatedMatrixRankingMap){
        for(int i = 0; i < aggregatedMatrix.length; i++){
            for(int j = 0; j < aggregatedMatrix[i].length; j++){
                //create cell
                Map<Object, Object> newMap = new HashMap<>();
                for(int k = 0; k < aggregatedMatrix[i][j].size(); k++){
                    //set countingMap to value
                    newMap.put(aggregatedMatrix[i][j].get(k), 0.0);
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
//        System.out.println("Generates decisionMakerWeightsList: ");

        for(int i = 0; i < number; i++){
            matrix = Helper.generate2DArray(clazz, row, col, min, max);
//            Helper.show2DArray(matrix);
//            System.out.println(" ");
            dMList.add(matrix);
        }

        return dMList;
    }

    public static void addRanking(Object[][] totalRanking, Object[] newRanking){
        Double i1;
        Integer x;
        for(int i = 0; i < newRanking.length; i++){
            x = (Integer) newRanking[i] - 1;
            i1 = (Double)totalRanking[i][x];
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
