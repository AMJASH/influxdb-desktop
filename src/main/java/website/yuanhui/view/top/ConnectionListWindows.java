
package website.yuanhui.view.top;

import website.yuanhui.action.ConnectionInfo;
import website.yuanhui.global.GlobalVar;
import website.yuanhui.ui.ConnectionTableModel;
import website.yuanhui.util.JDBCUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConnectionListWindows extends JFrame {
    private static final ConnectionListWindows instance = new ConnectionListWindows();
    private JTable jt;
    private ConnectionTableModel dm;
    private int selectRow = -1;

    private ConnectionListWindows() {
        this.setTitle("connection List");
        this.setSize(800, 300);
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.init();
        this.setVisible(false);
    }

    private ConnectionTableModel initTableModel() {
        this.dm = new ConnectionTableModel();
        this.dm.addColumnName("链接名称");
        this.dm.addColumnName("链接地址");
        this.dm.addColumnName("用户名");
        this.dm.addColumnName("密码");
        return this.dm;
    }

    private void initTable() {
        this.jt = new JTable(this.initTableModel());
        JScrollPane scrollpane = new JScrollPane(this.jt);
        scrollpane.setBounds(300, 100, 700, 500);
        this.jt.setBounds(300, 800, 700, 500);
        this.add(scrollpane, "Center");
    }

    private void initButton() {
        JMenuBar bg = new JMenuBar();
        bg.add(new JButton(new AbstractAction("选中") {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ConnectionListWindows.this.jt.getSelectedRow();
                if (selectedRow != -1) {
                    ConnectionListWindows.this.selectRow = ConnectionListWindows.this.jt.getSelectedRow();
                    Optional<ConnectionInfo> selected = ConnectionListWindows.selected();
                    selected.ifPresent(GlobalVar::setConnectionInfo);
                    GlobalVar.notifyObservers(selected);
                    ConnectionListWindows.close();
                }
            }
        }));
        bg.add(new JButton(new AbstractAction("删除") {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ConnectionListWindows.this.jt.getSelectedRow();
                if (selectedRow != -1) {
                    Object o = ConnectionListWindows.this.dm.getData(selectedRow).get("链接名称");
                    JDBCUtil.execute("DELETE FROM CONN_INFO WHERE NAME = '" + o + "';");
                    ConnectionListWindows.this.dm.deleteRow(selectedRow);
                    int rowCount = ConnectionListWindows.this.jt.getRowCount();
                    if (rowCount > selectedRow) {
                        ConnectionListWindows.this.jt.changeSelection(selectedRow, 0, false, false);
                    } else if (rowCount != 0) {
                        ConnectionListWindows.this.jt.changeSelection(selectedRow - 1, 0, false, false);
                    }

                }
            }
        }));
        bg.add(new JButton(new AbstractAction("退出") {
            public void actionPerformed(ActionEvent e) {
                ConnectionListWindows.close();
            }
        }));
        bg.setLayout(new FlowLayout());
        this.add(bg, "South");
    }

    private void init() {
        this.initTable();
        this.initButton();
    }

    public void loadConnectionList() {
        List<ConnectionInfo> load = ConnectionInfo.load();
        this.dm.clear();

        for (ConnectionInfo connectionInfo : load) {
            this.dm.addColumnData("链接名称", connectionInfo.getName());
            this.dm.addColumnData("链接地址", connectionInfo.getUrl());
            this.dm.addColumnData("用户名", connectionInfo.getUsername());
            this.dm.addColumnData("密码", connectionInfo.getPassword());
        }

        this.dm.refresh(new TableModelEvent(this.dm));
    }

    public static Optional<ConnectionInfo> selected() {
        if (instance.selectRow == -1) {
            return Optional.empty();
        } else {
            ConnectionInfo r = new ConnectionInfo();
            Map<String, Object> data = instance.dm.getData(instance.selectRow);
            r.setName(data.get("链接名称").toString());
            r.setPassword(data.get("密码").toString());
            r.setUsername(data.get("用户名").toString());
            r.setUrl(data.get("链接地址").toString());
            return Optional.of(r);
        }
    }

    public static void close() {
        instance.setVisible(false);
    }

    public static void open() {
        instance.loadConnectionList();
        instance.setVisible(true);
    }
}
