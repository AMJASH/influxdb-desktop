package website.yuanhui.model.influxdb.client;

public class Database {
    private final String name;

    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
