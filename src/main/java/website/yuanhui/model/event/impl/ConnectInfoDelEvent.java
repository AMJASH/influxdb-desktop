package website.yuanhui.model.event.impl;

import website.yuanhui.model.event.AbsEvent;
import website.yuanhui.model.influxdb.client.v1.client.ConnectInfoV1;

public class ConnectInfoDelEvent extends AbsEvent<ConnectInfoV1> {
    public ConnectInfoDelEvent(ConnectInfoV1 source) {
        super(source);
    }
}
