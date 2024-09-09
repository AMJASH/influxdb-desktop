package website.yuanhui.swing;

import website.yuanhui.util.I18NUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CloseTabComponent extends JPanel {
    private final JLabel closeButtonLabel;
    private final JLabel titleLabel;
    private final int titleLength;
    private final JTabbedPane parent;

    public CloseTabComponent(String title, JTabbedPane parent) {
        this(title, 0, parent);
    }

    public CloseTabComponent(String title, int titleLength, JTabbedPane parent) {
        super();
        this.parent = parent;
        this.titleLength = titleLength;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setOpaque(false);
        titleLabel = new JLabel(title);
        titleLabel.setToolTipText(title);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        setTitle(title);
        add(titleLabel);
        add(Box.createHorizontalGlue());
        closeButtonLabel = new JLabel("X");
        closeButtonLabel.setForeground(Color.RED);
        closeButtonLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setSize(closeButtonLabel);
        add(closeButtonLabel);
        initMouseListener();
    }

    private void initMouseListener() {
        JPopupMenu popupMenu = initPopupMenu();
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        closeButtonLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                    // 获取当前被点击的标签页的索引
                    int index = getCurrent();
                    if (index != -1) {
                        // 关闭当前标签页
                        parent.remove(index);
                    }
                }
            }
        });
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    parent.setSelectedIndex(getCurrent());
                }
            }
        });
    }

    private void setTitle(String title) {
        String text = title;
        if (titleLength <= 0) {
            titleLabel.setText(text);
            return;
        }
        FontMetrics fontMetrics = titleLabel.getFontMetrics(titleLabel.getFont());
        int i = fontMetrics.stringWidth(text);
        boolean add = false;
        while (i > 50) {
            text = text.substring(0, text.length() - 3);
            i = fontMetrics.stringWidth(text);
            add = true;
        }
        titleLabel.setText(text + (add ? "..." : ""));
    }

    private void setSize(JLabel label) {
        FontMetrics fontMetrics = label.getFontMetrics(label.getFont());
        int i = fontMetrics.stringWidth("x");
        Dimension minimumSize = new Dimension(i + 1, i + 1);
        label.setMinimumSize(minimumSize);
        label.setPreferredSize(minimumSize);
        label.setMaximumSize(minimumSize);
        label.setSize(minimumSize);
    }

    private JPopupMenu initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem closeLeftItem = new JMenuItem(I18NUtil.getString("CloseTabComponent.close.left"));
        JMenuItem closeRightItem = new JMenuItem(I18NUtil.getString("CloseTabComponent.close.right"));
        JMenuItem closeAllItem = new JMenuItem(I18NUtil.getString("CloseTabComponent.close.all"));
        JMenuItem closeOtherItem = new JMenuItem(I18NUtil.getString("CloseTabComponent.close.other"));
        JMenuItem copySql = new JMenuItem(I18NUtil.getString("CloseTabComponent.copy.sql"));
        popupMenu.add(copySql);
        popupMenu.add(closeLeftItem);
        popupMenu.add(closeRightItem);
        popupMenu.add(closeAllItem);
        popupMenu.add(closeOtherItem);
        closeLeftItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            int currentIndex = parent.indexOfTabComponent(this);
            for (int i = 0; i < currentIndex; i++) {
                parent.remove(0);
            }
        }));
        closeRightItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            int currentIndex = parent.indexOfTabComponent(this);
            for (int i = parent.getTabCount() - 1; i > currentIndex; i--) {
                parent.remove(currentIndex + 1);
            }
        }));
        closeAllItem.addActionListener(e -> SwingUtilities.invokeLater(parent::removeAll));
        closeOtherItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            int currentIndex = parent.indexOfTabComponent(this);
            int tabCount = parent.getTabCount();
            while (currentIndex < tabCount) {
                if (currentIndex + 1 < tabCount) {
                    parent.remove(currentIndex + 1);
                    tabCount--;
                } else {
                    break;
                }
            }
            while (tabCount > 1) {
                parent.remove(0);
                tabCount--;
            }
        }));

        copySql.addActionListener(e -> {
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(new StringSelection(titleLabel.getToolTipText()), null);
        });
        return popupMenu;
    }

    private int getCurrent() {
        return parent.indexOfTabComponent(this);
    }
}