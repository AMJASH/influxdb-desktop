package website.yuanhui.model.event.impl;

import website.yuanhui.model.event.AbsEvent;

public class CmdRunEvent extends AbsEvent<Void> {
    public CmdRunEvent(Void source) {
        super(source);
    }
}
