package server.repositories;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import server.models.Message;
import server.proxies.MessageProxy;

public class MessageRepository {
    private BufferedOutputStream bos;

    private String getFileUploadMessage(String sender, String receiver) {
        StringBuilder builder = new StringBuilder();
        builder.append("data").append("/").append("conversation").append("/");
        builder.append(sender).append("_").append(receiver);

        File uploadFile = new File(builder.toString());
        if (!uploadFile.exists()) {
            builder = new StringBuilder();
            builder.append("data").append("/").append("conversation").append("/");
            builder.append(receiver).append("_").append(sender);
        }
        return builder.toString();
    }

    public void saveMessage(Message message) {
        try {
            String fileUploadMessage = getFileUploadMessage(message.getSender(), message.getReceiver());
            File file = new File(fileUploadMessage);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, true));
            MessageProxy messageProxy = new MessageProxy(bos);
            
            messageProxy.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Message> loadMessages(String sender, String receiver) {
        List<Message> messages = new ArrayList<>();
        try {
            String fileUploadMessage = getFileUploadMessage(sender, receiver);
            File file = new File(fileUploadMessage);
            if (!file.exists()) {
                return messages;
            }

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] lengthBuffer = new byte[4]; 
            int bytesRead;
            
            while ((bytesRead = bis.read(lengthBuffer)) != -1) {
                int totalBytesRead = bytesRead; 

                // Reading length of message
                while (totalBytesRead < 4) { 
                    bytesRead = bis.read(lengthBuffer, totalBytesRead, 4 - totalBytesRead); 
                    if (bytesRead == -1) { 
                        System.out.println("Stream closed prematurely");
                        throw new IOException("Stream closed prematurely"); 
                    } 
                    totalBytesRead += bytesRead; 
                } 

                int messageLength = ByteBuffer.wrap(lengthBuffer).getInt(); 
                byte[] messageBuffer = new byte[messageLength]; 
                totalBytesRead = 0; 
                while (totalBytesRead < messageLength) { 
                    bytesRead = bis.read(messageBuffer, totalBytesRead, messageLength - totalBytesRead); 
                    if (bytesRead == -1) { 
                        throw new IOException("Stream closed prematurely"); 
                    } 
                    totalBytesRead += bytesRead; 
                } 

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(messageBuffer, 0, messageLength);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                // Handle the received message
                Message message = (Message) objectInputStream.readObject();
                messages.add(message);
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}
