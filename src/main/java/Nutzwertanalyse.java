import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;
import Enums.LexPreferenzes;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import static Enums.FuzzyJudgements.*;
import static Enums.FuzzyPreferenzes.*;
import static Enums.LexPreferenzes.*;
import static Enums.LexJudgements.*;

public class Nutzwertanalyse {
    public static final int alt = 5;
    public static final int crit = 5;
    public static final int numberOfDecisionMaker = 5;
    public static final Class jugClazz = LexJudgements.class;
    public static final Class prefClazz = LexPreferenzes.class;
    public static final boolean full = true;
    public static final boolean show = false;

    public static ArrayList<Object>[][] getMatrix() {
      /*  return new ArrayList[][]{
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
                        new ArrayList<>(){{add(MG); add(P);add(VG);}},
                        new ArrayList<>(){{add(G); add(P);}},
                        new ArrayList<>(){{add(F); add(P);}},
                }
        };

        return new ArrayList[][]{
                {
                        new ArrayList<>(){{add(F); add(G); add(MG);}},
                        new ArrayList<>(){{add(F); add(MG);}},
                        new ArrayList<>(){{add(F); add(G);add(MG);}}
                },
                {
                        new ArrayList<>(){{add(F); add(G); add(MG);}},
                        new ArrayList<>(){{add(MG); add(G);}},
                        new ArrayList<>(){{add(F); add(MP);}}
                },
                {
                        new ArrayList<>(){{add(MG); add(G);}},
                        new ArrayList<>(){{add(MG); add(G);}},
                        new ArrayList<>(){{add(MG); add(G); add(MP); add(F);}},
                },
                {
                        new ArrayList<>(){{add(F); add(G);}},
                        new ArrayList<>(){{add(MG);add(F); add(G);}},
                        new ArrayList<>(){{add(MG); add(MP); add(F);}},
                }
        };

       */
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
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{ add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);add(JB);add(JC);}},
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JC);}},
                },
                {
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JB);}},
                        new ArrayList<>() {{add(JB);add(JC);}},
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JA);}}
                },
                {
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JB);}},
                        new ArrayList<>() {{add(JB);}},
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);add(JB);}}
                },
                {
                        new ArrayList<>() {{add(JB);}},
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JA);add(JB);}},
                        new ArrayList<>() {{add(JA);}},
                        new ArrayList<>() {{add(JC);add(JB);}},
                        new ArrayList<>() {{add(JB);}}
                }
        };

    }

    public static ArrayList<Object>[] getWeights() {
/*        return new ArrayList[]{
                new ArrayList<>(){{add(H); add(ML); add(L);}},
                new ArrayList<>(){{add(L); add(MH);}},
                new ArrayList<>(){{add(M); add(H); add(L);}}
        };
return new ArrayList[]{
                new ArrayList<>(){{add(M); add(MH); add(H);}},
                new ArrayList<>(){{add(H); add(MH);}},
                new ArrayList<>(){{add(M); add(H); add(ML);}}
        };
 */

        return new ArrayList[]{
                new ArrayList<>() {{add(PA);add(PB);add(PC);add(PD);add(PE);}},
                new ArrayList<>() {{add(PA);add(PB);add(PC);}},
                new ArrayList<>() {{add(PA);add(PB);add(PC);add(PF);}},
                new ArrayList<>() {{add(PA);add(PB);add(PD);add(PE);add(PF);}},
                new ArrayList<>() {{add(PA);add(PB);add(PD);add(PE);add(PF);}},
                new ArrayList<>() {{add(PD);add(PE);add(PF);}}
        };

    }
    private static String readTxt(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String txt = "";
        while((line = br.readLine()) != null){
            if(line.length() > 5){
                txt += "\n" + line;
            }
        }
        return txt;
    }

    private static int getLines(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        int counter = 0;
        String line;
        while((line = br.readLine()) != null){
            if(line.length() > 5){
                counter++;
            }
        }
        return counter;
    }

    private static boolean fileExist(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
            return false;
        }else {
            System.out.println("File already exists: " + fileName.substring(fileName.lastIndexOf("\\")));
            return true;
        }
    }
    private static void writeTxt(String fileName, String newText) throws IOException {
        File myObj = new File(fileName);
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        }
        String fileData = readTxt(fileName);
        fileData = newText  + fileData;
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(fileData.getBytes());
        fos.flush();
        fos.close();
    }

    public static double currentEntropy = 100;
    public static void rechne(int numberOfDecisionMaker, int alt, int crit, Class jugClazz, Class prefClazz) throws IOException, ParseException {
        String berechnungsName;

        if(jugClazz == LexJudgements.class){
            berechnungsName = "Lex " + numberOfDecisionMaker + " x " + alt + " x " + crit;
        }else {
            berechnungsName = "FuzzySAW " + numberOfDecisionMaker + " x " + alt + " x " + crit;
        }
        String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName + ".txt";
        fileExist(fileName);

        Date startDate = new Date();
        Date endDate = new Date();
        System.out.println("Start: " + startDate);
        double sum = 0;
        int durchlaeufe = 100;
        int probleme = 1000;
        double overAllSum = 0;
        int linesInFile = getLines(fileName);

        for (int l = linesInFile; l < probleme; l++) {
            ArrayList<Object[][]> decisionMakerList = MonteCarloHelper.generateDecisionMakerList(jugClazz, numberOfDecisionMaker, alt, crit, 1, 10);
            ArrayList<Object[]> decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(prefClazz, numberOfDecisionMaker, crit, 0, 1);
            ArrayList<Object>[][] aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
            ArrayList<Object>[] aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            aggregatedMatrix = getMatrix();
            aggregatedWeights = getWeights();
            List<List<Object>> preferenceCombinationList = MonteCarloHelper.getPreferenceCombinations(aggregatedWeights);
            if(preferenceCombinationList.size() == 0){
                l--;
                continue;
            }
            int indivCounter = 0;
            for (int k = 0; k < durchlaeufe; k++) {

                List<Map<String, Object>> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, show);
                indivCounter++;
                while (currentEntropy!=0) {
                    getIdealPath(aggregatedMatrix, aggregatedWeights, lowestValue.get(0));
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, show);
                    indivCounter++;
                }
                sum += indivCounter;
                indivCounter = 0;
                //aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                //aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            }
            endDate = new Date();
            writeTxt(fileName, l + " Durchschnittliche Pfadlänge = " + sum / durchlaeufe + " : " + endDate);
            System.out.println(l + " Durchschnittliche Pfadlänge = " + sum / durchlaeufe + " : " + endDate);

            overAllSum += sum;
            sum = 0;
        }
        System.out.println(berechnungsName + " done.");
        System.out.println("End: " + endDate);
    }

    public static void main(String[] args) throws IOException, ParseException {
        int[] numberOfDecisionMakers = {6};
        int[] alt = {15};
        int[] crit = {6};
        Class[] jugdClazz = {FuzzyJudgements.class};
        Class[] prefClazz = {FuzzyPreferenzes.class};


        for(int i = 0; i < numberOfDecisionMakers.length; i++){
            for (int j = 0; j < alt.length; j++){
                for(int k = 0; k < crit.length; k++){
                    for(int l = 0; l < jugdClazz.length; l++){
                        rechne(numberOfDecisionMakers[i], alt[j], crit[k], jugdClazz[l], prefClazz[l]);
                    }
                }
            }
        }

        /*
        19.10 13 Uhr

        - adm und its
        - nicht quadratisches problem testen
        - berechnungszeit checken
            + probleme generieren, aggregierte generieren
            + berechnungsmethode(lex, saw)
            + kombinationen
            + statistik Matrizen
        Berechnung:
            - 1 Pfad mit idealauflösung 3,5,5, 7 klassen, 1000 Probleme
            - 100 Pfade mit zufallsauflösung
            - wenn k unter 1000 soll voll gerechnet werden
            - 1000 Probleme
            - Anzahl der DM:    3,4,5,6
            - Anzahl der Crit:  3,4,5,6,7,8,9,10
            - Anzahl der Alt:   3,4,5,6
        - zufällige pfade wählen
        - Fuzzy 5 lex 5 als Standard
        - danach fuzzy 3 lex 3
        - danach fuzzy 7 lex 7
        - nicht nur die niedrigsten Werte der entropie ausgeben lassen sondern die niedrigsten 3-5

         + logging studie
         + Grafiken Fuzzy Saw simulation
         + preferences cut
         + bug
         - aufbau arbeit
         + prüfungsamt

    // random path... mehrstufig kürzen zum checken
    // input checken?
    // start entropie(current entropie vor dem ersten mal rechen) und mittlere ausgeben lassen(wenn pfadlänge 1 ist, und die entropie hoch ist macht es keinen sinn)
    // current entropie soll 0 sein zum abbrechen, da danach erst der zufällige pfad gerechnet wird
         */

    }

    public static boolean containsZero(List<Map<String, Object>> lowestValue){
        for(Map map : lowestValue){
            if((Double) map.get("lowestValue") == 0){
               return true;
            }
        }
        return false;
    }

    public static void getIdealPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, Map<String, Object> lowestValue) {
        boolean lex = aggregatedWeights[0].get(0).getClass() == LexPreferenzes.class;
        if ((Boolean) lowestValue.get("lowestValueIsJudgement")) {
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")] = new ArrayList<>();
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].add(lowestValue.get("lowestKey"));
        } else {
            Object idealObject = lowestValue.get("lowestValue");
            int index = (Integer) lowestValue.get("lowestI");
            if(lex){
                optimizePref(aggregatedWeights, idealObject, index);
            }
        }
    }

    public static void getRandomPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, List<Map<String, Object>>  lowestValue) {
        Random random = new Random();
        boolean lex = aggregatedWeights[0].get(0).getClass() == LexPreferenzes.class;

        for(Map<String, Object> map : lowestValue){
            if ((Boolean) map.get("lowestValueIsJudgement")) {
                if(aggregatedMatrix[(Integer) map.get("lowestI")][(Integer) map.get("lowestJ")].size() > 1){
                    Integer randomNumber = random.nextInt(aggregatedMatrix[(Integer) map.get("lowestI")][(Integer) map.get("lowestJ")].size());
                    Object randomObject = aggregatedMatrix[(Integer) map.get("lowestI")][(Integer) map.get("lowestJ")].get(randomNumber);
                    aggregatedMatrix[(Integer) map.get("lowestI")][(Integer) map.get("lowestJ")] = new ArrayList<>();
                    aggregatedMatrix[(Integer) map.get("lowestI")][(Integer) map.get("lowestJ")].add(randomObject);
                    break;
                }
            } else {
                if(aggregatedWeights[(Integer) map.get("lowestI")].size() > 1){
                    Integer randomNumber = random.nextInt(aggregatedWeights[(Integer) map.get("lowestI")].size());
                    Object randomObject = aggregatedWeights[(Integer) map.get("lowestI")].get(randomNumber);
                    if(lex){
                        optimizePref(aggregatedWeights, randomObject, (Integer) map.get("lowestI"));
                    }
                    break;
                }

            }
        }
    }

    public static void optimizePref(ArrayList<Object>[] aggregatedWeights, Object lowestValueObject, int index){
        for(int i = 0; i < aggregatedWeights.length; i++){
            if(i == index){
                aggregatedWeights[index] = new ArrayList<>();
                aggregatedWeights[index].add(lowestValueObject);
            }else {
                aggregatedWeights[i].remove(lowestValueObject);
            }
        }

    }
}
//        return new ArrayList[][]{
//                {
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JB);}},
//                        new ArrayList<>() {{add(JC);}},
//                        new ArrayList<>() {{add(JA);}}
//                },
//                {
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JA);add(JB);add(JC);}},
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);add(JB);add(JC);}},
//                        new ArrayList<>() {{add(JB);}},
//                        new ArrayList<>() {{add(JB);add(JC);}},
//                },
//                {
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{ add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);add(JB);add(JC);}},
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JC);}},
//                },
//                {
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JB);}},
//                        new ArrayList<>() {{add(JB);add(JC);}},
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JA);}}
//                },
//                {
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JB);}},
//                        new ArrayList<>() {{add(JB);}},
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);add(JB);}}
//                },
//                {
//                        new ArrayList<>() {{add(JB);}},
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JA);add(JB);}},
//                        new ArrayList<>() {{add(JA);}},
//                        new ArrayList<>() {{add(JC);add(JB);}},
//                        new ArrayList<>() {{add(JB);}}
//                }
//        };

//        return new ArrayList[]{
//                new ArrayList<>() {{add(PA);add(PB);add(PC);add(PD);add(PE);}},
//                new ArrayList<>() {{add(PA);add(PB);add(PC);}},
//                new ArrayList<>() {{add(PA);add(PB);add(PC);add(PF);}},
//                new ArrayList<>() {{add(PA);add(PB);add(PD);add(PE);add(PF);}},
//                new ArrayList<>() {{add(PA);add(PB);add(PD);add(PE);add(PF);}},
//                new ArrayList<>() {{add(PD);add(PE);add(PF);}}
//        };