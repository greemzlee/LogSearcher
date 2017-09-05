package com.spbsplat.gui.viewer;

import com.spbsplat.FilteredFile;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

/**
 * Графический компонент для просмотра содержимого файлов.
 * Подсвечивает найденные совпадения.
 * Осуществляет навигацию по совпадениям.
 */
public class FileViewer extends JScrollPane {
    private static final Color matchColor = new Color(197, 194, 211);
    private static final Color currentMatchColor = new Color(181, 176, 255);

    JTextArea textArea;
    FilteredFile file;
    int currentMatchId;
    ChunkManager chunkManager;
    TextLineNumber lineManager;

    /**
     *
     * @param fileToOpen - файл со списком совпадений
     */
    public FileViewer(FilteredFile fileToOpen) {
        super();
        this.file = fileToOpen;

        textArea = new JTextArea();
        getViewport().add(textArea);

        lineManager = new TextLineNumber(textArea);
        setRowHeaderView(lineManager);

        currentMatchId = 0;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                SwingUtilities.invokeLater(FileViewer.this::scrollToActiveMatch);
            }
        });
        chunkManager = new ChunkManager(fileToOpen.getPath());
    }

    /**
     * Подсвечивает найденные совпадения
     */
    private void highlightMatches() {
        textArea.getHighlighter().removeAllHighlights();
        List<FilteredFile.MatchResult> matches = file.getMatches();

        for (int i = 0; i < matches.size(); i++) {
            int start = (int) matches.get(i).getStart(chunkManager.getPreviousChunk().getOffset());
            int end = (int) matches.get(i).getEnd(chunkManager.getPreviousChunk().getOffset());
            if (start >= 0) {
                try {
                    Color highlightColor = i == currentMatchId ? currentMatchColor : matchColor;
                    textArea.getHighlighter().addHighlight(
                            start,
                            end,
                            new DefaultHighlighter.DefaultHighlightPainter(highlightColor));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private FilteredFile.MatchResult getCurrentMatchResult() {
        return file.getMatches().get(currentMatchId);
    }

    /**
     * Прокрутка до следующего совпадения.
     * При достижении конца списка совпадений, происходит переход на первое совпадение
     */
    public void showNextMatch() {
        ++currentMatchId;
        if (currentMatchId >= file.getMatches().size())
            currentMatchId = 0;
        SwingUtilities.invokeLater(this::scrollToActiveMatch);
    }

    /**
     * Прокрутка до предыдущего совпадения.
     * При достижении начала списка совпадений, происходит переход на последнее совпадение
     */
    public void showPreviousMatch() {
        --currentMatchId;
        if (currentMatchId < 0)
            currentMatchId = file.getMatches().size() - 1;
        SwingUtilities.invokeLater(this::scrollToActiveMatch);
    }

    /**
     * Прокручивает файл до первого совпадения
     */
    private void scrollToActiveMatch() {
        long matchStart = getCurrentMatchResult().getStart();

        String text = chunkManager.getText(matchStart);
        if (text != null) {
            textArea.setText(text);
        }

        highlightMatches();

        try {
            FilteredFile.MatchResult mr = getCurrentMatchResult();
            int matchStartOffseted = ((int) mr.getStart(chunkManager.getPreviousChunk().getOffset()));
            int matchLength = ((int) mr.getLength());

            Rectangle viewRect = textArea.modelToView(matchStartOffseted);
            viewRect.y += 200;
            textArea.scrollRectToVisible(viewRect);
            textArea.setCaretPosition(matchStartOffseted + matchLength);

            int lineCount = 0;
            for (int i = 0; i < matchStartOffseted; i++) {
                if (text.charAt(i) == '\n') {
                    ++lineCount;
                }
            }
            lineCount = getCurrentMatchResult().getLineNumber() - lineCount + 1;
            lineManager.setLineOffset(lineCount);
            lineManager.setMinimumDisplayDigits(Integer.toString(lineCount).length() + 1);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

}
