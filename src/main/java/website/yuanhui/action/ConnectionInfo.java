
package website.yuanhui.action;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import website.yuanhui.util.JDBCUtil;

public class ConnectionInfo {
    private String username;
    private String password;
    private String url;
    private String name;

    public ConnectionInfo() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static List<ConnectionInfo> load() {
        return JDBCUtil.query("SELECT * FROM CONN_INFO", ConnectionInfo::handler);
    }

    private static List<ConnectionInfo> handler(ResultSet result) {
        List<ConnectionInfo> target = new ArrayList<>();
        try {
            while(result.next()) {
                ConnectionInfo record = new ConnectionInfo();
                record.setName(result.getString("NAME"));
                record.setPassword(result.getString("PASSWORD"));
                record.setUsername(result.getString("USER_NAME"));
                record.setUrl(result.getString("URL"));
                target.add(record);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return target;
    }
}
