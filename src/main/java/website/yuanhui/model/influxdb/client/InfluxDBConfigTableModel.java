package website.yuanhui.model.influxdb.client;

import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;
import website.yuanhui.util.I18NUtil;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class InfluxDBConfigTableModel extends AbstractTableModel {
    private final List<ConnectInfoV1> configs;
    private final String[] columnNames = {I18NUtil.getString("InfluxDBConfigTableModel.Name"),
            I18NUtil.getString("InfluxDBConfigTableModel.URI"),
            I18NUtil.getString("InfluxDBConfigTableModel.Username"),
            I18NUtil.getString("InfluxDBConfigTableModel.Password"),
            I18NUtil.getString("InfluxDBConfigTableModel.SSL"),
            ""};

    public InfluxDBConfigTableModel() {
        configs = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return configs.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ConnectInfoV1 config = configs.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return config.getName();
            case 1:
                return config.getUri();
            case 2:
                return config.getUsername();
            case 3:
                return config.isShowPassword() ? config.getPassword() : "******";
            case 4:
                return config.isSsl() ? "√" : "×";
            case 5:
                return config.isShowPassword();
            default:
                return null;
        }
    }

    public void addConfig(ConnectInfoV1 config) {
        configs.add(config);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void clear() {
        configs.clear();
    }

    public ConnectInfoV1 getConfig(int selectedRow) {
        return configs.get(selectedRow);
    }

    public void setPasswordVisibility(int row) {
        if (row >= configs.size() || row < 0) {
            return;
        }
        ConnectInfoV1 v1 = configs.get(row);
        v1.setShowPassword(!v1.isShowPassword());
        fireTableRowsDeleted(row, 3);
    }

    public ConnectInfoV1 removeConfig(Long selectId) {
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).getId().equals(selectId)) {
                return configs.remove(i);
            }
        }
        return null;
    }
}
