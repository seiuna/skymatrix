package cn.seiua.skymatrix.irc.message;

public class IRCMessage2C extends IRCPacket<IRCMessage2C> {

    /**
     * 消息
     */
    private String message;
    /**
     * 给谁？
     */
    private String to;
    /**
     * 用户前缀
     */
    private String prefix;
    /**
     * 来自谁？
     */
    private String from;
    /**
     * 是否为私聊
     */
    private boolean is_private;

    /**
     * 来自服务器的消息
     * @param message 消息
     * @param to 给谁？
     * @param prefix 用户前缀
     * @param from 来自谁？
     * @param is_private 是否为私聊
     */
    public IRCMessage2C(String message, String to, String prefix, String from, boolean is_private) {
        this.message = message;
        this.to = to;
        this.prefix = prefix;
        this.from = from;
        this.is_private = is_private;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isIs_private() {
        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }
}
