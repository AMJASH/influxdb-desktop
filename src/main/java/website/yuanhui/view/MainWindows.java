package website.yuanhui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import website.yuanhui.view.left.MainLeftView;
import website.yuanhui.view.right.MainRightView;
import website.yuanhui.view.top.MainTopView;

public class MainWindows extends JFrame {
    public MainWindows() {
        this.setTitle("InfluxDb-Desktop");
        this.setDefaultCloseOperation(3);
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(800, 600));
        this.init();
        this.setVisible(true);
    }

    public void init() {
        this.add(MainTopView.init(), "North");
        this.add(MainLeftView.init(), "West");
        this.add(MainRightView.init(), "Center");
    }

    public void open() {
        this.setVisible(true);
    }
}