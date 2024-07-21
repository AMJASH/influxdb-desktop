package website.yuanhui.view;

import website.yuanhui.model.influxdb.client.InfluxDBConfigTableModel;
import website.yuanhui.util.I18NUtil;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

public class InfluxDBConfigView extends JDialog {
    private final JTextField nameField;
    private final JTextField uriField;
    private final JTextField usernameField;
    private final JTextField passwordField;
    private final JCheckBox sslCheckBox;
    private final JButton saveButton;
    private final JButton deleteButton;
    private final JTable table;
    private final InfluxDBConfigTableModel tableModel;

    public InfluxDBConfigView(JFrame parent) {
        super((JFrame) null, I18NUtil.getString("InfluxDBConfigView.title"), true);
        setSize(800, 600);
        setLocationRelativeTo(parent); // Center the window
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.add(new JLabel(I18NUtil.getString("InfluxDBConfigView.Name")));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel(I18NUtil.getString("InfluxDBConfigView.URI")));
        uriField = new JTextField();
        inputPanel.add(uriField);
        inputPanel.add(new JLabel(I18NUtil.getString("InfluxDBConfigView.Username")));
        usernameField = new JTextField();
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel(I18NUtil.getString("InfluxDBConfigView.Password")));
        passwordField = new JTextField();
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel(I18NUtil.getString("InfluxDBConfigView.SSL")));
        sslCheckBox = new JCheckBox();
        inputPanel.add(sslCheckBox);
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton(I18NUtil.getString("InfluxDBConfigView.Save"));
        deleteButton = new JButton(I18NUtil.getString("InfluxDBConfigView.Del"));
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        tableModel = new InfluxDBConfigTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(5).setCellRenderer(new ShowPasswordButtonRenderer());
        JScrollPane scrollPane = new JScrollPane(table);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getUriField() {
        return uriField;
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JTextField getPasswordField() {
        return passwordField;
    }

    public JCheckBox getSslCheckBox() {
        return sslCheckBox;
    }

    public JButton getSaveButton() {
        return saveButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JTable getTable() {
        return table;
    }

    public InfluxDBConfigTableModel getTableModel() {
        return tableModel;
    }

    private static class ShowPasswordButtonRenderer extends JButton implements TableCellRenderer {
        public ShowPasswordButtonRenderer() {
            setText(I18NUtil.getString("InfluxDBConfigView.Password.Show"));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
}
