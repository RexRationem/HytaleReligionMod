package religion;

public class PietySystem {

    public static void addPiety(Religion r, int amount) {

        r.piety += amount;

        if (r.piety > r.pietyCap)
            r.piety = r.pietyCap;

    }

    public static boolean spendPiety(Religion r, int amount) {

        if (r.piety < amount)
            return false;

        r.piety -= amount;
        return true;
    }

}