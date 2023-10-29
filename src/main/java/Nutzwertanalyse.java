import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;
import Enums.LexPreferenzes;

import java.util.*;

import static Enums.FuzzyJudgements.*;
import static Enums.FuzzyPreferenzes.*;
import static Enums.LexPreferenzes.*;
import static Enums.LexJudgements.*;

public class Nutzwertanalyse {
    public static final int row = 5;
    public static final int col = 5;
    public static final int numberOfDecisionMaker = 5;
    public static final Class jugClazz = FuzzyJudgements.class;
    public static final Class prefClazz = FuzzyPreferenzes.class;

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
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JB);}},
                        new ArrayList<>() {{add(JC);}},
                        new ArrayList<>() {{add(JA);}}
                },
                {
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JA);add(JB);add(JC);}},
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);add(JB);add(JC);}},
                        new ArrayList<>() {{add(JB);}},
                        new ArrayList<>() {{add(JB);add(JC);}},
                },
                {
                        new ArrayList<>() {{add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                            add(JC);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                        }},
                        new ArrayList<>() {{
                            add(JC);
                        }},
                },
                {
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JB);
                            add(JC);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                        }}
                },
                {
                        new ArrayList<>() {{
                            add(JA);
                        }},
                        new ArrayList<>() {{
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }}
                },
                {
                        new ArrayList<>() {{
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JA);
                        }},
                        new ArrayList<>() {{
                            add(JC);
                            add(JB);
                        }},
                        new ArrayList<>() {{
                            add(JB);
                        }}
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
                new ArrayList<>() {{add(PA);add(PB);add(PC);add(PD);add(PE);}},
                new ArrayList<>() {{add(PA);add(PB);add(PC);}},
                new ArrayList<>() {{add(PA);add(PB);add(PC);add(PF);}},
                new ArrayList<>() {{add(PA);add(PB);add(PD);add(PE);add(PF);}},
                new ArrayList<>() {{add(PA);add(PB);add(PD);add(PE);add(PF);}},
                new ArrayList<>() {{add(PD);add(PE);add(PF);}}
        };
    }

    public static void main(String[] args) {
        Date startDate = new Date();
        Date endDate = new Date();
        System.out.println("Start: " + startDate);
        double sum = 0;
        int durchlaeufe = 1;
        int probleme = 1000;
        for (int l = 0; l < probleme; l++) {
            ArrayList<Object[][]> decisionMakerList = MonteCarloHelper.generateDecisionMakerList(jugClazz, numberOfDecisionMaker, row, col, 1, 10);
            ArrayList<Object[]> decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(prefClazz, numberOfDecisionMaker, row, 0, 1);
            ArrayList<Object>[][] aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
            ArrayList<Object>[] aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            //aggregatedMatrix = getMatrix();
            //aggregatedWeights = getWeights();
            int indivCounter = 0;
            for (int k = 0; k < durchlaeufe; k++) {
                //System.out.println("\nAggregated Matrix");
                //Helper.show2DArray(aggregatedMatrix);

                //System.out.println("\nAggregated Weight");
                //Helper.show1DArray(aggregatedWeights);
                Map<String, Object> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, true);
                indivCounter++;
//                for (Object key: lowestValue.keySet()) {
//                    System.out.println(key + " : " + lowestValue.get(key));
//                }

                while ((Double) lowestValue.get("lowestValue") != 0) {
                    getIdealPath(aggregatedMatrix, aggregatedWeights, lowestValue);
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, false);
                    indivCounter++;
                }
//                System.out.println("Pfadlänge: " + indivCounter);
                sum += indivCounter;
                indivCounter = 0;
                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
//                aggregatedMatrix = getMatrix();
//                aggregatedWeights = getWeights();
            }
            decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(FuzzyPreferenzes.class, numberOfDecisionMaker, row, 0, 1);
            decisionMakerList = MonteCarloHelper.generateDecisionMakerList(FuzzyJudgements.class, numberOfDecisionMaker, row, col, 1, 10);
            aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
            aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            endDate = new Date();
            System.out.println(l + " Durchschnittliche Pfadlänge = " + sum / durchlaeufe + " : " + endDate);
//            sum = 0;
        }
        System.out.println(probleme+ " Durchschnittliche Pfadlänge = " + sum / durchlaeufe + " : " + endDate);
        System.out.println("End: " + endDate);

        /*
        19.10 13 Uhr

        - adm und its
        - nicht quadratisches problem testen
        - berechnungszeit checken
            + probleme generieren, aggregierte generieren
            + berechnungsmethode(lex, saw)
            + kombinationen
            - statistik Matrizen
        Berechnung:
            - 1 Pfad mit idealauflösung 3,5,5, 7 klassen, 1000 Probleme
            - 100 Pfade mit zufallsauflösung
            - wenn k unter 1000 soll voll gerechnet werden
            - 1000 Probleme
            - Anzahl der DM:    2,3,4,5,6
            - Anzahl der Crit:  3,4,5,6,7,8,9,10
            - Anzahl der Alt:   3,4,5,6
        - zufällige pfade wählen
        - Fuzzy 5 lex 5 als Standard
        - danach fuzzy 3 lex 3
        - danach fuzzy 7 lex 7
        - nicht nur die niedrigsten Werte der entropie ausgeben lassen sondern die niedrigsten 3-5
        Teststudie 24.10.
        - gedanken zum ersten intro machen und durchführen
        - flyer oder link für aktivitäten heraussuchen damit bewertet werden kann
        - mit vorgesetzten sprechen für zeitlichen ablauf
         */
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