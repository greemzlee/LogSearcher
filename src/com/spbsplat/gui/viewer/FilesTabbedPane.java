package com.spbsplat.gui.viewer;

import com.spbsplat.FilteredFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Компонент, содержащий вкладки с открытыми файлами.
 */
public class FilesTabbedPane extends JTabbedPane {
    List<FilteredFile> tabFiles;

    public FilesTabbedPane() {
        this.setMinimumSize(new Dimension(430, 300));
        this.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        tabFiles = new ArrayList<>();
    }

    /**
     * Открывает файл в новой вкладке, если он еще не открыт, иначе происходит переход на открытый файл
     * @param fileToOpen - файл для открытия во вкладке
     */
    public void openFile(FilteredFile fileToOpen) {
        int indexOfTab = getIndexOfTab(fileToOpen);
        if (indexOfTab >= 0) {
            this.setSelectedIndex(indexOfTab);
        } else {
            FileViewer fileViewer = new FileViewer(fileToOpen);
            createTab(fileToOpen, fileViewer);
        }
    }

    private void createTab(FilteredFile fileToOpen, JComponent component) {
        this.addTab(fileToOpen.toString() + "    ", component);
        this.setSelectedIndex(this.getTabCount() - 1);

        ButtonTabComponent tabComponent = new ButtonTabComponent(this);
        this.setTabComponentAt(this.getTabCount() - 1, tabComponent);
        tabFiles.add(fileToOpen);
    }

    private int getIndexOfTab(FilteredFile filteredFile) {
        for (int i = 0; i < tabFiles.size(); i++) {
            if (tabFiles.get(i).equals(filteredFile)) {
                return i;
            }
        }
        return -1;
    }

    /**
     *  Удаляет вкладку по ее номеру
     * @param index - номер вкладки
     */
    public void removeFile(int index) {
        removeTabAt(index);
        tabFiles.remove(index);
    }

    /**
     *
     * @return - возвращает текущую вкладку
     */
    public FileViewer getCurrentViewer() {
        return ((FileViewer) getSelectedComponent());
    }
}


