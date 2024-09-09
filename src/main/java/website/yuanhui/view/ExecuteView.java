package website.yuanhui.view;

import org.influxdb.dto.QueryResult;
import website.yuanhui.model.event.SwingEventListenerContext;
import website.yuanhui.model.event.impl.CmdRunEvent;
import website.yuanhui.swing.CloseTabComponent;
import website.yuanhui.util.I18NUtil;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExecuteView extends JPanel {
    private final JTextArea query;
    private final JLabel title;
    private final JTabbedPane tabbedPane;

    public ExecuteView() {
        setLayout(new BorderLayout());
        title = initDatabaseName();
        query = initQuery();
        JScrollPane comp = new JScrollPane(query);
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, comp, tabbedPane);
        splitPane.setResizeWeight(0.4);
        splitPane.setOneTouchExpandable(true);
        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel initDatabaseName() {
        JLabel title = new JLabel(I18NUtil.getString("ExecuteView.current.database"));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel jPanel = new JPanel();
        jPanel.add(title);
        add(jPanel, BorderLayout.NORTH);
        return title;
    }

    private JTextArea initQuery() {
        JTextArea query = new JTextArea(40, 0);
        query.setLineWrap(false);
        query.setWrapStyleWord(true);
        UndoManager undoManager = new UndoManager();
        query.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        // 添加撤销快捷键
        InputMap inputMap = query.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = query.getActionMap();
        int cmd = System.getProperty("os.name").toLowerCase().startsWith("mac") ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, cmd), "undo");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, cmd), "runSql");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, cmd | InputEvent.SHIFT_DOWN_MASK), "redo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        // 添加重做快捷键
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
        actionMap.put("runSql", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingEventListenerContext.publishEvent(new CmdRunEvent(null));
            }
        });
        return query;
    }

    public void title(String title) {
        if (Objects.equals(this.title.getName(), title)) {
            return;
        }
        int width = this.getSize().width;
        if (this.title.getSize().width != width) {
            Dimension dimension = new Dimension(width, this.title.getSize().height);
            this.title.setSize(dimension);
            this.title.setMaximumSize(dimension);
            this.title.setMinimumSize(dimension);
            this.title.setPreferredSize(dimension);
        }
        this.title.setText(I18NUtil.getString("ExecuteView.current.database") + title);
        this.title.setName(title);

    }

    public String title() {
        return this.title.getName();
    }

    public void updateTable(QueryResult queryResult, String sql) {
        addResultTabs(queryResult, sql);
    }

    public JTextArea getQuery() {
        return query;
    }

    public void addResultTabs(QueryResult queryResult, String sql) {
        String[] split = sql.split(";");
        for (int i = 0; i < queryResult.getResults().size(); i++) {
            QueryResult.Result result = queryResult.getResults().get(i);
            String resultTitle = split[i];
            JTabbedPane subTabbedPane = new JTabbedPane();
            if (result.getSeries() == null) {
                tabbedPane.addTab(resultTitle, subTabbedPane);
                CloseTabComponent component = new CloseTabComponent(resultTitle, 50, tabbedPane);
                tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, component);
                continue;
            }
            for (QueryResult.Series series : result.getSeries()) {
                DefaultTableModel model = new DefaultTableModel();
                model.setColumnIdentifiers(series.getColumns().toArray());
                for (List<Object> row : series.getValues()) {
                    model.addRow(row.toArray());
                }
                JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
                table.setRowHeight(20);
                subTabbedPane.addTab(series.getName(), scrollPane);
            }
            tabbedPane.addTab(resultTitle, subTabbedPane);
            CloseTabComponent component = new CloseTabComponent(resultTitle, 50, tabbedPane);
            tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, component);
        }
    }

    public QueryResult.Result getCurrentResult() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex == -1) {
            return null;
        }
        QueryResult.Result result = new QueryResult.Result();
        List<QueryResult.Series> dataSeries = new ArrayList<>();
        Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
        if (selectedComponent instanceof JTabbedPane subTabbedPane) {
            for (int i = 0; i < subTabbedPane.getTabCount(); i++) {
                Component subSelectedComponent = subTabbedPane.getComponentAt(i);
                if (subSelectedComponent instanceof JScrollPane scrollPane) {
                    QueryResult.Series series = new QueryResult.Series();
                    series.setName(subTabbedPane.getTitleAt(i));
                    JTable table = (JTable) scrollPane.getViewport().getView();
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    List<String> columnNames = new ArrayList<>();
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        columnNames.add(model.getColumnName(j));
                    }
                    series.setColumns(columnNames);

                    List<List<Object>> rows = new ArrayList<>();
                    for (int j = 0; j < model.getRowCount(); j++) {
                        List<Object> row = new ArrayList<>();
                        for (int k = 0; k < model.getColumnCount(); k++) {
                            row.add(model.getValueAt(j, k));
                        }
                        rows.add(row);
                    }
                    series.setValues(rows);
                    dataSeries.add(series);
                }
            }
            result.setSeries(dataSeries);
            return result;
        }
        return null;
    }
}