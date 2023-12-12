
package website.yuanhui.action;

import website.yuanhui.global.GlobalVar;
import website.yuanhui.util.InfluxDbUtil;
import website.yuanhui.util.ObserverUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ConnectionSelected implements Observer {
    private final JTree tree;

    public ConnectionSelected(JTree tree) {
        this.tree = tree;
        GlobalVar.add(this);
    }

    public void update(Observable o, Object arg) {
        ConnectionInfo cast = ObserverUtil.cast(ConnectionInfo.class, arg);
        if (cast != null) {
            this.refreshDatabase();
        }
    }

    public void refreshDatabase() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("databases");
        List<String> strings = InfluxDbUtil.dbNames();

        for (String string : strings) {
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(new DataBase(string));
            top.add(newChild);
        }

        this.tree.setModel(new DefaultTreeModel(top));
    }

    static class DataBase {
        private final String name;

        public DataBase(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}
