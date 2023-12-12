
package website.yuanhui.global;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import website.yuanhui.action.ConnectionInfo;

public class GlobalVar {
    private static final List<Observer> observable = new ArrayList<>();
    private static volatile ConnectionInfo connectionInfo = null;
    private static volatile String db = "";

    public GlobalVar() {
    }

    public static void add(Observer o) {
        observable.add(o);
    }

    public static void notifyObservers(Object source) {
        for (Observer observer : observable) {
            observer.update(null, source);
        }
    }

    public static String getDb() {
        return db;
    }

    public static void setDb(String db) {
        GlobalVar.db = db;
    }

    public static ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public static void setConnectionInfo(ConnectionInfo connectionInfo) {
        GlobalVar.connectionInfo = connectionInfo;
        setDb("");
    }
}
