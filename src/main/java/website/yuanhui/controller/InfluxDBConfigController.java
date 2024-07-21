package website.yuanhui.controller;

import website.yuanhui.model.SQLiteModel;
import website.yuanhui.model.event.SwingEventListenerContext;
import website.yuanhui.model.event.impl.ConnectInfoAddEvent;
import website.yuanhui.model.event.impl.ConnectInfoDelEvent;
import website.yuanhui.model.influxdb.client.InfluxDBConfigTableModel;
import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;
import website.yuanhui.view.InfluxDBConfigView;

import javax.swing.JTable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class InfluxDBConfigController {
    private final InfluxDBConfigView view;
    private Long selectId;

    public InfluxDBConfigController(InfluxDBConfigView view) {
        this.view = view;
        this.view.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                view.dispose();
            }
        });
        List<ConnectInfoV1> all = SQLiteModel.findAll();
        all.forEach(view.getTableModel()::addConfig);
        view.getTableModel().fireTableDataChanged();
        // 添加列表选中事件
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            //当选中行的时候，获取选中行的索引 并将数据更新到view中
            if (!e.getValueIsAdjusting()) {
                return;
            }
            int selectedRow = view.getTable().getSelectedRow();
            if (selectedRow != -1) {
                ConnectInfoV1 config = view.getTableModel().getConfig(selectedRow);
                view.getNameField().setText(config.getName());
                view.getPasswordField().setText("");
                view.getUriField().setText(config.getUri());
                view.getUsernameField().setText(config.getUsername());
                view.getSslCheckBox().setSelected(config.isSsl());
                selectId = config.getId();
            }
        });
        //添加密码展示事件
        view.getTable().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //ig
            }

            @Override
            public void mousePressed(MouseEvent e) {
                JTable table = view.getTable();
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column != 5) {
                    return;
                }
                view.getTableModel().setPasswordVisibility(row);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JTable table = view.getTable();
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column != 5) {
                    return;
                }
                view.getTableModel().setPasswordVisibility(row);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        view.getSaveButton().addActionListener(e -> {
            ConnectInfoV1 info = _getView();
            SQLiteModel.save(info);
            SwingEventListenerContext.publishEvent(new ConnectInfoAddEvent(info));
            refresh();
        });
        view.getDeleteButton().addActionListener(e -> {
            if (selectId == null) {
                return;
            }
            SQLiteModel.delete(selectId);
            ConnectInfoV1 v1 = view.getTableModel().removeConfig(selectId);
            SwingEventListenerContext.publishEvent(new ConnectInfoDelEvent(v1));
            view.getTableModel().fireTableDataChanged();
        });
    }

    public void clearView() {
        view.getNameField().setText("");
        view.getPasswordField().setText("");
        view.getUriField().setText("");
        view.getUsernameField().setText("");
        view.getSslCheckBox().setSelected(false);
        selectId = null;
        view.getTable().clearSelection();
    }

    public void refresh() {
        clearView();
        List<ConnectInfoV1> all = SQLiteModel.findAll();
        InfluxDBConfigTableModel tableModel = view.getTableModel();
        tableModel.clear();
        all.forEach(tableModel::addConfig);
        view.getTableModel().fireTableDataChanged();
    }

    private ConnectInfoV1 _getView() {
        ConnectInfoV1 v1 = new ConnectInfoV1();
        v1.setName(view.getNameField().getText());
        v1.setPassword(view.getPasswordField().getText());
        v1.setUri(view.getUriField().getText());
        v1.setUsername(view.getUsernameField().getText());
        v1.setSsl(view.getSslCheckBox().isSelected());
        return v1;
    }

    public void show() {
        view.setVisible(true);
    }
}
   