package religion;

import java.util.Random;

public class MiracleSystem {

    static Random rand = new Random();

    public static boolean attemptMiracle(Religion r) {

        if (!PietySystem.spendPiety(r,100))
            return false;

        double chance = r.pietyCap / 100.0;

        if (chance > 50)
            chance = 50;

        return rand.nextDouble()*100 < chance;
    }

}