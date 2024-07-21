package website.yuanhui.model;

import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;
import website.yuanhui.model.log.LOG;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteModel {
    private final static String DRIVER_CLASS = "org.sqlite.JDBC";
    private final static String JDBC_URL = "jdbc:sqlite:sqlite";
    private final static String USER = "root";
    private final static String PASSWORD = "root";

    static {
        try {
            Class.forName(DRIVER_CLASS);
            createTablesIfNotExists();
        } catch (Exception e) {
            LOG.ERROR.msg("初始化SQLite失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void createTablesIfNotExists() {
        String createConnectInfo = "CREATE TABLE IF NOT EXISTS CONN_INFO (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT," + "NAME VARCHAR(255) NOT NULL," + "URI VARCHAR(500) NOT NULL," + "USERNAME VARCHAR(255) NOT NULL," + "PASSWORD VARCHAR(255) NOT NULL," + "SSL BOOLEAN NOT NULL)";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createConnectInfo);
        } catch (SQLException e) {
            LOG.ERROR.msg("创建连接信息表失败", e);
        }
    }

    public static void delete(Long id) {
        String sql = "DELETE FROM CONN_INFO WHERE ID = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                LOG.INFO.msg("删除连接信息成功");
            } else {
                LOG.INFO.msg("删除连接信息失败 No record found with the given ID");
            }
        } catch (Exception e) {
            LOG.ERROR.msg("删除连接信息失败", e);
        }
    }


    public static void save(ConnectInfoV1 info) {
        String sql = "INSERT INTO CONN_INFO (NAME, URI, USERNAME, PASSWORD, SSL) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, info.getName());
            pstmt.setString(2, info.getUri());
            pstmt.setString(3, info.getUsername());
            pstmt.setString(4, info.getPassword());
            pstmt.setBoolean(5, info.isSsl());
            pstmt.executeUpdate();

            try (PreparedStatement idStmt = conn.prepareStatement("SELECT last_insert_rowid()");
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    info.setId(rs.getLong(1)); // 获取并设置自增ID
                }
            }
        } catch (Exception e) {
            LOG.ERROR.msg("保存连接信息失败", e);
        }
    }

    public static List<ConnectInfoV1> findAll() {
        List<ConnectInfoV1> connectInfos = new ArrayList<>();
        String sql = "SELECT * FROM CONN_INFO";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ConnectInfoV1 info = new ConnectInfoV1();
                info.setId(rs.getLong("ID"));
                info.setName(rs.getString("NAME"));
                info.setUri(rs.getString("URI"));
                info.setUsername(rs.getString("USERNAME"));
                info.setPassword(rs.getString("PASSWORD"));
                info.setSsl(rs.getBoolean("SSL"));
                connectInfos.add(info);
            }
        } catch (SQLException e) {
            LOG.ERROR.msg("获取连接信息失败", e);
        }
        return connectInfos;
    }
}
