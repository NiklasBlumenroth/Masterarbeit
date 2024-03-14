import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testFileClass {
    public static final int numberOfDecisionMaker = 3;
    public static final int alt = 5;
    public static final int crit = 5;
    public static final boolean lex = true;
    public static final Class jugClazz = FuzzyJudgements.class;
    public static final Class prefClazz = FuzzyPreferenzes.class;

    private static Double getSavedValue(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String txt = "";
        List<Double> list = new ArrayList<>();
        while((line = br.readLine()) != null){
            if(line.length() > 5){
                try{

                }catch (Exception e){

                }
                String lineValue = line.substring(line.indexOf("=") + 1, line.indexOf(":")-1);
                list.add(Double.valueOf(lineValue));
            }
        }
        Double score = 0.0;
        for(Double value : list){
            score += value;
        }
        score /= list.size();

        return score;
    }
    public static void main(String[] args) throws IOException {
        /*String berechnungsName = "FuzzySAW 1st part 5 x 7 x 5.txt";
        String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName;
        System.out.println(getSavedValue(fileName));

         */

        String berechnungsName = "FuzzySAW ideal5 x 7 x 5.txt";
        String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName;
        getBalkenDiagramm(fileName);



    }
    private static void getBalkenDiagramm(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String txt = "";
        List<Integer> list = new ArrayList<>();
        while((line = br.readLine()) != null){
            if(line.length() > 5){
                String lineValue = line.substring(line.indexOf("=") + 1, line.indexOf(":")-1);
                lineValue = lineValue.replace(" ", "");
                lineValue = lineValue.substring(0, lineValue.indexOf("."));
                list.add(Integer.valueOf(lineValue));
            }
        }
        int[] heuristic = new int[50];

        for(int i = 0; i < heuristic.length; i++){
            heuristic[i] = 0;
        }

        for(Integer value: list){
            heuristic[value]++;
        }

        for(int i = 0; i < heuristic.length; i++){
            System.out.println(heuristic[i]);//ausgabe beginnt mit pfadlänge = 0
        }
    }

}
