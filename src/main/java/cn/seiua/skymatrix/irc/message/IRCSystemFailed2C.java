package cn.seiua.skymatrix.irc.message;

/**
 * 不需要处理用JSON自动转换的消息
 */
public class IRCSystemFailed2C extends IRCPacket<IRCSystemFailed2C> {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
