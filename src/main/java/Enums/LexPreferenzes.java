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
    LexPreferenzes(String value) {
        this.value = value;
        this.id = getLexIdByValue(value);
    }

    LexPreferenzes(int id){
        this.id = id;
        this.value = getLexValueById(id);
    }

    public static String getLexValueById(int id){
        for (LexPreferenzes u : LexPreferenzes.values()){
            if (id == u.getId()){
                return u.getValue();
            }
        }
        return null;
    }

    public static int getLexIdByValue(String value){
        for (LexPreferenzes u : LexPreferenzes.values()){
            if (value.equals(u.getValue())){
                return u.getId();
            }
        }
        return -1;
    }
}
