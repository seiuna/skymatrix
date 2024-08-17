package cn.seiua.skymatrix.irc.message;

public class IRCHeartBeat2S extends IRCPacket<IRCHeartBeat2S> {
    private int flag;

    /**
     * 服务器每2秒会向你发送一次心跳包，你需要根据服务器提供的flag进行回复 flag+1的心跳包 如果服务器达到10次没有收到你的心跳包，服务器就会断开连接 (currentFlag-ws.flag>10)
     * @param flag 你提供的flag
     */
    public IRCHeartBeat2S(int flag) {
        this.setType("heart_beat");
        this.flag = flag;
        this.setData(this);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
