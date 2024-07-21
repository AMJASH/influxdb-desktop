package website.yuanhui;


import website.yuanhui.view.MainWindows;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindows().setVisible(true));
    }
}
