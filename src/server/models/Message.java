package server.models;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

// enum MessageType {
//     TEXT,
//     FILE
// };

public class Message implements Serializable {
    private String sender;
    private String receiver;
    private MessageType type;
    private byte[] content;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content != null ? content.clone() : null;
    }

    public String getContentAsString() { 
        if (type == MessageType.TEXT) {
            return new String(content, StandardCharsets.UTF_8); 
        } 
        return null; 
    }

    public void setContentFromString(String contentString) { 
        this.content = contentString.getBytes(StandardCharsets.UTF_8); 
    }

    @Override
    public String toString() {
        return "Sender: " + this.sender + "\n"
                + "Receiver: " + this.receiver + "\n" 
                + "Content: " + (this.type == MessageType.TEXT ? this.getContentAsString() : this.content.toString());
    }
}

