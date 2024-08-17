package cn.seiua.skymatrix.irc.message;

public class IRCMessage2S extends IRCPacket<IRCMessage2S> {

    private String message;
    private String to;
    private boolean isPrivate;

    /**
     *
     * @param message 要发送的消息
     * @param isPrivate 如果为false，to 则无效
     * @param to 如果是私聊，to为私聊对象的用户名 如果为null则无效
     */
    public IRCMessage2S(String message, boolean isPrivate,String to) {
        this.setType("message");
        this.message = message;
        this.isPrivate = isPrivate;
        this.to = to;
        this.setData(this);
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
