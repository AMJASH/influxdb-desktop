package website.yuanhui.model.event;

public interface EventListener {
    <T> void apply(Event<T> event);
}