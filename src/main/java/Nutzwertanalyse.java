import java.util.*;

public class Nutzwertanalyse {
    public static final int row = 3;
    public static final int col = 3;
    public static final int numberOfDecisionMaker = 3;
    public static final boolean useStaticProblem = true;
    public static final boolean lex = true;
    public static final boolean full = true;

    public static ArrayList<Object>[][] getMatrix() {
//        return new ArrayList[][]{
//                {
//                        new ArrayList<>(){{add(MP); add(P);}},
//                        new ArrayList<>(){{add(F); add(G);}},
//                        new ArrayList<>(){{add(MG); add(VG);}}
//                },
//                {
//                        new ArrayList<>(){{add(MP); add(VG);}},
//                        new ArrayList<>(){{add(F); add(MG);}},
//                        new ArrayList<>(){{add(F); add(G);}}
//                },
//                {
//                        new ArrayList<>(){{add(MG); add(P); add(VG);}},
//                        new ArrayList<>(){{add(G); add(P);}},
//                        new ArrayList<>(){{add(F); add(P);}}
//                }
//        };

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

    public static ArrayList<Object>[] getWeights() {
//        return new ArrayList[]{
//                new ArrayList<>(){{add(H); add(ML); add(L);}},
//                new ArrayList<>(){{add(L); add(MH);}},
//                new ArrayList<>(){{add(M); add(H); add(L);}}
//        };

        return new ArrayList[]{
                new ArrayList<>() {{add(0);add(1);add(2);add(3);add(4);}},
                new ArrayList<>() {{add(0);add(1);add(2);}},
                new ArrayList<>() {{add(0);add(1);add(2);add(5);}},
                new ArrayList<>() {{add(0);add(1);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(0);add(1);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(3);add(4);add(5);}}
        };
    }


    public static void main(String[] args) {
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
                decisionMakerList = MonteCarloHelper.generateDecisionMakerList(numberOfDecisionMaker, row, col, lex);
                decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(numberOfDecisionMaker, row, lex);
                //generates aggregated matrix and fill with -1
                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            }

            int indivCounter = 0;
            double sum = 0;
            int durchlaeufe = 100;
            for (int k = 0; k < durchlaeufe; k++) {
                double[][] lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex);
                indivCounter++;

                while (!containsZero(lowestValue)) {
//                    getRandomPath(aggregatedMatrix, aggregatedWeights, lowestValue);
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex);
                    indivCounter++;
                }
                System.out.println("Pfadlänge: " + indivCounter);
                sum += indivCounter;
                indivCounter = 0;
//                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
//                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
//                aggregatedMatrix = getMatrix();
//                aggregatedWeights = getWeights();
            }
//            decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(FuzzyPreferenzes.class, numberOfDecisionMaker, row, 0, 1);
//            decisionMakerList = MonteCarloHelper.generateDecisionMakerList(FuzzyJudgements.class, numberOfDecisionMaker, row, col, 1, 10);
            aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
            aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            endDate = new Date();
            System.out.println(l + " Durchschnittliche Pfadlänge = " + sum / durchlaeufe + " : " + endDate);
            sum = 0;
        }
        System.out.println("End: " + endDate);
        /*
        - adm und its
        - nicht quadratisches problem testen
        -> berechnungszeit checken
        Berechnung:
            - 1 Pfad mit idealauflösung 3,5,5, 7 klassen, 1000 Probleme
            - 100 Pfade mit zufallsauflösung
            - wenn k unter 1000 soll voll gerechnet werden
            - 1000 Probleme
            - Anzahl der DM:    2,3,4,5,6
            - Anzahl der Crit:  3,4,5,6,7,8,9,10
            - Anzahl der Alt:   3,4,5,6
            -
        - zufällige pfade wählen
        - Fuzzy 5 lex 5 als Standard
        - danach fuzzy 3 lex 3
        - danach fuzzy 7 lex 7
        - nicht nur die niedrigsten Werte der entropie ausgeben lassen sondern die niedrigsten 3-5
        Teststudie 24.10.
        gedanken zum ersten intro machen und durchführen
        -> flyer oder link für aktivitäten heraussuchen damit bewertet werden kann
        mit vorgesetzten sprechen für zeitlichen ablauf
         */
    }

    public static boolean containsZero(double[][] lowestValue){
        for(int i = 0; i < lowestValue.length; i++){
            if(lowestValue[i][0] == 0){
                return true;
            }
        }

        return false;
    }

    /*
    nachricht an thony mit den kriterien, alternativen
    nachricht zum erfragen für den 24.10.

    prüfungsamt schreiben
     */


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
}