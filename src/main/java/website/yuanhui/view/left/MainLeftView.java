
package website.yuanhui.view.left;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import website.yuanhui.action.ConnectionSelected;
import website.yuanhui.action.DbListTreeMouseListener;
import website.yuanhui.ui.MyTreeCellRenderer;

public class MainLeftView {
    public MainLeftView() {
    }

    public static Component init() {
        JTree tree = new JTree();
        new ConnectionSelected(tree);
        tree.setModel((TreeModel)null);
        tree.addMouseListener(new DbListTreeMouseListener(tree));
        MyTreeCellRenderer cellRenderer = new MyTreeCellRenderer();
        cellRenderer.setBackgroundSelectionColor(Color.BLACK);
        tree.setCellRenderer(cellRenderer);
        JScrollPane jScrollPane = new JScrollPane(tree);
        Dimension minimumSize = new Dimension(160, 0);
        jScrollPane.setPreferredSize(minimumSize);
        return jScrollPane;
    }
}
