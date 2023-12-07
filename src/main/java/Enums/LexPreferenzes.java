package Enums;

import lombok.Getter;

public enum LexPreferenzes {
    PA    ("A", 0),
    PB        ("B", 1),
    PC    ("C", 2),
    PD          ("D", 3),
    PE         ("E", 4),
    PF             ("F", 5),
    PG         ("G", 6);

    @Getter
    public final String value;
    @Getter
    public final int id;

    LexPreferenzes(String value, int id){
        this.value = value;
        this.id = id;
    }

    public static LexPreferenzes getLexValueById(int id){
        for (LexPreferenzes u : LexPreferenzes.values()){
            if (id == u.getId()){
                return u;
            }
        }
        return null;
    }

    public static int getLexIdByValue(LexPreferenzes value){
        for (LexPreferenzes u : LexPreferenzes.values()){
            if (value == u){
                return u.getId();
            }
        }
        return -1;
    }
}
