package website.yuanhui.controller;

import website.yuanhui.model.SQLiteModel;
import website.yuanhui.model.event.Event;
import website.yuanhui.model.event.EventListener;
import website.yuanhui.model.event.SwingEventListenerContext;
import website.yuanhui.model.event.impl.ConnectInfoAddEvent;
import website.yuanhui.model.event.impl.ConnectInfoDelEvent;
import website.yuanhui.model.event.impl.CreateQueryEvent;
import website.yuanhui.model.influxdb.client.Database;
import website.yuanhui.model.influxdb.client.InfluxDbDatabaseModel;
import website.yuanhui.model.influxdb.client.Measurement;
import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;
import website.yuanhui.model.influxdb.client.v1.client.InfluxdbV1Client;
import website.yuanhui.model.log.LOG;
import website.yuanhui.util.ClassUtil;
import website.yuanhui.util.I18NUtil;
import website.yuanhui.util.SwingUtil;
import website.yuanhui.view.ConnectListView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionListController implements EventListener {

    private final ConnectListView view;
    private final Map<Long, InfluxDbDatabaseModel> cache = new HashMap<>();

    public ConnectionListController(ConnectListView view) {
        SwingEventListenerContext.register(this);
        this.view = view;
        init();
        //双击打开数据库
        this.view.addTreeMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent ee) {
                if (ee.getClickCount() == 2) {
                    JTree tree = ClassUtil.cast(ee.getSource(), JTree.class);
                    if (tree == null) {
                        return;
                    }
                    TreePath path = tree.getPathForLocation(ee.getX(), ee.getY());
                    openDatabase(tree, path);
                    openMeasurements(tree, path);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        //添加右键菜单
        this.view.addTreeMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    openConnectionPopup(e);
                    openDatabasePopup(e);
                    openMeasurementPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    openConnectionPopup(e);
                    openDatabasePopup(e);
                    openMeasurementPopup(e);
                }
            }
        });
    }

    private void openConnectionPopup(MouseEvent ee) {
        JPopupMenu popup = new JPopupMenu();
        JTree tree = ClassUtil.cast(ee.getSource(), JTree.class);
        if (tree == null) {
            return;
        }
        TreePath path = tree.getPathForLocation(ee.getX(), ee.getY());
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode node = ClassUtil.cast(path.getLastPathComponent(), DefaultMutableTreeNode.class);
        if (node == null) {
            return;
        }
        ConnectInfoV1 info = ClassUtil.cast(node.getUserObject(), ConnectInfoV1.class);
        if (info == null) {
            return;
        }
        tree.setSelectionPath(path);
        JMenuItem openConn = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.openConn"));
        openConn.addActionListener(e -> {
            if (cache.containsKey(info.getId())) {
                return;
            }
            try {
                InfluxDbDatabaseModel model = new InfluxDbDatabaseModel(new InfluxdbV1Client(info));
                cache.put(info.getId(), model);
                node.add(model);
            } catch (Exception ex) {
                LOG.ERROR.msg("连接失败", ex);
            }
        });
        JMenuItem refreshConn = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.refreshConn"));
        refreshConn.addActionListener(e -> {
            try {
                node.removeAllChildren();
                InfluxDbDatabaseModel model = new InfluxDbDatabaseModel(new InfluxdbV1Client(info));
                cache.put(info.getId(), model);
                node.add(model);
                this.view.getModel().nodeStructureChanged(node);
            } catch (Exception ex) {
                LOG.ERROR.msg(ex.toString(), ex);
            }

        });
        JMenuItem closeConn = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.closeConn"));
        closeConn.addActionListener(e -> {
            cache.remove(info.getId());
            node.removeAllChildren();
            this.view.getModel().nodeStructureChanged(node);
        });
        popup.add(openConn);
        popup.add(refreshConn);
        popup.add(closeConn);
        popup.show(tree, ee.getX(), ee.getY());
    }

    private void openDatabasePopup(MouseEvent ee) {
        JTree tree = ClassUtil.cast(ee.getSource(), JTree.class);
        if (tree == null) {
            return;
        }
        TreePath path = tree.getPathForLocation(ee.getX(), ee.getY());
        if (path == null) {
            return;
        }
        SwingUtil.Result<Database> database = SwingUtil.getAncestorsUserObj(path, Database.class);
        SwingUtil.Result<ConnectInfoV1> info = SwingUtil.getAncestorsUserObj(path, ConnectInfoV1.class);
        if (database == null || info == null) {
            return;
        }
        if (!cache.containsKey(info.obj.getId())) {
            return;
        }
        tree.setSelectionPath(path);
        JMenuItem openSQL = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.openSql"));
        JPopupMenu popup = new JPopupMenu();
        openSQL.addActionListener(e -> {
            ///新建查询窗口
            String sql = "select * from measurement order by time desc limit 100 ;";
            SwingEventListenerContext.publishEvent(new CreateQueryEvent(sql, info.obj, database.obj.getName()));
        });
        JMenuItem refresh = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.refresh"));
        refresh.addActionListener(e -> {
            //刷新openMeasurement
            database.node.removeAllChildren();
            openMeasurements(tree, path);
        });
        JMenuItem close = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.close"));
        close.addActionListener(e -> {
            database.node.removeAllChildren();
            this.view.getModel().nodeStructureChanged(database.node);
        });
        popup.add(openSQL);
        popup.add(refresh);
        popup.add(close);
        popup.show(tree, ee.getX(), ee.getY());
    }

    private void openMeasurementPopup(MouseEvent ee) {
        JPopupMenu popup = new JPopupMenu();
        JTree tree = ClassUtil.cast(ee.getSource(), JTree.class);
        if (tree == null) {
            return;
        }
        TreePath path = tree.getPathForLocation(ee.getX(), ee.getY());
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode node = ClassUtil.cast(path.getLastPathComponent(), DefaultMutableTreeNode.class);
        if (node == null) {
            return;
        }
        Measurement measurement = ClassUtil.cast(node.getUserObject(), Measurement.class);
        if (measurement == null) {
            return;
        }
        tree.setSelectionPath(path);
        JMenuItem openMeasurement = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.openMeasurement"));
        DefaultMutableTreeNode nodeOfDatabase = ClassUtil.cast(node.getParent(), DefaultMutableTreeNode.class);
        if (nodeOfDatabase == null) {
            return;
        }
        DefaultMutableTreeNode nodeOfConnectionInfo = ClassUtil.cast(node.getParent().getParent().getParent(), DefaultMutableTreeNode.class);
        if (nodeOfConnectionInfo == null) {
            return;
        }
        Database database = ClassUtil.cast(nodeOfDatabase.getUserObject(), Database.class);
        ConnectInfoV1 info = ClassUtil.cast(nodeOfConnectionInfo.getUserObject(), ConnectInfoV1.class);
        if (database == null || info == null) {
            return;
        }
        if (!cache.containsKey(info.getId())) {
            return;
        }
        openMeasurement.addActionListener(e -> {
            //新建查询窗口 默认带入执行语句
            //且执行sql查询 默认100条 按照时间倒排
            String sql = "select * from \"" + measurement.getName() + "\" order by time desc limit 100 " + I18NUtil.getString("tz.default") + ";";
            SwingEventListenerContext.publishEvent(new CreateQueryEvent(sql, info, database.getName(), true));
        });
        JMenuItem openSql = new JMenuItem(I18NUtil.getString("ConnectionListController.JMenuItem.openSql"));
        openSql.addActionListener(e -> {
            ///新建查询窗口 默认带入执行语句
            String sql = "select * from \"" + measurement.getName() + "\" order by time desc limit 100 " + I18NUtil.getString("tz.default") + ";";
            SwingEventListenerContext.publishEvent(new CreateQueryEvent(sql, info, database.getName()));
        });
        popup.add(openMeasurement);
        popup.add(openSql);
        popup.show(tree, ee.getX(), ee.getY());
    }

    private void openMeasurements(JTree tree, TreePath path) {
        if (path == null) {
            return;
        }
        SwingUtil.Result<Database> database = SwingUtil.getAncestorsUserObj(path, Database.class);
        SwingUtil.Result<ConnectInfoV1> info = SwingUtil.getAncestorsUserObj(path, ConnectInfoV1.class);
        if (database == null || info == null) {
            return;
        }
        if (database.node.getChildCount() > 0) {
            return;
        }
        database.node.add(new DefaultMutableTreeNode(I18NUtil.getString("option.loading")));
        tree.expandPath(path);
        this.view.getModel().nodeStructureChanged(database.node);
        InfluxDbDatabaseModel query = cache.get(info.obj.getId());
        new Thread(() -> {
            try {
                List<String> measurements = query.measurementNames(database.obj.getName());
                SwingUtil.updateTree(database.node, path, measurements, this.view.getModel(), tree, Measurement::new);
            } catch (Exception ex) {
                SwingUtil.updateSingleTree(database.node, path, I18NUtil.getString("option.failed") + ex.getMessage(), this.view.getModel(), tree);
                LOG.ERROR.msg("连接失败", ex);
            }
        }).start();
    }

    private void openDatabase(JTree tree, TreePath path) {
        if (path == null) {
            return;
        }
        SwingUtil.Result<ConnectInfoV1> info = SwingUtil.getAncestorsUserObj(path, ConnectInfoV1.class);
        if (info == null) {
            return;
        }
        DefaultMutableTreeNode node = info.node;
        if (cache.containsKey(info.obj.getId())) {
            return;
        }
        if (node.getChildCount() > 0) {
            return;
        }
        node.add(new DefaultMutableTreeNode(I18NUtil.getString("option.loading")));
        tree.expandPath(path);
        this.view.getModel().nodeStructureChanged(node);
        new Thread(() -> {
            try {
                InfluxDbDatabaseModel model = new InfluxDbDatabaseModel(new InfluxdbV1Client(info.obj));
                cache.put(info.obj.getId(), model);
                SwingUtil.updateSingleTree(node, path, model, this.view.getModel(), tree);
            } catch (Exception ex) {
                SwingUtil.updateSingleTree(node, path, ex.getMessage(), this.view.getModel(), tree);
                LOG.ERROR.msg("连接失败", ex);
            }
        }).start();
    }

    public void show() {
        view.setVisible(true);
    }

    void init() {
        this.view.clear();
        List<ConnectInfoV1> all = SQLiteModel.findAll();
        for (ConnectInfoV1 v1 : all) {
            this.view.addConnect(v1);
        }
        this.view.reload();
    }

    public void reload() {
        init();
    }

    public void addConnect(ConnectInfoV1 v1) {
        this.view.addConnect(v1);
    }

    public void removeConnect(Long selectId) {
        this.view.removeConnect(selectId);
    }

    @Override
    public <T> void apply(Event<T> event) {
        if (event instanceof ConnectInfoAddEvent) {
            SwingUtilities.invokeLater(() -> {
                ConnectInfoV1 e = ClassUtil.cast(event.source(), ConnectInfoV1.class);
                addConnect(e);
                reload();
            });
        } else if (event instanceof ConnectInfoDelEvent) {
            SwingUtilities.invokeLater(() -> {
                ConnectInfoV1 e = ClassUtil.cast(event.source(), ConnectInfoV1.class);
                if (e != null) {
                    removeConnect(e.getId());
                    reload();
                }
            });
        }
    }
}
