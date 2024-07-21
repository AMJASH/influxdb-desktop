package website.yuanhui.model.influxdb.client;

import org.influxdb.dto.QueryResult;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface IClient {
    List<String> dbNames() throws NoSuchAlgorithmException, KeyManagementException;

    List<String> measurementNames(String dbName) throws NoSuchAlgorithmException, KeyManagementException;

    QueryResult query(String cmd, String dbName) throws NoSuchAlgorithmException, KeyManagementException;
}
