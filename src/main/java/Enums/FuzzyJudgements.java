package Enums;

public enum FuzzyJudgements {
    VP    (0, 1, 2),
    P        (1, 2, 3),
    MP    (2, 3.5, 5),
    F          (4, 5, 6),
    MG         (5, 6.5, 8),
    G             (7, 8, 9),
    VG         (8, 9, 10);

    public final double value1;
    public final double value2;
    public final double value3;

    public static FuzzyJudgements getJudgement(int Id){
        return switch (Id) {
            case 0 -> VP;
            case 1 -> P;
            case 2 -> MP;
            case 3 -> F;
            case 4 -> MG;
            case 5 -> G;
            case 6 -> VG;
            default -> null;
        };
    }

    public static int getId(FuzzyJudgements judgements){
        return switch (judgements) {
            case VP -> 0;
            case P -> 1;
            case MP -> 2;
            case F -> 3;
            case MG -> 4;
            case G -> 5;
            case VG -> 6;
            default -> -1;
        };
    }

    FuzzyJudgements(double value1, double value2, double value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
