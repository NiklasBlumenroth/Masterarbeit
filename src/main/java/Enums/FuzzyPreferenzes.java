package Enums;

public enum FuzzyPreferenzes {
    VL     (0.0, 0.1,0.2),
    L         (0.1, 0.2,0.3),
    ML     (0.2, 0.35,0.5),
    M          (0.4, 0.5,0.6),
    MH   (0.5, 0.65,0.8),
    H       (0.7, 0.8,0.9),
    VH   (0.8, 0.9,1.0);

    public final double value1;
    public final double value2;
    public final double value3;

    public static FuzzyPreferenzes getPreferenzes(int Id){
        return switch (Id) {
            case 6 -> VH;
            case 5 -> H;
            case 4 -> MH;
            case 3 -> M;
            case 2 -> ML;
            case 1 -> L;
            case 0 -> VL;
            default -> null;
        };
    }

    FuzzyPreferenzes(double value1, double value2, double value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
