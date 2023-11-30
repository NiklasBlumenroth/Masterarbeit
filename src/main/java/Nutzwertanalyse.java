import Enums.FuzzyJudgements;
import Enums.FuzzyPreferenzes;
import Enums.LexJudgements;
import Enums.LexPreferenzes;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
//                        new ArrayList<>(){{add(MG); add(P);add(VG);}},
//                        new ArrayList<>(){{add(G); add(P);}},
//                        new ArrayList<>(){{add(F); add(P);}},
//                }
//        };
        return new ArrayList[][]{
                {
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(F); add(MG);}},
                        new ArrayList<>(){{add(F); add(G);add(MG);}}
                },
                {
                        new ArrayList<>(){{add(MG);}},
                        new ArrayList<>(){{add(MG); add(G);}},
                        new ArrayList<>(){{add(F); add(MP);}}
                },
                {
                        new ArrayList<>(){{add(MG); add(G);}},
                        new ArrayList<>(){{add(MG); add(G);}},
                        new ArrayList<>(){{add(F);}},
                },
                {
                        new ArrayList<>(){{add(F); add(G);}},
                        new ArrayList<>(){{add(MG);add(F); add(G);}},
                        new ArrayList<>(){{add(MG); add(MP); add(F);}},
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
                new ArrayList<>(){{add(M); add(MH); add(H);}},
                new ArrayList<>(){{add(H); add(MH);}},
                new ArrayList<>(){{add(M); add(H); add(ML);}}
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
            System.out.println("File already exists: " + fileName.substring(fileName.lastIndexOf("\\")-1));
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

    public static String output = "";
    public static void main(String[] args) throws IOException, ParseException {
        Date startDate = new Date();
        String logFile;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
        Calendar c = Calendar.getInstance();
        String curr_date = dateFormat.format(c.getTime());

        String fileName = System.getProperty("user.dir") + "\\src\\main\\resources\\Logging\\" + curr_date + ".txt";
        fileExist(fileName);

        output += "Start: " + startDate;
        ArrayList<Object>[][] aggregatedMatrix = getMatrix();
        ArrayList<Object>[] aggregatedWeights = getWeights();
        List<Map<String, Object>> lowestValue = MonteCarloHelper.showMonteCarloSaw(aggregatedMatrix, aggregatedWeights, full, show);

        output += "\nLowest values\n";

        output += "\n0: " + getLowestFormated(lowestValue.get(0));
        output += "\n1: " + getLowestFormated(lowestValue.get(1));
        output += "\n2: " + getLowestFormated(lowestValue.get(2));

        startDate = new Date();
        output += "\nEnd: " + startDate;

        System.out.println(output);
        writeTxt(fileName, output);
    }

    public static String getLowestFormated (Map<String, Object> map){
        String formated = "";
        String iValue = map.get("lowestI").toString();
        String jValue = null;

        try{
            jValue = map.get("lowestJ").toString();
        }catch (Exception e){
            jValue = "null";
        }

        String jugdementBoolean = map.get("lowestValueIsJudgement").toString();
        if(jugdementBoolean.contains("true")){
            jugdementBoolean = "Judgement";
        }else {
            jugdementBoolean = "Preference";
        }
        String lowestKey = map.get("lowestKey").toString();
        String lowestValue = map.get("lowestValue").toString();

        formated += jugdementBoolean + "\n";
        formated += "Key: " + lowestKey + " | Value: " + lowestValue + "\n";
        formated += "I: " + iValue + " | J: " + jValue + "\n";
        return formated;
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
        if ((Boolean) lowestValue.get("lowestValueIsJudgement")) {
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")] = new ArrayList<>();
            aggregatedMatrix[(Integer) lowestValue.get("lowestI")][(Integer) lowestValue.get("lowestJ")].add(lowestValue.get("lowestKey"));
        } else {
            aggregatedWeights[(Integer) lowestValue.get("lowestI")] = new ArrayList<>();
            aggregatedWeights[(Integer) lowestValue.get("lowestI")].add(lowestValue.get("lowestKey"));
        }
    }

    public static void getRandomPath(ArrayList<Object>[][] aggregatedMatrix, ArrayList<Object>[] aggregatedWeights, List<Map<String, Object>>  lowestValue) {
        Random random = new Random();
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
                    aggregatedWeights[(Integer) map.get("lowestI")] = new ArrayList<>();
                    aggregatedWeights[(Integer) map.get("lowestI")].add(randomObject);
                    break;
                }

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