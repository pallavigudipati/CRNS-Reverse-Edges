package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Entity {
    // public String entityLookupFileName;
    public List<String> entityLookup;
    public HashMap<String, Integer> indexLookup;

    public Entity(String entityLookupFileName) {
        entityLookup = new ArrayList<String>();
        indexLookup = new HashMap<String, Integer>();
        // TODO populate entityLookup
        for (int i = 0; i < entityLookup.size(); ++i) {
            indexLookup.put(entityLookup.get(i), i);
        }
    }
}
