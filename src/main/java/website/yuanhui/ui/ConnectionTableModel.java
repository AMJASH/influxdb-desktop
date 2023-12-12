
package website.yuanhui.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class ConnectionTableModel extends AbstractTableModel {
    private final List<String> columnName = new ArrayList<>();
    private final Map<String, List<Object>> data = new HashMap<>();
    private final List<TableModelListener> listenerList = new ArrayList<>();

    public ConnectionTableModel() {
    }

    public int getRowCount() {
        return this.data.keySet().stream().map(this.data::get).filter(Objects::nonNull).mapToInt(List::size).max().orElse(0);
    }

    public int getColumnCount() {
        return this.columnName.size();
    }

    public void addColumnName(String name) {
        this.columnName.add(name);
    }

    public void addColumnData(String column, Object val) {
        List<Object> objects = this.data.computeIfAbsent(column, (k) -> new ArrayList<>());
        objects.add(val);
    }

    public String getColumnName(int columnIndex) {
        return this.columnName.get(columnIndex);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return this.getColumnName(columnIndex).getClass();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        List<Object> strings = this.data.get(this.getColumnName(columnIndex));
        return strings.get(rowIndex);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        List<Object> objects = this.data.get(this.getColumnName(rowIndex));
        objects.set(columnIndex, aValue);
    }

    public void addTableModelListener(TableModelListener l) {
        this.listenerList.add(l);
    }

    public void removeTableModelListener(TableModelListener l) {
        this.listenerList.remove(l);
    }

    public void deleteRow(int row) {
        for (String s : this.columnName) {
            List<Object> objects = this.data.get(s);
            objects.remove(row);
        }
        this.refresh(new TableModelEvent(this, row, row, -1, TableModelEvent.DELETE));
    }

    public void refresh(TableModelEvent e) {
        for (TableModelListener tableModelListener : this.listenerList) {
            tableModelListener.tableChanged(e);
        }
    }

    public void clear() {
        this.data.clear();
    }

    public Map<String, Object> getData(int row) {
        if (row == -1) {
            return Collections.emptyMap();
        } else {
            Map<String, Object> target = new HashMap<>();

            for (String s : this.columnName) {
                target.put(s, this.data.get(s).get(row));
            }

            return target;
        }
    }
}
