
package website.yuanhui.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.influxdb.dto.QueryResult;
import website.yuanhui.action.ConnectionInfo;
import website.yuanhui.global.GlobalVar;
import website.yuanhui.influxdb.client.V1Client;

public class InfluxDbUtil {
    private static ConnectionInfo currentConnInfo = null;
    private static DBClient client = null;

    public InfluxDbUtil() {
    }

    public static List<String> dbNames() {
        ConnectionInfo connectionInfo = GlobalVar.getConnectionInfo();
        if (connectionInfo == null) {
            currentConnInfo = null;
            return Collections.emptyList();
        } else {
            if (currentConnInfo != connectionInfo) {
                initDBClient(connectionInfo);
                currentConnInfo = connectionInfo;
            }

            return client.dbNames();
        }
    }

    public static String version(String urlStr) {
        HttpURLConnection connection = null;
        String version = "";

        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.connect();
            version = connection.getHeaderField("X-Influxdb-Version");
        } catch (IOException var7) {
            var7.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }

        return version;
    }

    static void initDBClient(ConnectionInfo info) {
        if (client != null) {
            client.close();
        }

        String version = version(info.getUrl());
        if (version.startsWith("1")) {
            client = new V1Client(info);
        }

    }

    private static boolean isQuery(String cmd) {
        if (cmd == null) {
            return false;
        } else {
            String[] cmds = "SELECT,DELETE,SHOW,CREATE,DROP,EXPLAIN,GRANT,REVOKE,ALTER,SET,KILL".split(",");
            String cmdHead = cmd.trim().toUpperCase();
            String[] var3 = cmds;
            int var4 = cmds.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String s = var3[var5];
                if (cmdHead.startsWith(s)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static <T> T execute(String cmd) {
        String db = GlobalVar.getDb();
        if (isQuery(cmd)) {
            return client.query(db, cmd);
        } else {
            client.execute(db, cmd);
            return null;
        }
    }

    public static List<String> toList(QueryResult r) {
        if (r == null) {
            return Collections.emptyList();
        } else {
            List<QueryResult.Result> results = r.getResults();
            return ListUtil.isEmpty(results) ? Collections.emptyList() : (List)results.stream().map(QueryResult.Result::getSeries).filter(Objects::nonNull).flatMap(Collection::stream).map(QueryResult.Series::getValues).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
        }
    }

    public interface DBClient {
        void close();

        <T> T query(String var1, String var2);

        void execute(String var1, String var2);

        List<String> dbNames();
    }
}
