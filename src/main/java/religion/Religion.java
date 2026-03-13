package religion;

import java.util.*;

public class Religion {

    public String name;
    public UUID prophet;

    public Set<UUID> priests = new HashSet<>();
    public Set<UUID> laymen = new HashSet<>();

    public int piety = 0;
    public int pietyCap = 100;

    public Religion(String name, UUID prophet) {
        this.name = name;
        this.prophet = prophet;
    }

    public void addLayman(UUID player) {
        laymen.add(player);
        updateCap();
    }

    public void removeLayman(UUID player) {
        laymen.remove(player);
        updateCap();
    }

    private void updateCap() {
        pietyCap = 100 + (laymen.size() * 100);
    }
}