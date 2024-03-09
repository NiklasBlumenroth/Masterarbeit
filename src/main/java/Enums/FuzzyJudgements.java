package Enums;

public enum FuzzyJudgements {
    VP    (0, 1, 2),
    P        (2, 3, 4),
    F          (4, 5, 6),
    G             (6, 7, 8),
    VG         (8, 9, 10);

    public final double value1;
    public final double value2;
    public final double value3;

    public static FuzzyJudgements getJudgement(int Id){
        return switch (Id) {
            case 0 -> VP;
            case 1 -> P;
            case 2 -> F;
            case 3 -> G;
            case 4 -> VG;
            default -> null;
        };
    }

    public static int getId(FuzzyJudgements judgements){
        return switch (judgements) {
            case VP -> 0;
            case P -> 1;
            case F -> 2;
            case G -> 3;
            case VG -> 4;
            default -> -1;
        };
    }

    FuzzyJudgements(double value1, double value2, double value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
