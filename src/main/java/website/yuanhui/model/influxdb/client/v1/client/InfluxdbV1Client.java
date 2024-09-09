package website.yuanhui.model.influxdb.client.v1.client;

import okhttp3.OkHttpClient;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import website.yuanhui.model.influxdb.client.IClient;
import website.yuanhui.util.I18NUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class InfluxdbV1Client implements IClient {
    private final ConnectInfoV1 info;

    public InfluxdbV1Client(ConnectInfoV1 info) {
        this.info = info;
    }

    public static InfluxDB getConnect(ConnectInfoV1 info) throws KeyManagementException, NoSuchAlgorithmException {
        OkHttpClient.Builder target = new OkHttpClient.Builder();
        target.readTimeout(3, TimeUnit.MINUTES);
        target.writeTimeout(1, TimeUnit.MINUTES);
        target.connectTimeout(1, TimeUnit.MINUTES);
        target.callTimeout(1, TimeUnit.MINUTES);
        if (info.isSsl()) {
            return InfluxDBFactory.connect(info.getUri(), info.getUsername(), info.getPassword(), target);
        }
        if (info.getUri().startsWith("https")) {
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
            return InfluxDBFactory.connect(info.getUri(), info.getUsername(), info.getPassword(), target);
        } else {
            return InfluxDBFactory.connect(info.getUri(), info.getUsername(), info.getPassword(), target);
        }
    }

    @Override
    public List<String> dbNames() throws NoSuchAlgorithmException, KeyManagementException {
        try (InfluxDB connect = getConnect(info)) {
            QueryResult queryResult = connect.query(new Query("show databases"));
            List<QueryResult.Result> queryResults = queryResult.getResults();
            List<String> target = new ArrayList<>();
            for (QueryResult.Result queryRR : queryResults) {
                List<QueryResult.Series> seriesList = queryRR.getSeries();
                for (QueryResult.Series series : seriesList) {
                    List<List<Object>> values = series.getValues();
                    for (List<Object> objects : values) {
                        target.addAll(objects.stream().map(Objects::toString).toList());
                    }
                }
            }
            return target;
        }
    }

    @Override
    public List<String> measurementNames(String dbName) throws NoSuchAlgorithmException, KeyManagementException {
        try (InfluxDB connect = getConnect(info)) {
            connect.setDatabase(dbName);
            QueryResult queryResult = connect.query(new Query("show measurements"));
            List<QueryResult.Result> queryResults = queryResult.getResults();
            if (queryResults == null) {
                return Collections.singletonList(I18NUtil.getString("option.open.measurements.empty"));
            }
            List<String> target = new ArrayList<>();
            for (QueryResult.Result queryRR : queryResults) {
                List<QueryResult.Series> seriesList = queryRR.getSeries();
                if (seriesList == null) {
                    continue;
                }
                for (QueryResult.Series series : seriesList) {
                    List<List<Object>> values = series.getValues();
                    for (List<Object> objects : values) {
                        target.addAll(objects.stream().map(Objects::toString).toList());
                    }
                }
            }
            if (target.isEmpty()) {
                return Collections.singletonList(I18NUtil.getString("option.open.measurements.empty"));
            }
            return target;
        }
    }

    @Override
    public QueryResult query(String cmd, String dbName) throws NoSuchAlgorithmException, KeyManagementException {
        try (InfluxDB connect = getConnect(info)) {
            connect.setDatabase(dbName);
            return connect.query(new Query(cmd, dbName));
        }
    }
}
