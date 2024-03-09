package Enums;

public enum FuzzyPreferenzes {
    VL     (0.0, 0.1, 0.2),
    L         (0.2, 0.3, 0.4),
    M          (0.4, 0.5, 0.6),
    H       (0.6, 0.7, 0.8),
    VH   (0.8, 0.9, 1.0);

    public final double value1;
    public final double value2;
    public final double value3;

    public static FuzzyPreferenzes getPreferenzes(int Id){
        return switch (Id) {
            case 4 -> VH;
            case 3 -> H;
            case 2 -> M;
            case 1 -> L;
            case 0 -> VL;
            default -> null;
        };
    }
    public static int getId(FuzzyPreferenzes fuzzyPreferenzes){
        return switch (fuzzyPreferenzes) {
            case VH -> 4;
            case H -> 3;
            case M -> 2;
            case L -> 1;
            case VL -> 0;
            default -> -1;
        };
    }
    FuzzyPreferenzes(double value1, double value2, double value3) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }
}
