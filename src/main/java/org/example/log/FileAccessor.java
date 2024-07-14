package org.example.log;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.example.exception.CommonIOException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileAccessor {
    private final Path path;

    public FileAccessor(Path path) {
        checkPathAndPrepareFile(path);
        this.path = path;
    }
    private void checkPathAndPrepareFile (Path path){
        if (path==null) throw new CommonIOException("path can not be null");
        if (!Files.exists( path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new CommonIOException("cant create file " + path, e);
            }
            if (!Files.isRegularFile(path)) throw new CommonIOException ("this is a path to directory");
            if (!Files.isReadable(path)) throw new CommonIOException ("file can not be read");
            if (!Files.isWritable(path)) throw new CommonIOException ("file can not be written in");
        }
    }
    public void writeToEnd (String message){
        try {
            Files.writeString(path,message, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new CommonIOException("Unable to write in file", e);
        }
    }
    public List<String> readLastNLines(int countOfLastLines){
        if (countOfLastLines<=0) return new ArrayList<>();
        try (ReversedLinesFileReader reader = ReversedLinesFileReader.builder().setPath(path).setCharset(StandardCharsets.UTF_8).get()) {
        return reader.readLines(countOfLastLines);
        }    catch (IOException e) {
            throw new CommonIOException("Cannot read file", e);
        }
    }
}
