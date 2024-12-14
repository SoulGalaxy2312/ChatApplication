package server.proxies;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import server.models.Message;

public class MessageProxy {
    private BufferedOutputStream bos;

    public MessageProxy(BufferedOutputStream bos) {
        this.bos = bos;
    }

    public void sendMessage(Message message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(message);
        objectOutputStream.flush();


        byte[] messageBytes = byteArrayOutputStream.toByteArray(); 
        int messageLength = messageBytes.length; 
        byte[] lengthBytes = ByteBuffer.allocate(4).putInt(messageLength).array(); 
        bos.write(lengthBytes);         // Length of message
        bos.write(messageBytes);        // Message
        bos.flush();                    
    }
}
