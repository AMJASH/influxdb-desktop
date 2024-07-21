package website.yuanhui.util;

public class ClassUtil {
    public static <T> T cast(Object o, Class<T> clz) {
        if (clz.isInstance(o)) {
            return (T) o;
        }
        return null;
    }
}
