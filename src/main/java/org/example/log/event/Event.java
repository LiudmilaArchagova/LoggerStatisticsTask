package org.example.log.event;

import java.time.LocalDateTime;

public record Event(EventLevel eventLevel, LocalDateTime time, String text) implements Comparable<Event> {
    @Override
    public int compareTo(Event o) {
        return this.time.compareTo(o.time);
    }
}
