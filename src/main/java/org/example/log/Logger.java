package org.example.log;

import org.example.exception.LoggerException;

import java.util.List;

public class Logger {
    private final FileAccessor fileAccessor;
    private int writersCount;
    private int readersCount;
    private int linesCount;

    public int getLinesCount() {
        return linesCount;
    }

    public Logger(FileAccessor fileAccessor) {
        this.fileAccessor = fileAccessor;
        writersCount = 0;
        readersCount = 0;
        linesCount = 0;
    }

    public synchronized void startRead() {
        while (writersCount > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new LoggerException("Interrupted during waiting for ability to read", e);
            }
        }
        readersCount++;
    }

    public List<String> readNLastLines(int lastNLinesCount) {
        return fileAccessor.readLastNLines(lastNLinesCount);
    }

    public synchronized void finishReading() {
        readersCount--;
        notifyAll();
    }

    public synchronized void startWriting() {
        while (readersCount > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new LoggerException("Interrupted during waiting for ability to read", e);
            }
        }
        writersCount++;
    }

    public void write(String message) {
        fileAccessor.writeToEnd(message);
    }

    public synchronized void finishWriting() {
        writersCount--;
        notifyAll();
    }

}
