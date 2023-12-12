package website.yuanhui.view.right;

import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import website.yuanhui.action.SqlRunAction;

public class MainRightView {
    public MainRightView() {
    }

    public static Component init() {
        JTextArea sqlArea = new JTextArea();
        sqlArea.setRows(8);
        JScrollPane sql = new JScrollPane(sqlArea);
        JTabbedPane jtp = new JTabbedPane();
        jtp.setTabLayoutPolicy(0);
        jtp.setTabPlacement(1);
        JSplitPane splitPane = new JSplitPane(0, false, sql, jtp);
        splitPane.setDividerLocation(0.2);
        splitPane.setVisible(true);
        new SqlRunAction(sqlArea, jtp);
        return splitPane;
    }
}
