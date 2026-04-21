package lab6.common;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Object data;
    private MessageType type;

    public enum MessageType {
        REQUEST, RESPONSE, ERROR
    }

    public NetworkMessage(Object data, MessageType type) {
        this.data = data;
        this.type = type;
    }

    public Object getData() { return data; }
    public MessageType getType() { return type; }
}