package website.yuanhui.view.top;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import website.yuanhui.action.OpenConnectionManage;
import website.yuanhui.global.GlobalVar;
import website.yuanhui.util.ComponentUtil;

public class MainTopView {
    static int i = 0;

    public MainTopView() {
    }

    public static Component init() {
        JMenuBar comp = new JMenuBar();
        comp.setVisible(true);
        comp.setSize(0, 40);
        comp.add(initConnect());
        comp.add(initRun());
        return comp;
    }

    private static JMenu initConnect() {
        JMenu connect = new JMenu("链接");
        connect.add(ComponentUtil.createMenuItem("新建", "openConnectionCreateWindows", new OpenConnectionManage()));
        connect.addSeparator();
        connect.add(ComponentUtil.createMenuItem("列表", "openConnectionListWindows", new OpenConnectionManage()));
        return connect;
    }

    private static JMenu initRun() {
        JMenu connect = new JMenu("运行");
        JMenuItem invokeSql = new JMenuItem();
        invokeSql.setAction(new AbstractAction("执行sql") {
            public void actionPerformed(ActionEvent e) {
                GlobalVar.notifyObservers("invokeSql");
            }
        });
        invokeSql.setAccelerator(KeyStroke.getKeyStroke(10, 2));
        connect.add(invokeSql);
        return connect;
    }
}
