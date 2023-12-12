
package website.yuanhui.view.top;

import java.awt.GridLayout;
import javax.swing.JFrame;
import website.yuanhui.action.SaveConnectionInfo;
import website.yuanhui.util.ComponentUtil;

public class ConnectionCreateWindows extends JFrame {
    private static final ConnectionCreateWindows instance = new ConnectionCreateWindows();

    private ConnectionCreateWindows() {
        this.setTitle("create connection ");
        this.setSize(800, 300);
        this.setDefaultCloseOperation(1);
        GridLayout manager = new GridLayout(5, 2, 10, 20);
        this.setLayout(manager);
        this.init();
        this.setVisible(false);
    }

    public static void open() {
        instance.setVisible(true);
    }

    public static void close() {
        instance.setVisible(false);
    }

    public void init() {
        this.add(ComponentUtil.label("链接名称"));
        this.add(ComponentUtil.textField("name"));
        this.add(ComponentUtil.label("用户名"));
        this.add(ComponentUtil.textField("username"));
        this.add(ComponentUtil.label("密码"));
        this.add(ComponentUtil.textField("password"));
        this.add(ComponentUtil.label("链接"));
        this.add(ComponentUtil.textField("url"));
        this.add(ComponentUtil.createMenuItem("提交", "connectSubmit", new SaveConnectionInfo()));
        this.add(ComponentUtil.createMenuItem("取消", "connectCancel", new SaveConnectionInfo()));
    }
}
