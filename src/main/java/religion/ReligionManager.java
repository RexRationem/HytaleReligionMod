package religion;

import java.util.*;

public class ReligionManager {

    private Map<String, Religion> religions = new HashMap<>();

    public Religion createReligion(String name, UUID prophet) {

        if (religions.containsKey(name))
            throw new RuntimeException("Religion already exists");

        Religion r = new Religion(name, prophet);
        religions.put(name, r);

        EventLog.log("Religion founded: " + name);

        return r;
    }

    public Religion getReligion(String name) {
        return religions.get(name);
    }

}