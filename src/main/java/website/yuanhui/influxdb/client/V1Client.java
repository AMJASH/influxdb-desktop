
package website.yuanhui.influxdb.client;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import website.yuanhui.action.ConnectionInfo;
import website.yuanhui.util.InfluxDbUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class V1Client implements InfluxDbUtil.DBClient {
    private final InfluxDB connect;

    public V1Client(ConnectionInfo info) {
        if (info.getUrl().startsWith("https") && !Boolean.parseBoolean(info.getSsl())) {
            OkHttpClient.Builder target = new OkHttpClient.Builder();
            try {
                X509TrustManager trustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                target.sslSocketFactory(sslSocketFactory, trustManager).retryOnConnectionFailure(true).hostnameVerifier((hostname, session) -> true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.connect = InfluxDBFactory.connect(info.getUrl(), info.getUsername(), info.getPassword(), target);
        } else {
            this.connect = InfluxDBFactory.connect(info.getUrl(), info.getUsername(), info.getPassword());
        }
    }

    public void close() {
        this.connect.close();
    }

    @SuppressWarnings("unchecked")
    public <T> T query(String db, String cmd) {
        return (T) this.connect.query(new Query(cmd, db));
    }

    public void execute(String db, String cmd) {
        this.connect.write(db, "autogen", ConsistencyLevel.ONE, cmd);
    }

    public List<String> dbNames() {
        QueryResult queryResult = this.connect.query(new Query("show databases"));
        List<QueryResult.Result> queryResults = queryResult.getResults();
        List<String> target = new ArrayList<>();
        for (QueryResult.Result queryRR : queryResults) {
            List<QueryResult.Series> seriesList = queryRR.getSeries();

            for (QueryResult.Series series : seriesList) {
                List<List<Object>> values = series.getValues();
                for (List<Object> objects : values) {
                    target.addAll(objects.stream().map(Objects::toString).collect(Collectors.toList()));
                }
            }
        }
        return target;
    }
}
