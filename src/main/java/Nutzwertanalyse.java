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
    public static final int row = 3;
    public static final int col = 3;
    public static final int numberOfDecisionMaker = 3;
    public static final Class jugClazz = LexJudgements.class;
    public static final Class prefClazz = LexPreferenzes.class;

    public static ArrayList<Object>[][] getMatrix(){
        return new ArrayList[][]{
                {
                        new ArrayList<>(){{add(MP); add(P);}},
                        new ArrayList<>(){{add(F); add(G);}},
                        new ArrayList<>(){{add(MG); add(VG);}}
                },
                {
                        new ArrayList<>(){{add(MP); add(VG);}},
                        new ArrayList<>(){{add(F); add(MG);}},
                        new ArrayList<>(){{add(F); add(G);}}
                },
                {
                        new ArrayList<>(){{add(MG); add(P); add(VG);}},
                        new ArrayList<>(){{add(G); add(P);}},
                        new ArrayList<>(){{add(F); add(P);}}
                }
        };

//        return new ArrayList[][]{
//                {
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JB);}},
//                        new ArrayList<>(){{add(JC);}},
//                        new ArrayList<>(){{add(JA);}}
//                },
//                {
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JA); add(JB); add(JC);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB); add(JC);}},
//                        new ArrayList<>(){{add(JB);}},
//                        new ArrayList<>(){{add(JB); add(JC);}},
//                },
//                {
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB); add(JC);}},
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JC);}},
//                },
//                {
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JB);}},
//                        new ArrayList<>(){{add(JB); add(JC);}},
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JA);}}
//                },
//                {
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JB);}},
//                        new ArrayList<>(){{add(JB);}},
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA); add(JB);}}
//                },
//                {
//                        new ArrayList<>(){{add(JB);}},
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JA); add(JB);}},
//                        new ArrayList<>(){{add(JA);}},
//                        new ArrayList<>(){{add(JC); add(JB);}},
//                        new ArrayList<>(){{add(JB);}}
//                }
//        };
    }

    public static ArrayList<Object>[] getWeights(){
        return new ArrayList[]{
                new ArrayList<>(){{add(H); add(ML); add(L);}},
                new ArrayList<>(){{add(L); add(MH);}},
                new ArrayList<>(){{add(M); add(H); add(L);}}
        };

//        return new ArrayList[]{
//                new ArrayList<>(){{add(PA); add(PB); add(PC); add(PD); add(PE);}},
//                new ArrayList<>(){{add(PA); add(PB); add(PC);}},
//                new ArrayList<>(){{add(PA); add(PB); add(PC); add(PF);}},
//                new ArrayList<>(){{add(PA); add(PB); add(PD); add(PE); add(PF);}},
//                new ArrayList<>(){{add(PA); add(PB); add(PD); add(PE); add(PF);}},
//                new ArrayList<>(){{add(PD); add(PE); add(PF);}}
//        };
    }

    public static void main(String[] args) {
        Date startDate = new Date();
        Date endDate = new Date();
        System.out.println("Start: " + startDate);
        for(int l = 0; l < 10; l++){
            ArrayList<Object[][]> decisionMakerList = MonteCarloHelper.generateDecisionMakerList(jugClazz, numberOfDecisionMaker, row, col, 1, 10);
            ArrayList<Object[]> decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(prefClazz, numberOfDecisionMaker, row, 0, 1);
            ArrayList<Object>[][] aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
            ArrayList<Object>[] aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
                aggregatedMatrix = getMatrix();
                aggregatedWeights = getWeights();
            int indivCounter = 0;
            double sum = 0;
            int durchlaeufe = 100;
            for(int k = 0; k < durchlaeufe; k++){
                //System.out.println("\nAggregated Matrix");
                //Helper.show2DArray(aggregatedMatrix);

                //System.out.println("\nAggregated Weight");
                //Helper.show1DArray(aggregatedWeights);
                Map<String, Object> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, true);
                indivCounter++;
//                for (Object key: lowestValue.keySet()) {
//                    System.out.println(key + " : " + lowestValue.get(key));
//                }

                while ((Double)lowestValue.get("lowestValue") != 0){
                    getRandomPath(aggregatedMatrix, aggregatedWeights, lowestValue);
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, true);
                    indivCounter++;
//                    for (Object key: lowestValue.keySet()) {
//                        System.out.println(key + " : " + lowestValue.get(key));
//                    }
                }
                System.out.println("Pfadlänge: " + indivCounter);
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
            System.out.println(l + " Durchschnittliche Pfadlänge = " + sum/durchlaeufe + " : " + endDate);
            sum = 0;
        }
        System.out.println("End: " + endDate);

        /*
        14.09 Probeplanung 10Uhr
        - gruppe zusammenbekommen
        + nochmal über Formulare schauen + Kriterien formulieren(darauf achten, dass jeder weiß was genau bewertet wird)
        - Maxigliederung weiter arbeiten
         + nur pref aufstellen udn jug rng generieren wenn benötigt
         + montecarlo erst ab 1mio nutzen
         - vollrechnung nach optimierung

         // wie lange dauert es die vollrechnung zu machen? ein paar minuten


         Themen Probeexperimant:
         Restaurant
         Aktivitäten planen
         */

        /*
        5.10. 10 Uhr Uni
         Themen Probeexperiment:
         Restaurant
         Aktivitäten planen

        Formulare bearbeiten

         Formulare fürs anmelden WIP + Masterarbeit ausfüllen und ausdrucken
         */
    }

    public static void getIdealPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, Map<String, Object> lowestValue){
            if((Boolean) lowestValue.get("lowestValueIsJudgement")){
                aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")] = new ArrayList<>();
                aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].add(lowestValue.get("lowestKey"));
            }else{
                aggregatedWeights[(Integer) lowestValue.get("lowestI")] = new ArrayList<>();
                aggregatedWeights[(Integer) lowestValue.get("lowestI")].add(lowestValue.get("lowestKey"));
            }
    }

    public static void getRandomPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, Map<String, Object> lowestValue){
        Random random = new Random();
        if((Boolean) lowestValue.get("lowestValueIsJudgement")){
            Integer randomNumber = random.nextInt(aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].size());
            Object randomObject = aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].get(randomNumber);
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")] = new ArrayList<>();
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].add(randomObject);
        }else{
            Integer randomNumber = random.nextInt(aggregatedWeights[(Integer) lowestValue.get("lowestI")].size());
            Object randomObject = aggregatedWeights[(Integer) lowestValue.get("lowestI")].get(randomNumber);
            aggregatedWeights[(Integer) lowestValue.get("lowestI")] = new ArrayList<>();
            aggregatedWeights[(Integer) lowestValue.get("lowestI")].add(randomObject);
        }
    }

/*
ArrayList<Object>[][] aggregatedMatrix = new ArrayList[][]{
                {
                        new ArrayList<>(){{add(MP); add(P);}},
                        new ArrayList<>(){{add(F); add(G);}},
                        new ArrayList<>(){{add(MG); add(VG);}}
                },
                {
                        new ArrayList<>(){{add(MP); add(VG);}},
                        new ArrayList<>(){{add(F); add(MG);}},
                        new ArrayList<>(){{add(F); add(G);}}
                },
                {
                        new ArrayList<>(){{add(MG); add(P); add(VG);}},
                        new ArrayList<>(){{add(G); add(P);}},
                        new ArrayList<>(){{add(F); add(P);}}
                }
        };

        ArrayList<Object>[] aggregatedWeights = new ArrayList[]{
                new ArrayList<>(){{add(H); add(ML); add(L);}},
                new ArrayList<>(){{add(L); add(MH);}},
                new ArrayList<>(){{add(M); add(H); add(L);}}
        };
 */
    /*
    ArrayList<Object>[][] aggregatedMatrix = new ArrayList[][]{
                {
                        new ArrayList<>(){{add(JA); add(JC);}},
                        new ArrayList<>(){{add(JE); add(JG);}},
                        new ArrayList<>(){{add(JB); add(JD);}}
                },
                {
                        new ArrayList<>(){{add(JF); add(JA);}},
                        new ArrayList<>(){{add(JA); add(JB);}},
                        new ArrayList<>(){{add(JF); add(JG);}}
                },
                {
                        new ArrayList<>(){{add(JC); add(JA); add(JE);}},
                        new ArrayList<>(){{add(JB); add(JD);}},
                        new ArrayList<>(){{add(JE); add(JF);}}
                }
        };
// aufpassen beim auflösen von Problemen in den Diskussionen
        ArrayList<Object>[] aggregatedWeights = new ArrayList[]{
                new ArrayList<>(){{add(PA); add(PC); add(PD);}},
                new ArrayList<>(){{add(PE); add(PF);}},
                new ArrayList<>(){{add(PF); add(PE); add(PG);}}
        };

     */
}
/*
ArrayList<Object>[][] aggregatedMatrix = new ArrayList[][]{
                {
                        new ArrayList<>(){{add(VG);}},
                        new ArrayList<>(){{add(VP);}},
                        new ArrayList<>(){{add(P);}}
                },
                {
                        new ArrayList<>(){{add(G);}},
                        new ArrayList<>(){{add(VG);}},
                        new ArrayList<>(){{add(MP);}}
                },
                {
                        new ArrayList<>(){{add(VP);}},
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(F);}}
                }
        };

        ArrayList<Object>[] aggregatedWeights = new ArrayList[]{
                new ArrayList<>(){{add(H);}},
                new ArrayList<>(){{add(M);}},
                new ArrayList<>(){{add(ML);}}
        };

        ArrayList<Object>[][] aggregatedMatrix = new ArrayList[][]{
                {
                        new ArrayList<>(){{add(F);}},
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(G);}}
                },
                {
                        new ArrayList<>(){{add(P);}},
                        new ArrayList<>(){{add(VG);}},
                        new ArrayList<>(){{add(G);}}
                },
                {
                        new ArrayList<>(){{add(VP);}},
                        new ArrayList<>(){{add(G);}},
                        new ArrayList<>(){{add(F);}}
                }
        };

        ArrayList<Object>[] aggregatedWeights = new ArrayList[]{
                new ArrayList<>(){{add(L);}},
                new ArrayList<>(){{add(MH);}},
                new ArrayList<>(){{add(VH);}}
        };

        ArrayList<Object>[][] aggregatedMatrix = new ArrayList[][]{
                {
                        new ArrayList<>(){{add(MP);}},
                        new ArrayList<>(){{add(F);}},
                        new ArrayList<>(){{add(MG);}}
                },
                {
                        new ArrayList<>(){{add(MP);}},
                        new ArrayList<>(){{add(F);}},
                        new ArrayList<>(){{add(F);}}
                },
                {
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(G);}},
                        new ArrayList<>(){{add(F);}}
                }
        };

        ArrayList<Object>[] aggregatedWeights = new ArrayList[]{
                new ArrayList<>(){{add(H);}},
                new ArrayList<>(){{add(L);}},
                new ArrayList<>(){{add(M);}}
        };
 */