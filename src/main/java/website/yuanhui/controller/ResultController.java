package website.yuanhui.controller;

import org.influxdb.dto.QueryResult;
import website.yuanhui.model.event.Event;
import website.yuanhui.model.event.EventListener;
import website.yuanhui.model.event.SwingEventListenerContext;
import website.yuanhui.model.event.impl.CmdRunEvent;
import website.yuanhui.model.event.impl.CreateQueryEvent;
import website.yuanhui.model.influxdb.client.v1.client.InfluxdbV1Client;
import website.yuanhui.model.log.LOG;
import website.yuanhui.view.ExecuteView;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ResultController implements EventListener {
    private final ExecuteView view;
    private final Map<String, CreateQueryEvent> cache = new HashMap<>();

    public ResultController(ExecuteView view) {
        this.view = view;
        SwingEventListenerContext.register(this);
    }

    @Override
    public <T> void apply(Event<T> event) {
        if ((event instanceof CreateQueryEvent)) {
            createQuery((CreateQueryEvent) event);
            return;
        }
        if (event instanceof CmdRunEvent) {
            try {
                runQuery();
            } catch (Exception e) {
                LOG.ERROR.msg("runQuery", e);
            }
        }
    }

    public void runQuery() throws BadLocationException, NoSuchAlgorithmException, KeyManagementException {
        String title = view.title();
        CreateQueryEvent obj = cache.get(title);
        if (title == null) {
            return;
        }
        if (obj == null) {
            return;
        }
        JTextArea query = view.getQuery();
        String sql = getSelectText(query);
        if (sql.isEmpty()) {
            sql = query.getText();
        }
        if (sql.isEmpty()) {
            return;
        }
        runQuery(sql, obj);
    }

    private String getSelectText(JTextArea textArea) throws BadLocationException {
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        Element root = textArea.getDocument().getDefaultRootElement();
        int startLine = root.getElementIndex(start);
        int endLine = root.getElementIndex(end);
        StringBuilder sb = new StringBuilder();
        Document document = textArea.getDocument();
        String ls = System.lineSeparator();
        for (int i = startLine; i < endLine; i++) {
            Element element = root.getElement(i);
            sb.append(document.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset()));
            sb.append(ls);
        }
        if (sb.length() >= ls.length()) {
            sb.setLength(sb.length() - ls.length());
        }
        return sb.toString();
    }

    public void createQuery(CreateQueryEvent obj) {
        String tabName = String.format("[%s]->[%s]", obj.info().getName(), obj.database());
        cache.put(tabName, obj);
        view.title(tabName);
        view.getQuery().setText(obj.source());
        if (obj.isExecute()) {
            try {
                QueryResult query = new InfluxdbV1Client(obj.info()).query(obj.source(), obj.database());
                view.updateTable(query, obj.source());
            } catch (Exception e) {
                LOG.ERROR.msg("执行sql异常", e);
            }
        }

    }

    public void runQuery(String sql, CreateQueryEvent obj) throws NoSuchAlgorithmException, KeyManagementException {
        QueryResult query = new InfluxdbV1Client(obj.info()).query(sql, obj.database());
        SwingUtilities.invokeLater(() -> view.updateTable(query, sql));
    }
}
