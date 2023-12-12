
package website.yuanhui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import website.yuanhui.util.CmdUtil;
import website.yuanhui.view.top.ConnectionCreateWindows;
import website.yuanhui.view.top.ConnectionListWindows;

public class OpenConnectionManage implements ActionListener {
    public OpenConnectionManage() {
    }

    public void actionPerformed(ActionEvent e) {
        CmdUtil.doCmd(e, this);
    }

    @SuppressWarnings("unused")
    public void openConnectionCreateWindows(ActionEvent e) {
        ConnectionCreateWindows.open();
    }

    @SuppressWarnings("unused")
    public void openConnectionListWindows(ActionEvent e) {
        ConnectionListWindows.open();
    }
}