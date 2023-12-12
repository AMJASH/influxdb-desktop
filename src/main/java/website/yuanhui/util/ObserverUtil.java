
package website.yuanhui.util;

import java.util.Optional;

public class ObserverUtil {
    public ObserverUtil() {
    }

    public static <T> T cast(Class<T> clz, Object o) {
        if (o == null) {
            return null;
        } else if (clz.isInstance(o)) {
            return clz.cast(o);
        } else {
            return o instanceof Optional && ((Optional)o).isPresent() ? cast(clz, ((Optional)o).get()) : null;
        }
    }
}
