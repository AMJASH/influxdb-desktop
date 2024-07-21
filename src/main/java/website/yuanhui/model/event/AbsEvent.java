package website.yuanhui.model.event;

public class AbsEvent<T> implements Event<T> {
    public T source;

    public AbsEvent(T source) {
        this.source = source;
    }

    public T source() {
        return this.source;
    }
}
