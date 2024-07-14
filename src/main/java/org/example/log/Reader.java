package org.example.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.ReaderException;
import org.example.log.event.Event;
import org.example.util.ObjectMapperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Reader implements Runnable {
    private Logger logger;
    private ObjectMapper mapper;
    private List<Event> lastRead;
    private int lastReadLineNumber;

    public Reader(Logger logger) {
        this.logger = logger;
        this.mapper = ObjectMapperUtil.createMapper();
        this.lastRead = new ArrayList<>();
        this.lastReadLineNumber = 0;
    }

    public List<Event> getLastRead() {
        List<Event> copy = new ArrayList<>(lastRead);
        lastRead.clear();
        return copy;
    }

    @Override
    public void run() {
        while (Thread.currentThread().isAlive()) {
            lastRead = readNew();
            System.out.printf("Поток читатель %d получил новое событие", Thread.currentThread().getId());
            ;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new ReaderException("reader %d interrupted during sleep".formatted(Thread.currentThread().getId()), e);
            }
        }
    }

    private List<Event> readNew() {
        logger.startRead();
        int linesToBeRead = logger.getLinesCount() - lastReadLineNumber;
        System.out.printf("Поток читатель %d начал чтение", Thread.currentThread().getId());
        List <Event> events = toEvents(logger.readNLastLines(linesToBeRead));
        System.out.printf("Поток читатель %d закончил чтение", Thread.currentThread().getId());
        return events;

    }

    private List<Event> toEvents(List<String> lines) {
        return lines.stream()
                .map(this::toEvent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Event> toEvent(String json) {
        try {
            return Optional.ofNullable(mapper.readValue(json, Event.class));
        } catch (JsonProcessingException e) {
            System.err.printf("can not convert json to object: json=%s, reason=%s%n", json, e.getMessage());
            return Optional.empty();
        }
    }
}
