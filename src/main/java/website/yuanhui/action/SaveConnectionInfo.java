
package website.yuanhui.action;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.text.JTextComponent;

import com.google.gson.Gson;
import website.yuanhui.util.CmdUtil;
import website.yuanhui.util.ComponentUtil;
import website.yuanhui.util.JDBCUtil;
import website.yuanhui.view.top.ConnectionCreateWindows;

public class SaveConnectionInfo implements ActionListener {
    public SaveConnectionInfo() {
    }

    public void actionPerformed(ActionEvent e) {
        CmdUtil.doCmd(e, this);
    }

    @SuppressWarnings("unused")
    public void connectCancel(ActionEvent e) {
        Container parentContainer = ComponentUtil.getParentContainer(e);

        assert parentContainer != null;

        Component[] parentComponents = parentContainer.getComponents();
        for (Component parentComponent : parentComponents) {
            if (parentComponent instanceof JTextComponent) {
                ((JTextComponent) parentComponent).setText("");
            }
        }
        ConnectionCreateWindows.close();
    }

    @SuppressWarnings("unused")
    public void connectSubmit(ActionEvent e) {
        Map<String, String> stringStringMap = ComponentUtil.parentTextValMap(e);
        Gson gson = new Gson();
        ConnectionInfo javaObject = gson.fromJson(gson.toJson(stringStringMap), ConnectionInfo.class);
        JDBCUtil.save(javaObject);
    }
}
