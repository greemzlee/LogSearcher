package com.spbsplat.gui;

import com.spbsplat.*;
import com.spbsplat.gui.viewer.FilesTabbedPane;

import java.awt.BorderLayout;
import javax.swing.JButton;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.tree.TreePath;

/**
 * Класс главного окна программы
 */
public class MainFrame extends JFrame {
    private static final Dimension minimumSize = new Dimension(900, 700);
    private static JTextField patternString = new JTextField("", 30);
    private static JTextField extensionField = new JTextField(".log", 1);

    private FilesTabbedPane tabbedPane;
    private FilteredFilesTree filesTree;

    public MainFrame() {
        setTitle("Задача №1, Карижский Сергей Александрович");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(MainFrame.minimumSize);
        layoutComponents();
        pack();
    }

    private void layoutComponents() {
        tabbedPane = new FilesTabbedPane();

        filesTree = new FilteredFilesTree();
        filesTree.setRootVisible(false);
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = filesTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = filesTree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 2) {
                        if (selPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                            if (node.getUserObject() instanceof FilteredFile) {
                                FilteredFile file = (FilteredFile) (node.getUserObject());
                                tabbedPane.openFile(file);
                            }
                        }
                    }
                }
            }
        };
        filesTree.addMouseListener(ml);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, filesTree, tabbedPane);

        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        JToolBar toolBar = createToolbar();
        getContentPane().add(toolBar, BorderLayout.NORTH);

        JToolBar toolBarNav = createNavigationToolbar();
        getContentPane().add(toolBarNav, BorderLayout.SOUTH);
    }

    private JToolBar createToolbar() {
        JButton chooseDirectoryButton = new JButton("Выбрать папку");

        chooseDirectoryButton.addActionListener((ActionEvent event) -> {
            String choosertitle = null;
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle(choosertitle);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File chooseDirectory = chooser.getSelectedFile();
                filesTree.setChoosePath(chooseDirectory.toPath());
            }
        });

        JButton searchButton = new JButton("Поиск");
        searchButton.addActionListener(e -> performSearch());

        JToolBar toolBar = new JToolBar("Инструментальная панель");
        toolBar.add(chooseDirectoryButton);
        toolBar.add(patternString);
        toolBar.addSeparator();
        toolBar.add(extensionField);
        toolBar.addSeparator();
        toolBar.add(searchButton);

        return toolBar;
    }

    private JToolBar createNavigationToolbar() {
        JButton selectAll = new JButton("Выделить все");
        JButton nextNavigation = new JButton("Вперед");
        JButton prevNavigation = new JButton("Назад");

        nextNavigation.addActionListener((ActionEvent event) -> {
            try {
                tabbedPane.getCurrentViewer().showNextMatch();
            } catch (NullPointerException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Нет открытых файлов");
            }
        });
        prevNavigation.addActionListener((ActionEvent event) -> {
            try {
                tabbedPane.getCurrentViewer().showPreviousMatch();
            } catch (NullPointerException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Нет открытых файлов");
            }
        });

        JToolBar toolBarNav = new JToolBar("");
        toolBarNav.add(Box.createHorizontalGlue());
        toolBarNav.add(selectAll);
        toolBarNav.addSeparator();
        toolBarNav.add(nextNavigation);
        toolBarNav.addSeparator();
        toolBarNav.add(prevNavigation);

        return toolBarNav;
    }

    private void performSearch() {
        String text = patternString.getText();
        String ext = extensionField.getText();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите строку для поиска");
            return;
        }

        final List<Path> files = new LinkedList<>();
        try {
            Path path = filesTree.getChoosePath();
            if (path == null) {
                JOptionPane.showMessageDialog(this, "Выберите папку для поиска");
                return;
            }
            files.addAll(FileFinder.searchFiles(path, ext));
        } catch (IOException x) {
            JOptionPane.showMessageDialog(this, "Ошибка при работе с файловой системой: " + x.getMessage());
            return;
        }

        final List<FilteredFile> filteredFiles = new ArrayList<>();

        Runnable task = () -> {
            List<Thread> list = new ArrayList<>();
            for (Path entry : files) {
                Thread thread = new Thread(() -> {
                    PatternFinder patternFinder = new PatternFinder(text);
                    try {
                        FilteredFile filteredFile = patternFinder.findMatches(entry);
                        if (!filteredFile.isEmpty()) {
                            synchronized (filteredFiles) {
                                filteredFiles.add(filteredFile);
                            }
                        }
                    } catch (IOException x) {
                        System.err.println(entry.toString() + ": " + x);
                    }
                });
                thread.start();
                list.add(thread);
            }
            list.forEach(thread -> {
                if (thread.isAlive()) {
                    try{
                        thread.join();
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            });
            synchronized (filesTree) {
                filesTree.setFiles(filteredFiles);
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}