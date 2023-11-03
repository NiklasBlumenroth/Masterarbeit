import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class testFileClass {

    public static final int alt = 3;
    public static final int crit = 3;
    public static final int numberOfDecisionMaker = 2;

    private static Double getSavedValue(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String txt = "";
        List<Double> list = new ArrayList<>();
        while((line = br.readLine()) != null){
            if(line.length() > 5){
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
        String berechnungsName = numberOfDecisionMaker + " x " + alt + " x " + crit;
        String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\Berechnungen\\" + berechnungsName + ".txt";
        System.out.println(getSavedValue(fileName));
    }
}
