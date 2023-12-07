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

    public static int getJudgement(LexJudgements lexJudgements){
        return switch (lexJudgements) {
            case JA -> 0;
            case JB -> 1;
            case JC -> 2;
            case JD -> 3;
            case JE -> 4;
            case JF -> 5;
            case JG -> 6;
            default -> -1;
        };
    }

    LexJudgements(String value) {
        this.value = value;
    }
}
