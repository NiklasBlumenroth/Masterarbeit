package Enums;

public enum FuzzyPreferenzes {
    SEHRUNWICHTIG     (0.0, 0.1,0.2),
    UNWICHTIG         (0.1, 0.2,0.3),
    EHERUNWICHTIG     (0.2, 0.35,0.5),
    MITTEL          (0.4, 0.5,0.6),
    EHERWICHTIG   (0.5, 0.65,0.8),
    WICHTIG       (0.7, 0.8,0.9),
    SEHRWICHTIG   (0.8, 0.9,1.0);

    public final double value1;
    public final double value2;
    public final double value3;

    public static FuzzyPreferenzes getPreferenzes(int Id){
        return switch (Id) {
            case 0 -> SEHRWICHTIG;
            case 1 -> WICHTIG;
            case 2 -> EHERWICHTIG;
            case 3 -> MITTEL;
            case 4 -> EHERUNWICHTIG;
            case 5 -> UNWICHTIG;
            case 6 -> SEHRUNWICHTIG;
            default -> null;
        };
    }

    FuzzyPreferenzes(double value1, double value2, double value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
