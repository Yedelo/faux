package at.yedel.faux.utils;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class RelationMap {
    private final String id;
    private final Map<String, ArrayList<String>> relations = new HashMap<>();

    public RelationMap(String id) {
        this.id = id;
    }

    public void addRelationKey(String key) {
        relations.put(key, new ArrayList<>());
    }

    public void addRelationMod(String key, String id) {
        relations.get(key).add(id);
    }

    @Override
    public String toString() {
        return "[" + id + ": " + relations + "]";
    }
}
