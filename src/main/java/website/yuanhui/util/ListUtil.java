package website.yuanhui.util;

import java.util.Collection;

public class ListUtil {
    public ListUtil() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
}
