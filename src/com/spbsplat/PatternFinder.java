package com.spbsplat;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;

/**
 * Класс для поиска совпадений в искомых файлах
 */
public class PatternFinder {

    private Pattern pattern;
    private String pat;

    /**
     *
     * @param pat - паттерн для поиска
     */
    public PatternFinder(String pat) {
        this.pat = pat;
        try {
            pattern = Pattern.compile(pat);
        } catch (PatternSyntaxException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    /**
     *
     * @param path - путь файла
     * @return - возвращает файл с найденными совпадениями
     * @throws IOException
     */
    public FilteredFile findMatches(Path path) throws IOException{
        FilteredFile filteredFile = new FilteredFile(path);

        BufferedReader r = new BufferedReader(new FileReader(path.toFile()));
        Scanner sc = new Scanner(r);
        sc.useDelimiter("\n");

        String line;
        long totalSize = 0;
        int lineCount = 0;

        while (sc.hasNext()) {
            line = sc.next();
            Matcher m = pattern.matcher(line);
            while (m.find()) {

                int start = m.start(0);
                int end = m.end(0);
                filteredFile.addMatchResult(totalSize+start, totalSize+end, lineCount);
            }
            totalSize += line.length() + 1;
            ++lineCount;
        }
        return filteredFile;
    }
}