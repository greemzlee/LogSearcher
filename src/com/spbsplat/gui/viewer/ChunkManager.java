package com.spbsplat.gui.viewer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Менеджер, управляющий подгрузкой участков текстового файла.
 * Разбивает файл на блоки и подгружает предыдущий, текущий и следующий блоки.
 * Предназначен для работы с большими файлами.
 * Кэширует открытые блоки.
 */
public class ChunkManager {
    private final long CHUNK_SIZE;
    private final long chunkCount;
    private Path pathToFile;
    private Map<Long, TextChunk> cashedChunks;
    private long currentChunkId;

    /**
     * Стандарнтый менеджер с размером блока 2048 символов
     * @param pathToFile - файл для загрузки
     */
    public ChunkManager(Path pathToFile) {
        this(pathToFile, 2048);
    }

    /**
     * Менеджер с регулируемым размером блока
     * @param pathToFile - файл для загрузки
     * @param chunkSize - размера блока
     */
    public ChunkManager(Path pathToFile, long chunkSize) {
        this.pathToFile = pathToFile;
        CHUNK_SIZE = chunkSize;
        cashedChunks = new WeakHashMap<>();

        long fileSize = pathToFile.toFile().length();
        chunkCount = fileSize % CHUNK_SIZE == 0 ? fileSize / CHUNK_SIZE : fileSize / CHUNK_SIZE + 1;
    }

    /**
     * Возвращает блок текста, в котором содержится искомый текст
     * @param matchPosition - смещение в символах от начала файла
     * @return - возвращает предыдущий, текущий и последующий блоки.
     */
    public String getText(long matchPosition) {
        long newChunkId = matchPosition / CHUNK_SIZE;

        currentChunkId = newChunkId;

        StringBuilder builder = new StringBuilder();

        if (currentChunkId > 0) {
            TextChunk previousChunk = getChunk(currentChunkId - 1);
            builder.append(previousChunk.getContent());
        }

        TextChunk currentChunk = getChunk(currentChunkId);
        builder.append(currentChunk.getContent());

        if (currentChunkId < chunkCount - 1) {
            TextChunk nextChunk = getChunk(currentChunkId + 1);
            builder.append(nextChunk.getContent());
        }

        return builder.toString();
    }

    /**
     * Создает новый или возвращает существующий блок по его номеру
     * @param chunkId - номер блока
     * @return
     */
    private TextChunk getChunk(long chunkId) {
        if (cashedChunks.containsKey(chunkId)) {
            return cashedChunks.get(chunkId);
        }

        TextChunk readedChunk = readNewChunk(chunkId);
        cashedChunks.put(chunkId, readedChunk);
        return readedChunk;
    }

    /**
     * Считывает новый блок из файла
     * @param chunkId - номер блока
     * @return новый блок текста
     */
    private TextChunk readNewChunk(long chunkId) {
        String content = "";
        long chunkSize;
        long chunkOffset = chunkId * CHUNK_SIZE;

        BufferedReader reader = null;
        try {
            char[] buf = new char[((int) CHUNK_SIZE)];
            reader = new BufferedReader(new FileReader(pathToFile.toFile()));
            reader.skip(chunkOffset);
            chunkSize = reader.read(buf);
            content = new String(buf, 0, (int) chunkSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TextChunk(content, chunkId, chunkOffset);
    }

    /**
     *
     * @return текущий блок текста
     */
    public TextChunk getCurrentChunk() {
        return cashedChunks.get(currentChunkId);
    }

    /**
     *
     * @return предыдущий блок текста
     */
    public TextChunk getPreviousChunk() {
        return currentChunkId == 0 ? getCurrentChunk() : cashedChunks.get(currentChunkId - 1);
    }

    /**
     *
     * @return следующий блок текста
     */
    public TextChunk getNextChunk() {
        return currentChunkId == chunkCount - 1 ? getCurrentChunk() : cashedChunks.get(currentChunkId + 1);
    }
}
