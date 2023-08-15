import Enums.LexPreferenzes;

import java.util.Comparator;

public class LexComperator implements Comparator<LexPreferenzes> {
    @Override
    public int compare(LexPreferenzes o1, LexPreferenzes o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }
}
