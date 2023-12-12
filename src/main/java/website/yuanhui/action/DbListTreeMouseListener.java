
package website.yuanhui.action;

import org.influxdb.dto.QueryResult;
import website.yuanhui.global.GlobalVar;
import website.yuanhui.util.InfluxDbUtil;
import website.yuanhui.util.ObserverUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class DbListTreeMouseListener implements MouseListener {
    private final JTree tree;

    public DbListTreeMouseListener(JTree tree) {
        this.tree = tree;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            TreePath selPath = this.tree.getPathForLocation(e.getX(), e.getY());
            if (selPath != null) {
                DefaultMutableTreeNode cast = ObserverUtil.cast(DefaultMutableTreeNode.class, selPath.getLastPathComponent());
                Object userObject = cast.getUserObject();
                if (userObject instanceof ConnectionSelected.DataBase) {
                    GlobalVar.setDb(((ConnectionSelected.DataBase)userObject).getName());
                    cast.removeAllChildren();
                    QueryResult queryResult = InfluxDbUtil.execute("SHOW MEASUREMENTS");
                    List<String> list = InfluxDbUtil.toList(queryResult);

                    for (String s : list) {
                        cast.add(new DefaultMutableTreeNode(s));
                    }

                    TreeModel model = this.tree.getModel();
                    if (model instanceof DefaultTreeModel) {
                        ((DefaultTreeModel)model).reload();
                    }

                    this.tree.setSelectionPath(selPath);
                    this.tree.expandPath(selPath);
                    this.tree.scrollPathToVisible(selPath);
                }

            }
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
