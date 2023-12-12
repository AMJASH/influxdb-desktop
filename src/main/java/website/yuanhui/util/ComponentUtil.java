package website.yuanhui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class ComponentUtil {
    public ComponentUtil() {
    }

    public static JMenuItem createMenuItem(String text, String name, ActionListener listener) {
        JMenuItem item = new JMenuItem();
        item.setText(text);
        item.setName(name);
        item.addActionListener(listener);
        return item;
    }

    public static JTextField textField(String name) {
        JTextField target = new JTextField();
        target.setName(name);
        return target;
    }

    public static JLabel label(String text) {
        JLabel target = new JLabel();
        target.setText(text);
        return target;
    }

    public static Map<String, String> parentTextValMap(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof Component) {
            Container parent = ((Component)source).getParent();
            Map<String, String> target = new HashMap();
            Component[] var4 = parent.getComponents();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Component component = var4[var6];
                if (component instanceof JTextComponent) {
                    target.put(component.getName(), ((JTextComponent)component).getText());
                }
            }

            return target;
        } else {
            return Collections.emptyMap();
        }
    }

    public static List<Component> getParentComponents(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof Component) {
            Container parent = ((Component)source).getParent();
            return Arrays.asList(parent.getComponents());
        } else {
            return Collections.emptyList();
        }
    }

    public static Container getParentContainer(ActionEvent e) {
        Object source = e.getSource();
        return source instanceof Component ? ((Component)source).getParent() : null;
    }

    public static void closeTab(final JTabbedPane pane, String title, Icon icon, Component component, String tip, Color color) {
        int tabCount = pane.getTabCount();
        pane.addTab(title, icon, component, tip);
        pane.setBackgroundAt(tabCount, color);
        final Box closePanel = Box.createHorizontalBox();
        JLabel comp = new JLabel(title);
        final JButton closeButton = new JButton("x");
        closeButton.setMaximumSize(new Dimension(20, 20));
        closeButton.addMouseListener(new MouseAdapter() {
            private Color c;

            public void mouseEntered(MouseEvent e) {
                this.c = closeButton.getForeground();
                closeButton.setForeground(Color.RED);
            }

            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(this.c);
            }

            public void mouseClicked(MouseEvent e) {
                pane.remove(pane.indexOfTabComponent(closePanel));
            }
        });
        closePanel.add(comp);
        closePanel.add(closeButton);
        pane.setTabComponentAt(tabCount, closePanel);
    }
}
