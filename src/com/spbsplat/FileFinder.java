package com.spbsplat;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который ищет файлы по совпадениям
 */
public class FileFinder {

    /**
     * Рекурсивный поиск файлов по расширению
     * @param path - путь файла
     * @param ext - расширение файла
     * @return - возвращает список найденных файлов
     * @throws IOException
     */
    public static List<Path> searchFiles(Path path, String ext) throws IOException {
        List<Path> findedFiles = new ArrayList<>();
        listFiles(path, ext, findedFiles);
        return findedFiles;
    }

    private static void listFiles(Path path, String ext, List<Path> findedFiles) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    listFiles(entry, ext, findedFiles);
                }
                else if (entry.getFileName().toString().endsWith(ext)){
                    findedFiles.add(entry);
                }
            }
        }
    }
}
