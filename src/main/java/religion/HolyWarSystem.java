package religion;

public class HolyWarSystem {

    public static int getKillValue(String role) {

        switch(role) {

            case "PROPHET":
                return 500;

            case "PRIEST":
                return 100;

            default:
                return 50;

        }

    }

}