package website.yuanhui.util;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SwingUtil {
    public static <T> Result<T> getAncestorsUserObj(TreePath path, Class<T> clz) {
        while (path != null) {
            Object lastPathComponent = path.getLastPathComponent();
            if (lastPathComponent instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastPathComponent;
                Object userObject = node.getUserObject();
                if (clz.isInstance(userObject)) {
                    return new Result<>(path, node, clz.cast(userObject));
                }
            }
            path = path.getParentPath();
        }
        return null;
    }

    public static <T> void updateSingleTree(DefaultMutableTreeNode node, TreePath path, T children, DefaultTreeModel model, JTree tree) {
        updateSingleTree(node, path, children, model, tree, null);
    }

    public static <T, R> void updateSingleTree(DefaultMutableTreeNode node, TreePath path, T children, DefaultTreeModel model, JTree tree, Function<T, R> apply) {
        updateTree(node, path, Collections.singletonList(children), model, tree, apply);
    }

    public static <T, R> void updateTree(DefaultMutableTreeNode node, TreePath path, List<T> children, DefaultTreeModel model, JTree tree, Function<T, R> apply) {
        SwingUtilities.invokeLater(() -> {
            node.removeAllChildren();
            for (T child : children) {
                if (apply != null) {
                    node.add(new DefaultMutableTreeNode(apply.apply(child)));
                    continue;
                }
                if (child instanceof MutableTreeNode) {
                    node.add((MutableTreeNode) child);
                    continue;
                }
                node.add(new DefaultMutableTreeNode(child));
            }
            model.nodeStructureChanged(node);
            tree.expandPath(path);
        });
    }

    public static class Result<T> {
        public final T obj;
        public final TreePath path;
        public final DefaultMutableTreeNode node;

        public Result(TreePath path, DefaultMutableTreeNode node, T obj) {
            this.path = path;
            this.node = node;
            this.obj = obj;
        }
    }
}
