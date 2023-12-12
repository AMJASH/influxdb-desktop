
package website.yuanhui.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

public class CmdUtil {
    public CmdUtil() {
    }

    public static <T> Object doCmd(ActionEvent event, ActionListener listener) {
        System.out.println(event);
        Object source = event.getSource();

        try {
            if (source instanceof Component) {
                String name = ((Component)source).getName();
                Method method = listener.getClass().getMethod(name, ActionEvent.class);
                return method.invoke(listener, event);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            System.out.println(var5);
        }

        return null;
    }
}
