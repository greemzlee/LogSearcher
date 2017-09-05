package com.spbsplat;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс со списком совпадений
 */
public class FilteredFile {
    private Path filePath;
    private List<MatchResult> matches;

    public FilteredFile(Path filePath) {
        this.filePath = filePath;
        this.matches = new ArrayList<>();
    }

    /**
     * Добавляет новое совпадение
     * @param startOffset Позиция начала совпадения
     * @param endOffset Позиция конца совпадения
     * @param lineNumber Номер строки совпадения
     */
    public void addMatchResult(long startOffset, long endOffset, int lineNumber) {
        matches.add(new MatchResult(startOffset, endOffset, lineNumber));
    }

    public boolean isEmpty() {
        return this.matches.isEmpty();
    }

    public Path getPath() {
        return filePath;
    }

    public List<MatchResult> getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return filePath.getFileName().toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof FilteredFile) && filePath.equals(((FilteredFile) obj).getPath());
    }

    /**
     * Класс, содержащий позиции начала и конца совпадений
     */
    public static class MatchResult {
        long start;
        long end;
        int lineNumber;

        public MatchResult(long startIndex, long endIndex, int lineNumber)
        {
            this.start = startIndex;
            this.end = endIndex;
            this.lineNumber = lineNumber;
        }

        public long getStart() {
            return start;
        }

        public long getStart(long offset) {
            return start-offset;
        }

        public long getEnd() {
            return end;
        }

        public long getEnd(long offset) {
            return end-offset;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public long getLength()
        {
            return end-start;
        }

    }
}
