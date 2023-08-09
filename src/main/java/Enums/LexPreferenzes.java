package Enums;

public enum LexPreferenzes {
    PA    ("A"),
    PB        ("B"),
    PC    ("C"),
    PD          ("D"),
    PE         ("E"),
    PF             ("F"),
    PG         ("G");

    public final String value;

    public static LexPreferenzes getJudgement(int Id){
        return switch (Id) {
            case 0 -> PA;
            case 1 -> PB;
            case 2 -> PC;
            case 3 -> PD;
            case 4 -> PE;
            case 5 -> PF;
            case 6 -> PG;
            default -> null;
        };
    }

    public String getValue(){
        return this.value;
    }

    LexPreferenzes(String value) {
        this.value = value;
    }
}
