package website.yuanhui.model.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 提供全局的事件发布和消费，注册事件监听器。
 */
public class SwingEventListenerContext {
    private final List<EventListener> eventListeners = new ArrayList<>();
    private final ExecutorService executorService;

    private SwingEventListenerContext() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public static void register(EventListener listener) {
        SingletonHolder.INSTANCE._register(listener);
    }

    public static <T> void publishEvent(Event<T> event) {
        SingletonHolder.INSTANCE._publishEvent(event);
    }

    /**
     * 注册事件监听器
     *
     * @param listener 监听器
     */
    private void _register(EventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * 发布事件
     *
     * @param event 事件对象
     */
    private <T> void _publishEvent(Event<T> event) {
        executorService.execute(() -> {
            for (EventListener eventListener : eventListeners) {
                eventListener.apply(event);
            }
        });
    }

    private static class SingletonHolder {
        private static final SwingEventListenerContext INSTANCE = new SwingEventListenerContext();
    }
}
