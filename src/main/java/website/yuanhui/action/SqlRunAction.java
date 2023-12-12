
package website.yuanhui.action;

import org.influxdb.dto.QueryResult;
import website.yuanhui.global.GlobalVar;
import website.yuanhui.util.ComponentUtil;
import website.yuanhui.util.InfluxDbUtil;
import website.yuanhui.util.ObserverUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SqlRunAction implements Observer {
    private final JTextArea sql;
    private final JTabbedPane ui;

    public SqlRunAction(JTextArea sql, JTabbedPane ui) {
        this.sql = sql;
        this.ui = ui;
        GlobalVar.add(this);
    }

    public void update(Observable o, Object arg) {
        String cmd = ObserverUtil.cast(String.class, arg);
        if ("invokeSql".equals(cmd)) {
            String sqlText = this.sql.getSelectedText();
            if (sqlText == null || sqlText.trim().isEmpty()) {
                sqlText = this.sql.getText();
            }

            try {
                QueryResult execute = InfluxDbUtil.execute(sqlText);
                if (execute == null) {
                    ComponentUtil.closeTab(this.ui, "success", null, new JLabel(sqlText + " run success"), null, Color.green);
                } else {
                    this.append(execute, sqlText);
                }
            } catch (Exception var13) {
                String message = var13.getMessage();
                if (message == null) {
                    message = var13.getClass().getName();
                }
                JTextArea jTextArea = new JTextArea(message);
                jTextArea.append("\n");
                StackTraceElement[] trace = var13.getStackTrace();
                for (StackTraceElement traceElement : trace) {
                    jTextArea.append("    at ");
                    jTextArea.append(traceElement.toString());
                    jTextArea.append("\n");
                }

                ComponentUtil.closeTab(this.ui, "error", null, new JScrollPane(jTextArea), sqlText, Color.red);
            }

        }
    }

    public void append(QueryResult execute, String sqlText) {
        String error = execute.getError();
        if (error != null && !error.isEmpty()) {
            ComponentUtil.closeTab(this.ui, "error", null, new JLabel(sqlText + " run fail"), "", Color.red);
        }

        List<QueryResult.Result> results = execute.getResults();
        results.forEach((o) -> this.append(o, sqlText));
    }

    public void append(QueryResult.Result result, String sql) {
        if (result != null) {
            String error = result.getError();
            JTabbedPane resultTab = new JTabbedPane();
            if (error != null && error.trim().isEmpty()) {
                int tabCount = resultTab.getTabCount();
                resultTab.addTab("error", null, new JLabel(error));
                resultTab.setBackgroundAt(tabCount, Color.red);
            }

            List<QueryResult.Series> series = result.getSeries();

            for (QueryResult.Series s : series) {
                this.append(s, resultTab);
            }

            ComponentUtil.closeTab(this.ui, "result", null, resultTab, sql, Color.cyan);
        }
    }

    public void append(QueryResult.Series series, JTabbedPane resultTab) {
        if (series != null) {
            String name = series.getName();
            Object[][] dd = new Object[series.getValues().size()][series.getColumns().size()];
            for(int i = 0; i < series.getValues().size(); ++i) {
                List<Object> objects = series.getValues().get(i);
                for(int j = 0; j < objects.size(); ++j) {
                    dd[i][j] = objects.get(j);
                }
            }
            JTable table = new JTable(dd, series.getColumns().toArray());
            resultTab.addTab(name, new JScrollPane(table));
        }
    }
}
