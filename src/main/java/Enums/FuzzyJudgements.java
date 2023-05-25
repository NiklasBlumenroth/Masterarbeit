package Enums;

public enum FuzzyJudgements {
    SEHRSCHLECHT    (0, 1, 2),
    SCHLECHT        (1, 2, 3),
    EHERSCHLECHT    (2, 3.5, 5),
    MITTEL          (4, 5, 6),
    EHERGUT         (5, 6.5, 8),
    GUT             (7, 8, 9),
    SEHRGUT         (8, 9, 10);

    public final double value1;
    public final double value2;
    public final double value3;

    public static FuzzyJudgements getJudgement(int Id){
        return switch (Id) {
            case 0 -> SEHRSCHLECHT;
            case 1 -> SCHLECHT;
            case 2 -> EHERSCHLECHT;
            case 3 -> MITTEL;
            case 4 -> EHERGUT;
            case 5 -> GUT;
            case 6 -> SEHRGUT;
            default -> null;
        };
    }

    FuzzyJudgements(double value1, double value2, double value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
