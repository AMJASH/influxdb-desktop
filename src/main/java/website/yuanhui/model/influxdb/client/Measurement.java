package website.yuanhui.model.influxdb.client;

public class Measurement {
    private final String name;


    public Measurement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
