import java.util.*;

public class MapSortByValue {

    public static Map<Integer, String> sortByValue(Map<Integer, String> unsortedMap) {
        // Konvertiere die Map in eine Liste von Einträgen
        List<Map.Entry<Integer, String>> entryList = new ArrayList<>(unsortedMap.entrySet());

        // Sortiere die Liste der Einträge nach dem Value (String)
        entryList.sort(Map.Entry.comparingByValue());

        // Erstelle eine neue LinkedHashMap, um die sortierten Einträge beizubehalten
        Map<Integer, String> sortedMap = new LinkedHashMap<>();

        // Füge die sortierten Einträge zur neuen Map hinzu
        for (Map.Entry<Integer, String> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void main(String[] args) {
        // Beispiel-Eingabe Map
        Map<Integer, String> unsortedMap = new HashMap<>();
        unsortedMap.put(3, "Apfel");
        unsortedMap.put(1, "Banane");
        unsortedMap.put(2, "Orange");

        System.out.println("Unsortierte Map: " + unsortedMap);

        // Rufe die Methode auf, um die Map nach dem Value zu sortieren
        Map<Integer, String> sortedMap = sortByValue(unsortedMap);

        System.out.println("Sortierte Map: " + sortedMap);
    }
}