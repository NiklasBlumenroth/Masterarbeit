import lombok.Data;

@Data
public class LowestValueObject {
    public int i;
    public int j;
    public boolean isJudgement;
    public double lowestValue;
    public int lowestKey;
    public boolean isValid;

    public LowestValueObject(double lowestValue, int lowestKey, int i, int j, boolean isJudgement){
        this.i = i;
        this.j = j;
        this.isJudgement = isJudgement;
        this.lowestKey = lowestKey;
        this.lowestValue = lowestValue;
    }
}
