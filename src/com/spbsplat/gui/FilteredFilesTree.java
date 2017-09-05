package com.spbsplat.gui;

import com.spbsplat.FilteredFile;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.nio.file.Path;
import java.util.List;

public class FilteredFilesTree extends JTree {

    private Path choosePath = null;

    public FilteredFilesTree() {
        super(new DefaultMutableTreeNode(""));
    }

    public void setFiles(List<FilteredFile> filteredFiles) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(choosePath);
        DefaultMutableTreeNode curNode = top;
        DefaultMutableTreeNode mainNode = curNode;
        for (FilteredFile filteredFile : filteredFiles) {
            Path parent = choosePath.relativize(filteredFile.getPath()).getParent();
            if (parent == null) {
                curNode = mainNode;
            }else{
                next: for (Path nodeElement: parent) {
                    for(int i = 0; i<curNode.getChildCount(); i++){
                        TreeNode child = curNode.getChildAt(i);
                        if(child.toString().equals(nodeElement.toString())){
                            curNode = (DefaultMutableTreeNode)child;
                            continue next;
                        }
                    }
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeElement);
                    curNode.add(node);
                    curNode = node;
                }

            }
            curNode.add(new DefaultMutableTreeNode(filteredFile));
            curNode = mainNode;

        }
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.setRoot(top);
        expandAllNodes(0, 1);
    }


    private void expandAllNodes(int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            expandRow(i);
        }
        if(getRowCount()!=rowCount){
            expandAllNodes(rowCount, getRowCount());
        }
    }

    public Path getChoosePath() {
        return choosePath;
    }

    public void setChoosePath(Path choosePath) {
        this.choosePath = choosePath;
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(choosePath);
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.setRoot(top);
        this.setRootVisible(true);
        this.updateUI();
    }
}
