package website.yuanhui.util;

import org.influxdb.dto.QueryResult;
import website.yuanhui.action.ConnectionInfo;
import website.yuanhui.global.GlobalVar;
import website.yuanhui.influxdb.client.V1Client;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static String version(ConnectionInfo info) {
        final String url = info.getUrl();
        if (!url.startsWith("https")) {
            return http(info.getUrl());
        }
        boolean ssl = Boolean.parseBoolean(info.getSsl());
        return ssl ? http(info.getUrl()) : igSSl(info.getUrl());
    }

    public static String igSSl(String urlStr) {
        //设置可通过ip地址访问https请求
        TrustManager[] tm = {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tm, new java.security.SecureRandom());
            URL url = new URL(urlStr);
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setHostnameVerifier((hostname, session) -> true);
            con.setSSLSocketFactory(ssf);
            return getVersionHeader(con);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getVersionHeader(HttpURLConnection con) throws IOException {
        if (con == null) {
            return "";
        }
        con.setRequestMethod("GET");
        con.setReadTimeout(15000);
        con.connect();
        final String headerField = con.getHeaderField("X-Influxdb-Version");
        con.disconnect();
        return headerField;
    }

    private static String http(String urlStr) {
        try {
            return getVersionHeader((HttpURLConnection) new URL(urlStr).openConnection());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void initDBClient(ConnectionInfo info) {
        if (client != null) {
            client.close();
        }
        String version = version(info);
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

            for (int var5 = 0; var5 < var4; ++var5) {
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
            return ListUtil.isEmpty(results) ? Collections.emptyList() : (List) results.stream().map(QueryResult.Result::getSeries).filter(Objects::nonNull).flatMap(Collection::stream).map(QueryResult.Series::getValues).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
        }
    }

    public interface DBClient {
        void close();

        <T> T query(String var1, String var2);

        void execute(String var1, String var2);

        List<String> dbNames();
    }

}
