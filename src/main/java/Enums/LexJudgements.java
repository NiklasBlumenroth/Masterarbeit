package Enums;

public enum LexJudgements {
    JA    ("A"),
    JB        ("B"),
    JC    ("C"),
    JD          ("D"),
    JE         ("E"),
    JF             ("F"),
    JG         ("G");

    public final String value;

    public static LexJudgements getJudgement(int Id){
        return switch (Id) {
            case 0 -> JA;
            case 1 -> JB;
            case 2 -> JC;
            case 3 -> JD;
            case 4 -> JE;
            case 5 -> JF;
            case 6 -> JG;
            default -> null;
        };
    }

    LexJudgements(String value) {
        this.value = value;
    }
}
