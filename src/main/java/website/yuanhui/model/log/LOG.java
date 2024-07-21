package website.yuanhui.model.log;

public enum LOG {
    DEBUG,
    INFO,
    WARN,
    ERROR;


    public void msg(String msg, Exception e) {
        System.out.println(this.name() + " : " + msg);
        if (e != null) {
            e.printStackTrace();
        }
    }

    public void msg(String msg) {
        this.msg(msg, null);
    }
}