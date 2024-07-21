package website.yuanhui.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18NUtil {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static String getString(String key) {
        return messages.getString(key);
    }
}
