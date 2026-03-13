package religion.service;

import religion.data.Religion;
import religion.data.ReligionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventLogService {

    private final List<ReligionEvent> globalEvents = new ArrayList<>();

    public void add(Religion religion, long now, String type, String details) {
        ReligionEvent event = new ReligionEvent(now, religion.name, type, details);
        religion.history.add(event);
        globalEvents.add(event);
    }

    public List<ReligionEvent> getGlobalEvents() {
        return Collections.unmodifiableList(globalEvents);
    }
}