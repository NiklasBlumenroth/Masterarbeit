import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        String berechnungsName = "FuzzySAW2 3 x 5 x 5";//"FuzzySAW " + numberOfDecisionMaker + " x " + alt + " x " + crit;
        String dir = System.getProperty("user.dir") + "\\src\\main\\resources\\SimulationIdealAuflösung\\";
        File file = new File(dir + berechnungsName + ".txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String txt = "";
        int[] list = new int[50];
        for(int i = 0; i < list.length; i++){
            list[i] = 0;
        }
        while((line = br.readLine()) != null){
            line = line.substring(line.indexOf("=") + 2, line.indexOf("."));
            list[Integer.parseInt(line)]++;
            if(Integer.parseInt(line) == 23){
                System.out.println();
            }
        }

        for(int i : list){
            System.out.println(i);
        }

//        String berechnungsName = numberOfDecisionMaker + " x " + alt + " x " + crit;
//        if(lex){
//            berechnungsName = "Lex " + numberOfDecisionMaker + " x " + alt + " x " + crit;
//        }else {
//            berechnungsName = "FuzzySAW " + numberOfDecisionMaker + " x " + alt + " x " + crit;
//        }
//        String dir = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\";
//        Set<String> set = Stream.of(new File(dir).listFiles())
//                .filter(file -> !file.isDirectory())
//                .map(File::getName)
//                .collect(Collectors.toSet());
//        for(String path : set){
//            System.out.println(path + " -> " + getSavedValue(dir + path));
//        }
//        String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName + ".txt";
//        System.out.println(getSavedValue(fileName));
    }
}
