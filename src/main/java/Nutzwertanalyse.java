import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.*;

import static Enums.FuzzyJudgements.*;
import static Enums.FuzzyPreferenzes.*;
import static Enums.LexPreferenzes.*;
import static Enums.LexJudgements.*;

public class Nutzwertanalyse {
    public static final int row = 3;
    public static final int col = 3;
    public static final int numberOfDecisionMaker = 10;
    public static void main(String[] args) {
        ArrayList<Object[][]> decisionMakerList = MonteCarloHelper.generateDecisionMakerList(FuzzyJudgements.class, numberOfDecisionMaker, row, col, 1, 10);
//        ArrayList<Object[]> decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(FuzzyPreferenzes.class, numberOfDecisionMaker, row, 0, 1);
//        ArrayList<Object>[][] aggregatedMatrix = generateAggregatedMatrix(decisionMakerList);
//        ArrayList<Object>[] aggregatedWeights = generateAggregatedWeights(decisionMakerWeightsList);

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

        ArrayList<Object>[] aggregatedWeights = new ArrayList[]{
                new ArrayList<>(){{add(PA); add(PC); add(PD);}},
                new ArrayList<>(){{add(PE); add(PF);}},
                new ArrayList<>(){{add(PF); add(PE); add(PG);}}
        };


        Map<String, Object> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, true);
        for (Object key: lowestValue.keySet()) {
            System.out.println(key + " : " + lowestValue.get(key));
        }

        while ((Double)lowestValue.get("lowestValue") != 0){
            getIdealPath(aggregatedMatrix, aggregatedWeights, lowestValue);
            lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, false);
            for (Object key: lowestValue.keySet()) {
                System.out.println(key + " : " + lowestValue.get(key));
            }
        }


        /*
        27.07. 10 Uhr

        -> Formular in Google formular übernehmen
        -> Miro board erstellen und tabellen übernehmen
        -> Zielvereinbarungen überarbeiten -> Beispiel von Jana ansehen
        -> lexikografisches Entscheidungsmodell : Schlüko

         */



        /*
            -> lexikografisches Entscheidungsmodell : Schlüko
            -> pfadlängen bestimmen für mehrere durchläufe(simulationen)

            -> Formular für Fragen danach
            -> Formular Judgements
            -> Formular Titel (Alles selbsterklärend)

            -> erster Testlauf
            17.8. 10:30 Uhr
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

//                        new ArrayList<>(){{add(P);}},//current entropie müsste somit 1.08... betragen siehe excel
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