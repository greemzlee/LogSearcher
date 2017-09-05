package com.spbsplat.gui.viewer;

/**
 * Кусок текста для работы с большими файлами
 */
public class TextChunk {
    private long id;
    private String content;
    private long offset;

    public TextChunk(String content, long id, long offset) {
        this.content = content;
        this.id = id;
        this.offset = offset;
    }

    public String getContent() {
        return content;
    }

    public long getOffset() {
        return offset;
    }

    public long getSize() {
        return content.length();
    }

    public long getEnd() {
        return getSize() + getOffset();
    }
}
