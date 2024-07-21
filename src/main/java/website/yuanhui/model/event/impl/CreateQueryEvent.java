package website.yuanhui.model.event.impl;

import website.yuanhui.model.event.AbsEvent;
import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;

public class CreateQueryEvent extends AbsEvent<String> {
    private final boolean execute;
    private final ConnectInfoV1 path;
    private final String database;

    public CreateQueryEvent(String source, ConnectInfoV1 path, String database, boolean execute) {
        super(source);
        this.execute = execute;
        this.path = path;
        this.database = database;
    }

    public CreateQueryEvent(String source, ConnectInfoV1 info, String database) {
        this(source, info, database, false);
    }

    public boolean isExecute() {
        return execute;
    }

    public ConnectInfoV1 info() {
        return path;
    }

    public String database() {
        return database;
    }
}
