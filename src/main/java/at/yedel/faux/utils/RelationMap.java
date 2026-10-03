package at.yedel.faux.utils;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class RelationMap {
    public final String id;
    public final Map<String, ArrayList<String>> relations = new HashMap<>();

    public RelationMap(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + id + ": " + relations + "]";
    }
}
