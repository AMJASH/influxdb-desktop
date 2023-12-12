
package website.yuanhui.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import website.yuanhui.action.ConnectionInfo;

public class JDBCUtil {
    static String DRIVER_CLASS = "org.sqlite.JDBC";
    static String JDBC_URL = "jdbc:sqlite:sqliteDB.db";
    static String USER = "root";
    static String PASSWORD = "root";

    public JDBCUtil() {
    }

    public static Connection open() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER_CLASS);
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static <T> List<T> query(String sql, Function<ResultSet, List<T>> handler) {
        try {
            Connection open = open();
            Statement statement = open.createStatement();
            ResultSet result = statement.executeQuery(sql);
            List<T> apply = (List)handler.apply(result);
            result.close();
            statement.close();
            open.close();
            return apply;
        } catch (Exception var6) {
            var6.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static boolean execute(String sql) {
        try {
            Connection open = open();
            Statement statement = open.createStatement();
            boolean result = statement.execute(sql);
            statement.close();
            open.close();
            return result;
        } catch (Exception var4) {
            System.err.println(sql);
            var4.printStackTrace();
            return false;
        }
    }

    public static void createTables() {
        String createConnectInfo = "CREATE TABLE IF NOT EXISTS CONN_INFO (NAME VARCHAR(50) NOT NULL, USER_NAME VARCHAR(50) NOT NULL,PASSWORD VARCHAR(50) NOT NULL, URL VARCHAR(500) NOT NULL)";
        execute(createConnectInfo);
    }

    public static void save(ConnectionInfo info) {
        String sql = "INSERT INTO CONN_INFO VALUES ('" + info.getName() + "','" + info.getUsername() + "','" + info.getPassword() + "','" + info.getUrl() + "')";

        try {
            execute(sql);
        } catch (Exception var3) {
            System.err.println(sql);
            var3.printStackTrace();
        }

    }

    static {
        createTables();
    }
}
