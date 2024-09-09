package website.yuanhui.model.log;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Frame;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public enum LOG {
    DEBUG,
    INFO,
    WARN,
    ERROR;


    private static final JTextArea textArea = new JTextArea();
    private static final JDialog logDialog = new JDialog((Frame) null, "Log Panel", false);

    public void msg(String msg) {
        this.msg(msg, null);
    }
    private static int appId = 0;

    static {
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        logDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        logDialog.setSize(600, 400);
        logDialog.setResizable(true); // 允许调整大小
        logDialog.add(scrollPane);
        logDialog.setVisible(true);
    }

    private static void appendJvmInfoToLog() {
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        textArea.append("JVM Name: " + System.getProperty("java.vm.name") + "\n");
        textArea.append("JVM Version: " + System.getProperty("java.version") + "\n");
        textArea.append("JVM Vendor: " + System.getProperty("java.vm.vendor") + "\n");
        textArea.append("Java Home: " + System.getProperty("java.home") + "\n");
        textArea.append("Operating System: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n");
        textArea.append("Arguments: " + runtimeMxBean.getInputArguments() + "\n");
        textArea.append("Uptime: " + runtimeMxBean.getUptime() + " ms\n");
        textArea.setCaretPosition(textArea.getDocument().getLength()); // 滚动到底部
    }

    public void msg(String msg, Exception e) {
        if (appId == 0) {
            appendJvmInfoToLog();
            appId = 1;
        }
        textArea.append(msg + "\n");
        if (e != null) {
            textArea.append(getStackTraceAsString(e));
        }
        textArea.setCaretPosition(textArea.getDocument().getLength()); // 滚动到底部

    }

    private String getStackTraceAsString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMessage()).append("\n\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}