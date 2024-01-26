import lombok.SneakyThrows;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Nutzwertanalyse {
    public static ArrayList<Object>[][] getMatrix(boolean lex) {
//        return getTestLexBiotechMatrix();
        if(lex){
            return getLexMatrix();
        }
        return getFuzzyMatrix();
    }

    public static ArrayList<Object>[] getWeights(boolean lex) {
//        return getTestLexBiotechWeights();
        if(lex){
            return getLexWeights();
        }
        return getFuzzyWeights();
    }

    public static double currentEntropy;
    public static double calculateMaxEntropy;
    public static String logPath = System.getProperty("user.dir") + "/src/main/resources/logs/";
    public static String fileNameLex;
    public static String fileNameFuzzy;

    public static void main(String[] args) throws IOException {
        int[] alternatives = {5, 10, 15};
        int[] criteria = {3, 6};
        int[] numberOfDecisionMakers = {3, 6};

        boolean full = false;
        boolean useStaticProblem = false;
        boolean lex = true;
        boolean show = false;

//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss_MM_dd_yyyy");
//        Calendar c = Calendar.getInstance();
//        String curr_date = dateFormat.format(c.getTime());
//
//        fileName = logPath + curr_date +".txt";
//        fileExist(fileName);
//        Nutzwertanalyse.writeTxt("newText");
        for(int i = 0; i < 1001; i += 10){
//            rechnen(5, 5, 3, full, lex, useStaticProblem, show, i);
            for(int alt : alternatives){
                for(int crit : criteria){
                    for(int num : numberOfDecisionMakers){
                        if(num == 6 && alt == 15 && crit == 6){

                        }else {
                            //rechnen(alt, crit, num, full, lex, useStaticProblem, show, i);
                        }
                        //rechnen(6, 6, 6, full, lex, useStaticProblem, show, i);
                        rechnen(alt, crit, num, full, lex, useStaticProblem, show, i);
                    }
                }
            }
        }
    }
    public static boolean newProblem = false;
    public static boolean nextIsZero = false;
    public static int idealCounter = 0;
    public static void rechnen(int alt, int crit, int numberOfDecisionMaker, boolean full, boolean lex, boolean useStaticProblem, boolean show, int number) throws IOException {
        String berechnungsName;

        berechnungsName = "Lex " + numberOfDecisionMaker + " x " + alt + " x " + crit;
        fileNameLex = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName + ".txt";
        fileExist(fileNameLex);
        berechnungsName = "FuzzySAW " + numberOfDecisionMaker + " x " + alt + " x " + crit;
        fileNameFuzzy = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName + ".txt";
        fileExist(fileNameFuzzy);

        Date endDate = new Date();
        int[][][] aggregatedMatrix = null;
        int[][] aggregatedWeights = null;
        int[][][] decisionMakerList = null;
        int[][] decisionMakerWeightsList = null;
        int indivPathLength = 0;
        double avgPathLength = 0;
        int durchlaeufe = 100;

        int linesInFile = getLines(fileNameFuzzy);
        for (int l = linesInFile; l < number; l++) {
            if(useStaticProblem){
                //gets static problem matrix
                ArrayList<Object>[][] staticAggregatedMatrix = getMatrix(lex);
                ArrayList<Object>[] staticAggregatedWeights = getWeights(lex);
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
            lex = true;
            for (int k = 0; k < durchlaeufe; k++) {
                List<LowestValueObject> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show, useStaticProblem);

                if(lowestValue.size() == 0){
                    decisionMakerList = MonteCarloHelper.generateDecisionMakerList(numberOfDecisionMaker, alt, crit, lex);
                    decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(numberOfDecisionMaker, crit, lex);
                    //generates aggregated matrix and fill with -1
                    aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                    aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
                    k=-1;
                    newProblem = false;
                    System.out.println("new");
                    continue;
                }
                indivPathLength++;
                while (currentEntropy != 0) {//currentEntropy > calculateMaxEntropy * 0.1
                    getRandomPath(aggregatedMatrix, aggregatedWeights, lowestValue, lex);
                    if(newProblem){
                        indivPathLength = 0;
                        break;
                    }
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show, useStaticProblem);
                    indivPathLength++;
                }
//                System.out.println("idealCounter = " + idealCounter);
                idealCounter = 0;
                if(newProblem){
                    decisionMakerList = MonteCarloHelper.generateDecisionMakerList(numberOfDecisionMaker, alt, crit, lex);
                    decisionMakerWeightsList = MonteCarloHelper.generateDecisionMakerWeightList(numberOfDecisionMaker, crit, lex);
                    k--;
                    newProblem = false;
                }else {
                    //Nutzwertanalyse.writeTxt(k + ": Pfadlänge: " + indivPathLength);
                    avgPathLength += indivPathLength;
                }
                indivPathLength = 0;

                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            }
            if(!newProblem){
                endDate = new Date();
                Nutzwertanalyse.writeTxt(l + " Durchschnittliche Pfadlänge = " + avgPathLength / durchlaeufe + " : " + endDate);
                }
            avgPathLength = 0;

            aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
            aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            //FUZZY SAW
            lex = false;
            for (int k = 0; k < durchlaeufe; k++) {
                List<LowestValueObject> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show, useStaticProblem);
                indivPathLength++;
                while (currentEntropy != 0) {//currentEntropy > calculateMaxEntropy * 0.1
                    getRandomPath(aggregatedMatrix, aggregatedWeights, lowestValue, false);
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, false, show, useStaticProblem);
                    indivPathLength++;
                }
                //Nutzwertanalyse.writeTxtFuzzy("" + indivPathLength);
                idealCounter = 0;
                avgPathLength += indivPathLength;
                indivPathLength = 0;

                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            }

            if(!newProblem){
                endDate = new Date();
                Nutzwertanalyse.writeTxtFuzzy(l + " Durchschnittliche Pfadlänge = " + avgPathLength / durchlaeufe + " : " + endDate);
            }
            avgPathLength = 0;
        }
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

    private static void fileExist(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getName());
        }else {
            System.out.println("File already exists: " + fileName.substring(fileName.lastIndexOf("\\")+1));
        }
    }

    @SneakyThrows
    public static void writeTxt(String newText) {
        System.out.println(newText);
        FileWriter fw = new FileWriter(fileNameLex,true); //the true will append the new data
        fw.write(newText + "\n");//appends the string to the file
        fw.close();
    }

    @SneakyThrows
    public static void writeTxtFuzzy(String newText) {
        System.out.println(newText);
        FileWriter fw = new FileWriter(fileNameFuzzy,true); //the true will append the new data
        fw.write(newText + "\n");//appends the string to the file
        fw.close();
    }

    public static void getIdealPath(int[][][] aggregatedMatrix, int[][] aggregatedWeights, List<LowestValueObject> lowestValues, boolean lex) {
        for (LowestValueObject object : lowestValues) {
            if (object.isJudgement) {
                if (aggregatedMatrix[object.getI()][object.getJ()].length > 1) {
                    aggregatedMatrix[object.getI()][object.getJ()] = new int[]{object.getLowestKey()};
//                    System.out.println("Entropy: " + currentEntropy + object + " chosen: " + object.getLowestKey());
                    break;
                }

            } else {
                if (aggregatedWeights[object.getI()].length > 1) {
                    aggregatedWeights[object.getI()] = new int[]{object.getLowestKey()};
                    if (lex) validateWeights(aggregatedWeights);
                    break;
                }
            }
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

    public static void getRandomPath(int[][][] aggregatedMatrix, int[][] aggregatedWeights, List<LowestValueObject> lowestValues, boolean lex) {
        int chosen = -1;
        int ideal = -1;
        Random random = new Random();
        for(int i = 0; i < lowestValues.size(); i++){
            LowestValueObject object = lowestValues.get(i);
            if (object.isJudgement) {
                if(aggregatedMatrix[object.getI()][object.getJ()].length > 1){
                    int randomNumber = random.nextInt(aggregatedMatrix[object.getI()][object.getJ()].length);
                    int newObject = aggregatedMatrix[object.getI()][object.getJ()][randomNumber];
                    aggregatedMatrix[object.getI()][object.getJ()] = new int[]{newObject};
                    //System.out.println("Entropy: " + currentEntropy + object + " chosen: " + newObject);
                    if(newObject == object.getLowestKey()) idealCounter++;
                    break;
                }
            } else {
                if(aggregatedWeights[object.getI()].length > 1){
                    if(lex){
                        int randomNumber = random.nextInt(aggregatedWeights[object.getI()].length);
                        int randomObject = aggregatedWeights[object.getI()][randomNumber];
                        aggregatedWeights[object.getI()] = new int[]{randomObject};
                        validateWeights(aggregatedWeights);
                        //System.out.println("Entropy: " + currentEntropy + object + " chosen: " + randomObject);
                        break;
                    }else {
                        int randomNumber = random.nextInt(aggregatedWeights[object.getI()].length);
                        int randomObject = aggregatedWeights[object.getI()][randomNumber];
                        aggregatedWeights[object.getI()] = new int[]{randomObject};
                        //System.out.println("Entropy: " + currentEntropy + object + " chosen: " + randomObject);
                        if(randomObject == object.getLowestKey()) idealCounter++;
                        break;
                    }
                }
            }
        }
//        Nutzwertanalyse.writeTxt("Aggregated Matrix");
//        Helper.show3DArray(aggregatedMatrix);
//
//        Nutzwertanalyse.writeTxt("Aggregated Weight");
//        Helper.showAggregatedWeightsArray(aggregatedWeights);
    }
    public static void doubleCut(int[][] aggregatedWeights){
        for(int i = 0; i < aggregatedWeights.length; i++){
            if(aggregatedWeights[i].length == 2){
                //save couple
                Set<Integer> set = new HashSet<>();
                set.add(aggregatedWeights[i][0]);
                set.add(aggregatedWeights[i][1]);
                //search for another couple with same values
                for(int j = 0; j < aggregatedWeights.length; j++){
                    if(aggregatedWeights[j].length == 2 && i != j){
                        if(set.contains(aggregatedWeights[j][0]) && set.contains(aggregatedWeights[j][1])){
                            //cut numbers from set except for positions i and j
                            for(int k = 0; k < aggregatedWeights.length; k++){
                                if(k != i && k != j){
                                    aggregatedWeights[k] = cutLexRandomObject(aggregatedWeights[j], (Integer) set.toArray()[0]);
                                    aggregatedWeights[k] = cutLexRandomObject(aggregatedWeights[j], (Integer) set.toArray()[1]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public static void validateWeights(int[][] aggregatedWeights){
        for(int[] array : aggregatedWeights){
            //double cut
            //doubleCut(aggregatedWeights);
            //single cut
            cutIfHasOnlyOne(aggregatedWeights);
        }
        //little validate check
        for(int[] array : aggregatedWeights){
            if (array.length == 0) {
                newProblem = true;
                break;
            }
        }
    }

    public static void cutIfHasOnlyOne(int[][] aggregatedWeights){
        for(int i = 0; i < aggregatedWeights.length; i++){
            if(aggregatedWeights[i].length == 1){
                int randomObject = aggregatedWeights[i][0];
                for(int j = 0; j < aggregatedWeights.length; j++){
                    if(hasElem(aggregatedWeights[j], randomObject) && j != i){
                        aggregatedWeights[j] = cutLexRandomObject(aggregatedWeights[j], randomObject);
                    }
                }
            }
        }

    }

    public static boolean hasElem(int[] array, int object){
        for (int j : array) {
            if (j == object) return true;
        }
        return false;
    }

    public static int[] cutLexRandomObject(int[] array, int randomObject){
        int[] newArray = new int[array.length-1];
        int count = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] != randomObject){
                newArray[count] = array[i];
                count++;
            }
        }
        return newArray;
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

    public static ArrayList<Object>[][] getTestLexMatrix() {
        return new ArrayList[][]{
                {
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}}
                },
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(2);}},
                        new ArrayList<>() {{add(1);}}
                },
                {
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}}
                }
        };
    }

    public static ArrayList<Object>[] getTestLexWeights() {
        return new ArrayList[]{
                new ArrayList<>() {{add(0);add(1);}},
                new ArrayList<>() {{add(0);add(1);}},
                new ArrayList<>() {{add(2);}}
        };
    }

    public static ArrayList<Object>[][] getTestLexBiotechMatrix() {
        return new ArrayList[][]{
                {
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}},
                },
                {
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}},
                },
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                },
                {
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);}},
                },
                {
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(1);add(2);}},
                },
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);}},
                }
        };
    }

    public static ArrayList<Object>[] getTestLexBiotechWeights() {
        return new ArrayList[]{
                new ArrayList<>() {{add(1);add(2);add(3);add(4);}},
                new ArrayList<>() {{add(1);add(2);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(1);add(2);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(0);}},
                new ArrayList<>() {{add(1);add(2);add(3);}},
                new ArrayList<>() {{add(1);add(2);add(3);add(4);add(5);}},
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