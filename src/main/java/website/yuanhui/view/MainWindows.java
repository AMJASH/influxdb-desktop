package website.yuanhui.view;

import org.influxdb.dto.QueryResult;
import website.yuanhui.controller.ConnectionListController;
import website.yuanhui.controller.InfluxDBConfigController;
import website.yuanhui.controller.ResultController;
import website.yuanhui.model.event.SwingEventListenerContext;
import website.yuanhui.model.event.impl.CmdRunEvent;
import website.yuanhui.model.log.LOG;
import website.yuanhui.util.I18NUtil;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainWindows extends JFrame {
    public MainWindows() {
        setLayout(new BorderLayout());
        setSize(800, 600);
        // 初始化菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu(I18NUtil.getString("MainWindows.option.optionsMenu"));
        JMenuItem linkManagementMenuItem = new JMenuItem(I18NUtil.getString("MainWindows.option.LinkManage"));
        JMenuItem linkManagementRefresh = new JMenuItem(I18NUtil.getString("MainWindows.option.LinkRefresh"));
        //菜单初始化
        InfluxDBConfigView view = new InfluxDBConfigView(this);
        linkManagementMenuItem.addActionListener(e -> new InfluxDBConfigController(view).show());
        //initLeftPanel
        ConnectListView connectListView = new ConnectListView();
        ConnectionListController connectionListController = new ConnectionListController(connectListView);
        connectionListController.show();
        this.add(connectListView, BorderLayout.WEST);
        linkManagementRefresh.addActionListener(e -> connectionListController.reload());

        ExecuteView executeView = new ExecuteView();
        new ResultController(executeView);
        this.add(executeView, BorderLayout.CENTER);

        optionsMenu.add(linkManagementMenuItem);
        optionsMenu.add(linkManagementRefresh);
        menuBar.add(optionsMenu);
        {
            // 创建"查询"菜单
            JMenu cmd = new JMenu(I18NUtil.getString("MainWindows.search"));
            // 添加菜单项
            JMenuItem runItem = new JMenuItem(I18NUtil.getString("MainWindows.search.cmd.run"));
            runItem.addActionListener(e -> SwingEventListenerContext.publishEvent(new CmdRunEvent(null)));
            JMenuItem sumItem = getSumItem(executeView);
            JMenuItem averageItem = getAvgItem(executeView);
            JMenuItem exportItem = getExportItem(executeView);
            // 将菜单项添加到菜单
            cmd.add(runItem);
            cmd.add(sumItem);
            cmd.add(averageItem);
            cmd.add(exportItem);
            // 将菜单添加到菜单栏
            menuBar.add(cmd);
        }
        this.setJMenuBar(menuBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    private JMenuItem getSumItem(ExecuteView executeView) {
        JMenuItem sumItem = new JMenuItem(I18NUtil.getString("MainWindows.search.cmd.sum"));
        sumItem.addActionListener(e -> {
            try {
                String label = JOptionPane.showInputDialog(null,           // 父组件，null表示使用默认的Frame作为父组件
                        I18NUtil.getString("MainWindows.search.cmd.sum.label"), // 显示的提示信息
                        I18NUtil.getString("MainWindows.search.cmd.sum"),   // 对话框标题
                        JOptionPane.QUESTION_MESSAGE // 对话框图标类型
                        , null, null, "value").toString();
                QueryResult.Result currentResult = executeView.getCurrentResult();
                if (currentResult != null) {
                    BigDecimal sum = new BigDecimal(0);
                    if (currentResult.getSeries() != null) {
                        for (QueryResult.Series series : currentResult.getSeries()) {
                            List<String> columns = series.getColumns();
                            int columnsIndex = columns.indexOf(label);
                            for (List<Object> value : series.getValues()) {
                                sum = sum.add(new BigDecimal(value.get(columnsIndex).toString()));
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(null, sum, I18NUtil.getString("MainWindows.search.cmd.result"), JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, I18NUtil.getString("MainWindows.search.cmd.result") + exception.getMessage());
            }
        });
        return sumItem;
    }

    private JMenuItem getAvgItem(ExecuteView executeView) {
        JMenuItem averageItem = new JMenuItem(I18NUtil.getString("MainWindows.search.cmd.avg"));
        averageItem.addActionListener(e -> {
            try {
                String label = JOptionPane.showInputDialog(null,           // 父组件，null表示使用默认的Frame作为父组件
                        I18NUtil.getString("MainWindows.search.cmd.avg.label"), // 显示的提示信息
                        I18NUtil.getString("MainWindows.search.cmd.avg"),   // 对话框标题
                        JOptionPane.QUESTION_MESSAGE // 对话框图标类型
                        , null, null, "value").toString();
                QueryResult.Result currentResult = executeView.getCurrentResult();
                if (currentResult != null) {
                    BigDecimal sum = new BigDecimal(0);
                    if (currentResult.getSeries() != null) {
                        for (QueryResult.Series series : currentResult.getSeries()) {
                            List<String> columns = series.getColumns();
                            int columnsIndex = columns.indexOf(label);
                            for (List<Object> value : series.getValues()) {
                                sum = sum.add(new BigDecimal(value.get(columnsIndex).toString()));
                            }
                        }
                    }
                    BigDecimal agc = sum.divide(new BigDecimal(currentResult.getSeries().size()), 4, RoundingMode.HALF_UP);
                    JOptionPane.showMessageDialog(null, agc, I18NUtil.getString("MainWindows.search.cmd.result"), JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null, I18NUtil.getString("MainWindows.search.cmd.result") + exception.getMessage());
            }
        });
        return averageItem;
    }

    private JMenuItem getExportItem(ExecuteView executeView) {
        JMenuItem exportItem = new JMenuItem(I18NUtil.getString("MainWindows.search.cmd.export"));
        exportItem.addActionListener(e -> {
            QueryResult.Result currentResult = executeView.getCurrentResult();
            if (currentResult == null) {
                return;
            }
            List<QueryResult.Series> series = currentResult.getSeries();
            if (series == null || series.isEmpty()) {
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter;
            boolean single = series.size() == 1;
            if (single) {
                filter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");
            } else {
                filter = new FileNameExtensionFilter("Zip files (*.zip)", "zip");
            }
            fileChooser.setFileFilter(filter);
            int userSelection = fileChooser.showSaveDialog(null);
            String end = single ? ".csv" : ".zip";
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith(end)) {
                    filePath += end;
                }
                if (single) {
                    exportSingleSeriesToCSV(series.get(0), filePath);
                } else {
                    exportMultipleSeriesToZip(series, filePath);
                }
            }
        });
        return exportItem;
    }

    private void exportMultipleSeriesToZip(List<QueryResult.Series> series, String filePath) {
        Path path = Paths.get(filePath);
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(path))) {
            for (QueryResult.Series dd : series) {
                Path temp = Paths.get(path.getParent().toString(), dd.getName() + ".csv");
                String csvFilePath = temp.toString();
                exportSingleSeriesToCSV(dd, csvFilePath);
                addFileToZip(zipOut, csvFilePath);
                // 删除临时CSV文件
                Files.delete(temp);
            }
        } catch (IOException e) {
            LOG.ERROR.msg(e.getMessage(), e);
        }
    }

    private void addFileToZip(ZipOutputStream zipOut, String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            ZipEntry zipEntry = new ZipEntry(new File(filePath).getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.closeEntry();
        }
    }

    private void exportSingleSeriesToCSV(QueryResult.Series series, String filePath) {
        try (FileWriter csvWriter = new FileWriter(filePath)) {
            // 写入列名
            List<String> columnNames = series.getColumns();
            for (int i = 0; i < columnNames.size(); i++) {
                csvWriter.append(columnNames.get(i));
                if (i != columnNames.size() - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\n");
            // 写入数据行
            for (List<Object> row : series.getValues()) {
                for (int i = 0; i < row.size(); i++) {
                    csvWriter.append(row.get(i).toString());
                    if (i != row.size() - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");
            }
        } catch (IOException e) {
            LOG.ERROR.msg(e.getMessage(), e);
        }
    }
}
