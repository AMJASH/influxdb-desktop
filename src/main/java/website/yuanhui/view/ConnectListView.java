package website.yuanhui.view;

import website.yuanhui.model.influxdb.client.Database;
import website.yuanhui.model.influxdb.client.Measurement;
import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;
import website.yuanhui.util.ClassUtil;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseListener;

/**
 * 根据listModel 显示连接名称 出现在主界面的左侧
 */
public class ConnectListView extends JPanel {
    private final JTree tree;
    private final DefaultTreeModel model;

    public ConnectListView() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        this.model = new DefaultTreeModel(root);
        this.tree = new JTree(model);
        this.tree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                    if (userObject instanceof String) {
                        setText((String) userObject);
                    } else if (userObject instanceof ConnectInfoV1) {
                        setText(((ConnectInfoV1) userObject).getName());
                    } else if (userObject instanceof Database) {
                        setText(((Database) userObject).getName());
                    } else if (userObject instanceof Measurement) {
                        setText(((Measurement) userObject).getName());
                    }
                }
                return this;
            }
        });

        this.tree.setTransferHandler(new JTreeTransferHandler());
        this.tree.setRootVisible(false);
        JScrollPane comp = new JScrollPane(tree);
        this.add(comp);
        this.setPreferredSize(new Dimension(150, 0));
    }

    public void addConnect(ConnectInfoV1 v1) {
        TreePath selectionPath = tree.getSelectionPath();
        DefaultMutableTreeNode node;
        if (selectionPath == null) {
            node = (DefaultMutableTreeNode) model.getRoot();
        } else {
            node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
        }
        if (node.isRoot()) {
            node.add(new DefaultMutableTreeNode(v1));
        }
        model.nodeStructureChanged(node);
    }

    public void reload() {
        tree.clearSelection();
        model.nodeStructureChanged((TreeNode) model.getRoot());
    }


    public void clear() {
        tree.clearSelection();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
        node.removeAllChildren();
        model.nodeStructureChanged(node);
    }


    public void removeConnect(Long selectId) {
        DefaultMutableTreeNode root = ClassUtil.cast(model.getRoot(), DefaultMutableTreeNode.class);
        if (root == null) {
            return;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode connectTree = ClassUtil.cast(root.getChildAt(i), DefaultMutableTreeNode.class);
            if (connectTree == null) {
                continue;
            }
            ConnectInfoV1 info = ClassUtil.cast(connectTree.getUserObject(), ConnectInfoV1.class);
            if (info == null) {
                continue;
            }
            if (info.getId().equals(selectId)) {
                root.remove(i);
                model.nodeStructureChanged(root);
            }
        }
    }

    public DefaultTreeModel getModel() {
        return model;
    }

    public void addTreeMouseListener(MouseListener listener) {
        tree.addMouseListener(listener);
    }

    //处理复制问题
    private static class JTreeTransferHandler extends TransferHandler {
        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                String copy;
                if (userObject instanceof String) {
                    copy = ClassUtil.cast(userObject, String.class);
                } else if (userObject instanceof ConnectInfoV1) {
                    copy = ClassUtil.cast(userObject, ConnectInfoV1.class).getName();
                } else if (userObject instanceof Database) {
                    copy = ClassUtil.cast(userObject, Database.class).getName();
                } else if (userObject instanceof Measurement) {
                    copy = ClassUtil.cast(userObject, Measurement.class).getName();
                } else {
                    return null;
                }
                return new StringSelection(copy);
            }
            return null;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY; // or COPY_OR_MOVE depending on your needs
        }
    }
}
