import java.util.*;

public class Nutzwertanalyse {
    public static ArrayList<Object>[][] getMatrix() {
//        return getLexMatrix();
        return getFuzzyMatrix();
    }

    public static ArrayList<Object>[] getWeights() {
//        return getLexWeights();
        return getFuzzyWeights();
    }
    public static double currentEntropy;
    public static void main(String[] args) {
        int[] alternatives = {5, 10, 15};
        int[] criteria = {3, 6};
        int[] numberOfDecisionMakers = {3, 6};
        boolean full = false;
        boolean useStaticProblem = false;
        boolean lex = false;
        boolean show = false;

        for(int alt : alternatives){
            for(int crit : criteria){
                for(int num : numberOfDecisionMakers){
                    rechnen(15, 6, 6, false, false, false, show);
                    //rechnen(alt, crit, num, full, lex, useStaticProblem, show);
                }
            }
        }
    }

    public static void rechnen(int alt, int crit, int numberOfDecisionMaker, boolean full, boolean lex, boolean useStaticProblem, boolean show){
        Date startDate = new Date();
        Date endDate = new Date();
        System.out.println("Start: " + startDate);
        for (int l = 0; l < 10; l++) {
            int[][][] aggregatedMatrix = null;
            int[][] aggregatedWeights = null;
            int[][][] decisionMakerList = null;
            int[][] decisionMakerWeightsList = null;
            if(useStaticProblem){
                //gets static problem matrix
                ArrayList<Object>[][] staticAggregatedMatrix = getMatrix();
                ArrayList<Object>[] staticAggregatedWeights = getWeights();
                //transfer static arraylist problem to matrix filled with judgements and -1
                aggregatedMatrix = transferStaticAggregatedMatrixToIntArray(staticAggregatedMatrix);
                aggregatedWeights = transferStaticAggregatedWeightToIntArray(staticAggregatedWeights);
            }else{
                decisionMakerList = MonteCarloHelper.generateDecisionMakerList(numberOfDecisionMaker, alt, crit, lex);
                decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(numberOfDecisionMaker, crit, lex);
                //generates aggregated matrix and fill with -1
                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            }

            int indivCounter = 0;
            double sum = 0;
            int durchlaeufe = 100;
            for (int k = 0; k < durchlaeufe; k++) {
                List<LowestValueObject> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show);
                indivCounter++;

                while (currentEntropy != 0) {
//                    getRandomPath(aggregatedMatrix, aggregatedWeights, lowestValue);
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show);
                    indivCounter++;
                }
                System.out.println("Pfadlänge: " + indivCounter);
                sum += indivCounter;
                indivCounter = 0;
            }
            endDate = new Date();
            System.out.println(l + " Durchschnittliche Pfadlänge = " + sum / durchlaeufe + " : " + endDate);
            sum = 0;
        }
        System.out.println("End: " + endDate);
    }

    public static void getIdealPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, Map<String, Object> lowestValue) {
        if ((Boolean) lowestValue.get("lowestValueIsJudgement")) {
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")] = new ArrayList<>();
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].add(lowestValue.get("lowestKey"));
        } else {
            aggregatedWeights[(Integer) lowestValue.get("lowestI")] = new ArrayList<>();
            aggregatedWeights[(Integer) lowestValue.get("lowestI")].add(lowestValue.get("lowestKey"));
        }
    }

    public static int[][][] transferStaticAggregatedMatrixToIntArray(ArrayList<Object>[][] staticAggregatedMatrix){
        //fill aggregatedMatrix
        int[][][] aggregatedMatrixArray = new int[staticAggregatedMatrix.length][staticAggregatedMatrix[0].length][];
        for(int i = 0; i < staticAggregatedMatrix.length; i++){
            for(int j = 0; j < staticAggregatedMatrix[i].length; j++){
                int[] var = new int[staticAggregatedMatrix[i][j].size()];
                for(int k = 0; k < staticAggregatedMatrix[i][j].size(); k++){
                    var[k] = (int)staticAggregatedMatrix[i][j].get(k);
                }
                aggregatedMatrixArray[i][j] = var;
            }
        }
        return aggregatedMatrixArray;
    }

    public static int[][] transferStaticAggregatedWeightToIntArray(ArrayList<Object>[] staticAggregatedWeight){
        int[][] aggregatedWeights = new int[staticAggregatedWeight.length][];
        for(int i = 0; i < staticAggregatedWeight.length; i++){
            int[] var = new int[staticAggregatedWeight[i].size()];
            for(int k = 0; k < staticAggregatedWeight[i].size(); k++){
                var[k] = (int)staticAggregatedWeight[i].get(k);
            }
            aggregatedWeights[i] = var;
        }
        return aggregatedWeights;
    }

    public static void getRandomPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, Map<String, Object> lowestValue) {
        Random random = new Random();
        if ((Boolean) lowestValue.get("lowestValueIsJudgement")) {
            Integer randomNumber = random.nextInt(aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].size());
            Object randomObject = aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].get(randomNumber);
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")] = new ArrayList<>();
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].add(randomObject);
        } else {
            Integer randomNumber = random.nextInt(aggregatedWeights[(Integer) lowestValue.get("lowestI")].size());
            Object randomObject = aggregatedWeights[(Integer) lowestValue.get("lowestI")].get(randomNumber);
            aggregatedWeights[(Integer) lowestValue.get("lowestI")] = new ArrayList<>();
            aggregatedWeights[(Integer) lowestValue.get("lowestI")].add(randomObject);
        }
    }

    public static ArrayList<Object>[][] getFuzzyMatrix() {
        return new ArrayList[][]{
                {
                        new ArrayList<>(){{add(2); add(1);}},
                        new ArrayList<>(){{add(3); add(5);}},
                        new ArrayList<>(){{add(4); add(6);}}
                },
                {
                        new ArrayList<>(){{add(2); add(6);}},
                        new ArrayList<>(){{add(3); add(4);}},
                        new ArrayList<>(){{add(3); add(5);}}
                },
                {
                        new ArrayList<>(){{add(4); add(1); add(6);}},
                        new ArrayList<>(){{add(5); add(1);}},
                        new ArrayList<>(){{add(3); add(1);}}
                }
        };
    }

    public static ArrayList<Object>[][] getLexMatrix() {
        return new ArrayList[][]{
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(2);}},
                        new ArrayList<>() {{add(0);}}
                },
                {
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);add(2);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);add(2);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(1);add(2);}},
                },
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);add(2);}},
                        new ArrayList<>() {{ add(0);}},
                        new ArrayList<>() {{ add(2);}},
                },
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}}
                },
                {
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}}
                },
                {
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(2);add(1);}},
                        new ArrayList<>() {{add(1);}}
                }
        };
    }

    public static ArrayList<Object>[] getLexWeights() {
        return new ArrayList[]{
                new ArrayList<>() {{add(0);add(1);add(2);add(3);add(4);}},
                new ArrayList<>() {{add(0);add(1);add(2);}},
                new ArrayList<>() {{add(0);add(1);add(2);add(5);}},
                new ArrayList<>() {{add(0);add(1);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(0);add(1);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(3);add(4);add(5);}}
        };
    }

    public static ArrayList<Object>[] getFuzzyWeights() {
        return new ArrayList[]{
                new ArrayList<>(){{add(5); add(2); add(1);}},
                new ArrayList<>(){{add(1); add(4);}},
                new ArrayList<>(){{add(3); add(5); add(1);}}
        };
    }
}