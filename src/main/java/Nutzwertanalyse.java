import lombok.SneakyThrows;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Nutzwertanalyse {
    public static ArrayList<Object>[][] getMatrix(boolean lex) {
        return getTestLexMatrix();
//        if(lex){
//            return getLexMatrix();
//        }
//        return getFuzzyMatrix();
    }

    public static ArrayList<Object>[] getWeights(boolean lex) {
        return getTestLexWeights();
//        if(lex){
//            return getLexWeights();
//        }
//        return getFuzzyWeights();
    }

    public static double currentEntropy;
    public static String logPath = System.getProperty("user.dir") + "/src/main/resources/logs/";
    public static String fileName;

    public static void main(String[] args) throws IOException {
        int[] alternatives = {5, 10, 15};
        int[] criteria = {3, 6};
        int[] numberOfDecisionMakers = {3, 6};

        boolean full = true;
        boolean useStaticProblem = true;
        boolean lex = true;
        boolean show = false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss_MM_dd_yyyy");
        Calendar c = Calendar.getInstance();
        String curr_date = dateFormat.format(c.getTime());

        fileName = logPath + curr_date +".txt";
        fileExist(fileName);
        Nutzwertanalyse.writeTxt("newText");
        for(int alt : alternatives){
            for(int crit : criteria){
                for(int num : numberOfDecisionMakers){
                    rechnen(3, 3, 3, full, lex, useStaticProblem, show);
                    //rechnen(alt, crit, num, full, lex, useStaticProblem, show);
                }
            }
        }
    }

    public static void rechnen(int alt, int crit, int numberOfDecisionMaker, boolean full, boolean lex, boolean useStaticProblem, boolean show) throws IOException {
        Date startDate = new Date();
        Date endDate = new Date();
        int[][][] aggregatedMatrix = null;
        int[][] aggregatedWeights = null;
        int[][][] decisionMakerList = null;
        int[][] decisionMakerWeightsList = null;
        int indivPathLength = 0;
        double avgPathLength = 0;
        int durchlaeufe = 100;
        boolean newProblem = false;
        Nutzwertanalyse.writeTxt("Start: " + startDate);
        for (int l = 0; l < 10; l++) {
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

            for (int k = 0; k < durchlaeufe; k++) {
                List<LowestValueObject> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show, useStaticProblem);
                indivPathLength++;
                System.out.println(currentEntropy);
                while (currentEntropy != 0) {
                    if(!getRandomPath(aggregatedMatrix, aggregatedWeights, lowestValue, lex)){
                        indivPathLength = 0;
                        newProblem = true;
                        break;
                    }
                    lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, lex, show, useStaticProblem);
                    indivPathLength++;
                    System.out.println(currentEntropy);
                }
                if(!newProblem){
                    Nutzwertanalyse.writeTxt("Pfadlänge: " + indivPathLength);
                    avgPathLength += indivPathLength;
                }
                newProblem = false;
                indivPathLength = 0;
                aggregatedMatrix = MonteCarloHelper.generateAggregatedMatrix(decisionMakerList);
                aggregatedWeights = MonteCarloHelper.generateAggregatedWeights(decisionMakerWeightsList);
            }
            endDate = new Date();
            Nutzwertanalyse.writeTxt(l + " Durchschnittliche Pfadlänge = " + avgPathLength / durchlaeufe + " : " + endDate);
            avgPathLength = 0;
        }
        Nutzwertanalyse.writeTxt("End: " + endDate);
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
            Nutzwertanalyse.writeTxt("File created: " + file.getName());
        }else {
            Nutzwertanalyse.writeTxt("File already exists: " + fileName.substring(fileName.lastIndexOf("\\")-1));
        }
    }

    @SneakyThrows
    public static void writeTxt(String newText) {
        System.out.println(newText);
        FileWriter fw = new FileWriter(fileName,true); //the true will append the new data
        fw.write(newText + "\n");//appends the string to the file
        fw.close();
    }

    public static boolean getIdealPath(int[][][] aggregatedMatrix, int[][] aggregatedWeights, List<LowestValueObject> lowestValues, boolean lex) {
        for (LowestValueObject object : lowestValues) {
            if (object.isJudgement) {
                if (aggregatedMatrix[object.getI()][object.getJ()].length > 1) {
                    aggregatedMatrix[object.getI()][object.getJ()] = new int[]{object.getLowestKey()};
                    return true;
                }
            } else {
                if (aggregatedWeights[object.getI()].length > 1) {
                    aggregatedWeights[object.getI()] = new int[]{object.getLowestKey()};
                    if (lex) return validateWeights(aggregatedWeights);
                }
            }
        }
        return true;
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

    public static boolean getRandomPath(int[][][] aggregatedMatrix, int[][] aggregatedWeights, List<LowestValueObject> lowestValues, boolean lex) {
        Random random = new Random();
        for(int i = 0; i < lowestValues.size(); i++){
            LowestValueObject object = lowestValues.get(i);
            if (object.isJudgement) {
                if(aggregatedMatrix[object.getI()][object.getJ()].length > 1){
                    int randomNumber = random.nextInt(aggregatedMatrix[object.getI()][object.getJ()].length);
                    int newObject = aggregatedMatrix[object.getI()][object.getJ()][randomNumber];
                    aggregatedMatrix[object.getI()][object.getJ()] = new int[]{newObject};
                    return true;
                }
            } else {
                if(aggregatedWeights[object.getI()].length > 1){
                    if(lex){
                        int randomNumber = random.nextInt(aggregatedWeights[object.getI()].length);
                        int randomObject = aggregatedWeights[object.getI()][randomNumber];
                        //System.out.println(randomObject + " from " + object.getI());
                        aggregatedWeights[object.getI()] = new int[]{randomObject};
                        return validateWeights(aggregatedWeights);
                    }else {
                        int randomNumber = random.nextInt(aggregatedWeights[object.getI()].length);
                        int randomObject = aggregatedWeights[object.getI()][randomNumber];
                        aggregatedWeights[object.getI()] = new int[]{randomObject};
                        return true;
                    }

                }
            }
        }
        return true;
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
    public static boolean validateWeights(int[][] aggregatedWeights){
        for(int[] array : aggregatedWeights){
            //double cut
            //doubleCut(aggregatedWeights);
            //single cut
            cutIfHasOnlyOne(aggregatedWeights);
        }
        //little validate check
        for(int[] array : aggregatedWeights){
            if(array.length == 0){
                System.out.println();
                return false;
            }
        }
        return true;
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
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(2);}},
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);}}
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
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                },
                {
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);}}
                },
                {
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(1);add(2);}}
                },
                {
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);add(1);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);add(2);}},
                        new ArrayList<>() {{add(0);}},
                        new ArrayList<>() {{add(1);}}
                }
        };
    }

    public static ArrayList<Object>[] getTestLexWeights() {
        return new ArrayList[]{
                new ArrayList<>() {{add(0);add(1);add(2);add(3);add(4);}},
                new ArrayList<>() {{add(1);add(2);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(0);add(1);add(2);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(0);add(3);add(4);add(5);}},
                new ArrayList<>() {{add(0);add(1);add(2);add(3);}},
                new ArrayList<>() {{add(1);add(2);add(3);add(4);add(5);}}
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