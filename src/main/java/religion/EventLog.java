package religion;

import java.time.LocalDateTime;

public class EventLog {

    public static void log(String msg) {

        System.out.println("[Religion] "
                + LocalDateTime.now()
                + " "
                + msg);

    }

}