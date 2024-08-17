package cn.seiua.skymatrix.irc.message;

public class IRCPacket<C> {
    private String type;
    private C data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public C getData() {
        return data;
    }

    public void setData(C data) {
        this.data = data;
    }
}
