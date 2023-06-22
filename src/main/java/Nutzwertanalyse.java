import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static Enums.FuzzyJudgements.*;
import static Enums.FuzzyPreferenzes.*;
import static Enums.FuzzyPreferenzes.L;

public class Nutzwertanalyse {
    public static final int row = 3;
    public static final int col = 3;
    public static final int numberOfDecisionMaker = 10;
    public static void main(String[] args) {
        ArrayList<Object[][]> decisionMakerList = MonteCarloHelper.generateDecisionMakerList(FuzzyJudgements.class, numberOfDecisionMaker, row, col, 1, 10);
        ArrayList<Object[]> decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(FuzzyPreferenzes.class, numberOfDecisionMaker, row, 0, 1);
//        ArrayList<Object>[][] aggregatedMatrix = generateAggregatedMatrix(decisionMakerList);
//        ArrayList<Object>[] aggregatedWeights = generateAggregatedWeights(decisionMakerWeightsList);

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

        MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights);

//        double entropy = calculateEntropy(array);
    }




}
